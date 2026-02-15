#!/usr/bin/env bash
set -euo pipefail

echo "Running backend checks..."
pushd "api/ggos-crm" >/dev/null
docker compose up -d
./mvnw test
./mvnw -DskipTests package
popd >/dev/null

echo "Running frontend checks..."
pushd "web" >/dev/null
if [ -f package-lock.json ]; then
  npm ci
else
  npm install
fi
npm test
npm run build
popd >/dev/null

echo "Building production compose..."
docker compose -f docker-compose.prod.yml build

echo "Release gate complete."
