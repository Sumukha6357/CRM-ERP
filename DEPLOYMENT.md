# Deployment

## Required Environment Variables
Create a `.env.prod` from `.env.prod.example` at repo root:
- `DB_USER`
- `DB_PASS`
- `JWT_SECRET`

## Production Compose
```
docker compose -f docker-compose.prod.yml --env-file .env.prod up -d --build
```

Open app directly: `http://localhost:3000`
Open API directly: `http://localhost:8080`

Note: This repo no longer ships Nginx config. Use your server-managed Nginx to reverse proxy to these ports.

## Release Gate
Windows PowerShell:
```
scripts/release-gate.ps1
```

Git Bash:
```
scripts/release-gate.sh
```
