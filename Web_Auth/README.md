# Web_Auth - Security Lab Authentication

This project implements the lab requirements from `7_ITS_Lab_Authentication.pdf`:

- Registration with email/password
- Login/logout
- Optional social login (Google OAuth)
- Per-user nickname storage and update
- PostgreSQL persistence

## Security relevant behavior

- Passwords are hashed with `bcrypt` (`salt rounds = 12`) before storage.
- Session IDs are stored server-side in PostgreSQL (`connect-pg-simple`).
- Nickname updates are only possible for the currently authenticated user (`WHERE id = req.user.id`).
- SQL statements use parameterized queries.

## Tech stack

- Node.js + Express + EJS
- Passport (`passport-local`, `passport-google-oauth20`)
- PostgreSQL (`pg`)

## 1) Configure environment

Copy `.env.example` to `.env` and adjust values:

```bash
cp .env.example .env
```

Important variables:

- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`
- `SESSION_SECRET` (use a long random value)
- `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, `GOOGLE_CALLBACK_URL` (optional)

If Google OAuth values are empty, local auth still works and social login is simply disabled.

## 2) Start PostgreSQL

### Option A - Docker (quickest)

```bash
docker compose up -d
```

### Option B - Native PostgreSQL on Linux VM

Create a database and user matching your `.env`.

## 3) Install and run app

```bash
npm install
npm run dev
```

or:

```bash
npm start
```

Open:

- `http://localhost:3000/login`

## Google social login setup

1. Create OAuth credentials in Google Cloud Console.
2. Add callback URL:
   - `http://localhost:3000/auth/google/callback`
3. Put client ID/secret in `.env`.
4. Restart the app.

## Deploying later with external VM IP

When you move from localhost to your VM IP/domain:

- update `GOOGLE_CALLBACK_URL`
- set secure cookie settings behind HTTPS (`cookie.secure = true`)
- use a strong `SESSION_SECRET`
- configure firewall/reverse proxy as needed
