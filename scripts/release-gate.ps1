$ErrorActionPreference = "Stop"

Write-Host "Running backend checks..."
Push-Location "api/ggos-crm"
docker compose up -d
.\mvnw.cmd test
.\mvnw.cmd -DskipTests package
Pop-Location

Write-Host "Running frontend checks..."
Push-Location "web"
if (Test-Path "package-lock.json") {
  npm ci
} else {
  npm install
}
npm test
npm run build
Pop-Location

Write-Host "Building production compose..."
docker compose -f docker-compose.prod.yml build

Write-Host "Release gate complete."
