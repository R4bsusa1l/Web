const bcrypt = require("bcrypt");
const passport = require("passport");
const LocalStrategy = require("passport-local").Strategy;
const GoogleStrategy = require("passport-google-oauth20").Strategy;
const { pool } = require("./db");

function configurePassport() {
  passport.use(
    new LocalStrategy(
      { usernameField: "email", passwordField: "password" },
      async (email, password, done) => {
        try {
          const result = await pool.query(
            "SELECT * FROM users WHERE email = $1 LIMIT 1",
            [email.toLowerCase()]
          );
          const user = result.rows[0];

          if (!user || !user.password_hash) {
            return done(null, false, { message: "Invalid email or password." });
          }

          const isValid = await bcrypt.compare(password, user.password_hash);
          if (!isValid) {
            return done(null, false, { message: "Invalid email or password." });
          }

          return done(null, user);
        } catch (error) {
          return done(error);
        }
      }
    )
  );

  const googleClientId = process.env.GOOGLE_CLIENT_ID;
  const googleClientSecret = process.env.GOOGLE_CLIENT_SECRET;
  const googleCallbackUrl = process.env.GOOGLE_CALLBACK_URL;

  if (googleClientId && googleClientSecret && googleCallbackUrl) {
    passport.use(
      new GoogleStrategy(
        {
          clientID: googleClientId,
          clientSecret: googleClientSecret,
          callbackURL: googleCallbackUrl,
        },
        async (_accessToken, _refreshToken, profile, done) => {
          try {
            const email = profile.emails?.[0]?.value?.toLowerCase();
            if (!email) {
              return done(null, false, { message: "No email from Google account." });
            }

            const googleId = profile.id;
            const displayName = profile.displayName || email.split("@")[0];

            let result = await pool.query(
              "SELECT * FROM users WHERE google_id = $1 LIMIT 1",
              [googleId]
            );
            let user = result.rows[0];

            if (!user) {
              result = await pool.query(
                "SELECT * FROM users WHERE email = $1 LIMIT 1",
                [email]
              );
              user = result.rows[0];
            }

            if (!user) {
              result = await pool.query(
                `
                INSERT INTO users (email, nickname, provider, google_id)
                VALUES ($1, $2, 'google', $3)
                RETURNING *
                `,
                [email, displayName, googleId]
              );
              user = result.rows[0];
            } else if (!user.google_id) {
              result = await pool.query(
                `
                UPDATE users
                SET google_id = $1, provider = 'google'
                WHERE id = $2
                RETURNING *
                `,
                [googleId, user.id]
              );
              user = result.rows[0];
            }

            return done(null, user);
          } catch (error) {
            return done(error);
          }
        }
      )
    );
  }

  passport.serializeUser((user, done) => {
    done(null, user.id);
  });

  passport.deserializeUser(async (id, done) => {
    try {
      const result = await pool.query("SELECT * FROM users WHERE id = $1 LIMIT 1", [id]);
      done(null, result.rows[0] || false);
    } catch (error) {
      done(error);
    }
  });
}

function isGoogleOAuthEnabled() {
  return Boolean(
    process.env.GOOGLE_CLIENT_ID &&
      process.env.GOOGLE_CLIENT_SECRET &&
      process.env.GOOGLE_CALLBACK_URL
  );
}

module.exports = {
  configurePassport,
  isGoogleOAuthEnabled,
};
