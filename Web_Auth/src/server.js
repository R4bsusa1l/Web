require("dotenv").config();

const path = require("path");
const express = require("express");
const session = require("express-session");
const PgStore = require("connect-pg-simple")(session);
const bcrypt = require("bcrypt");
const helmet = require("helmet");
const passport = require("passport");
const rateLimit = require("express-rate-limit");
const csrf = require("csurf");

const { pool } = require("./db");
const { initDb } = require("./initDb");
const { configurePassport, isGoogleOAuthEnabled } = require("./auth");

const app = express();
const PORT = Number(process.env.PORT || 3000);
const googleEnabled = isGoogleOAuthEnabled();
const isProduction = process.env.NODE_ENV === "production";
const sessionSecret = process.env.SESSION_SECRET;

if (!sessionSecret || sessionSecret === "insecure-dev-secret-change-me") {
  throw new Error("SESSION_SECRET is missing or insecure. Set a strong value in .env.");
}

configurePassport();

app.set("view engine", "ejs");
app.set("views", path.join(__dirname, "..", "views"));
if (isProduction) {
  app.set("trust proxy", 1);
}

app.use(express.urlencoded({ extended: false }));
app.use(express.static(path.join(__dirname, "..", "public")));
app.use(helmet());

app.use(
  session({
    store: new PgStore({
      pool,
      tableName: "session",
      createTableIfMissing: true,
    }),
    secret: sessionSecret,
    resave: false,
    saveUninitialized: false,
    rolling: true,
    cookie: {
      httpOnly: true,
      sameSite: "strict",
      secure: isProduction,
      maxAge: 1000 * 60 * 60,
    },
  })
);

app.use(passport.initialize());
app.use(passport.session());
app.use(csrf());
app.use((req, res, next) => {
  res.locals.csrfToken = req.csrfToken();
  next();
});

const authLimiter = rateLimit({
  windowMs: 15 * 60 * 1000,
  max: 20,
  standardHeaders: true,
  legacyHeaders: false,
  message: "Too many auth attempts. Please try again later.",
});

function ensureAuthenticated(req, res, next) {
  if (req.isAuthenticated()) {
    return next();
  }
  return res.redirect("/login");
}

function ensureGuest(req, res, next) {
  if (req.isAuthenticated()) {
    return res.redirect("/profile");
  }
  return next();
}

app.get("/", (req, res) => {
  if (req.isAuthenticated()) {
    return res.redirect("/profile");
  }
  return res.redirect("/login");
});

app.get("/register", ensureGuest, (req, res) => {
  res.render("register", {
    error: req.query.error || "",
    email: req.query.email || "",
    nickname: req.query.nickname || "",
  });
});

app.post("/register", authLimiter, ensureGuest, async (req, res) => {
  const email = (req.body.email || "").trim().toLowerCase();
  const password = req.body.password || "";
  const nickname = (req.body.nickname || "").trim();

  if (!email || !password || !nickname) {
    return res.redirect(
      `/register?error=${encodeURIComponent(
        "Email, password, and nickname are required."
      )}&email=${encodeURIComponent(email)}&nickname=${encodeURIComponent(nickname)}`
    );
  }

  if (password.length < 10) {
    return res.redirect(
      `/register?error=${encodeURIComponent(
        "Password must be at least 10 characters."
      )}&email=${encodeURIComponent(email)}&nickname=${encodeURIComponent(nickname)}`
    );
  }

  try {
    const existing = await pool.query("SELECT id FROM users WHERE email = $1 LIMIT 1", [email]);
    if (existing.rowCount > 0) {
      return res.redirect(
        `/register?error=${encodeURIComponent(
          "Registration failed."
        )}&email=${encodeURIComponent(email)}&nickname=${encodeURIComponent(nickname)}`
      );
    }

    const passwordHash = await bcrypt.hash(password, 12);
    await pool.query(
      `
      INSERT INTO users (email, password_hash, nickname, provider)
      VALUES ($1, $2, $3, 'local')
      `,
      [email, passwordHash, nickname]
    );

    return res.redirect("/login?success=Account created. Please log in.");
  } catch (error) {
    return res.redirect(`/register?error=${encodeURIComponent("Registration failed.")}`);
  }
});

app.get("/login", ensureGuest, (req, res) => {
  res.render("login", {
    error: req.query.error || "",
    success: req.query.success || "",
    googleEnabled,
  });
});

app.post("/login", authLimiter, ensureGuest, (req, res, next) => {
  passport.authenticate("local", (authError, user, info) => {
    if (authError) {
      return next(authError);
    }
    if (!user) {
      const message = info?.message || "Login failed.";
      return res.redirect(`/login?error=${encodeURIComponent(message)}`);
    }

    return req.logIn(user, (loginError) => {
      if (loginError) {
        return next(loginError);
      }
      return res.redirect("/profile");
    });
  })(req, res, next);
});

if (googleEnabled) {
  app.get("/auth/google", ensureGuest, passport.authenticate("google", { scope: ["profile", "email"] }));
  app.get(
    "/auth/google/callback",
    ensureGuest,
    passport.authenticate("google", {
      failureRedirect: "/login?error=Google+login+failed",
    }),
    (_req, res) => {
      res.redirect("/profile");
    }
  );
}

app.get("/profile", ensureAuthenticated, (req, res) => {
  res.render("profile", {
    user: req.user,
    success: req.query.success || "",
    error: req.query.error || "",
  });
});

app.post("/profile/nickname", ensureAuthenticated, async (req, res) => {
  const nickname = (req.body.nickname || "").trim();

  if (!nickname || nickname.length > 40) {
    return res.redirect("/profile?error=Nickname+must+be+between+1+and+40+characters");
  }

  try {
    await pool.query("UPDATE users SET nickname = $1 WHERE id = $2", [nickname, req.user.id]);
    return res.redirect("/profile?success=Nickname+updated");
  } catch (error) {
    return res.redirect("/profile?error=Could+not+update+nickname");
  }
});

app.post("/logout", ensureAuthenticated, (req, res, next) => {
  req.logout((error) => {
    if (error) {
      return next(error);
    }
    req.session.destroy(() => {
      res.clearCookie("connect.sid");
      return res.redirect("/login?success=Logged+out");
    });
  });
});

app.use((_req, res) => {
  res.status(404).render("not-found");
});

app.use((error, _req, res, _next) => {
  if (error.code === "EBADCSRFTOKEN") {
    return res.status(403).send("Invalid CSRF token.");
  }
  // Keep production responses generic to avoid leaking internals.
  console.error(error);
  res.status(500).send("Internal server error.");
});

async function startServer() {
  try {
    await initDb();
    app.listen(PORT, () => {
      console.log(`Web_Auth server running at http://localhost:${PORT}`);
    });
  } catch (error) {
    console.error("Failed to initialize application:", error);
    process.exit(1);
  }
}

startServer();
