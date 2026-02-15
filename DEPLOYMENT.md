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

Open: `http://localhost`

## Release Gate
Windows PowerShell:
```
scripts/release-gate.ps1
```

Git Bash:
```
scripts/release-gate.sh
```
