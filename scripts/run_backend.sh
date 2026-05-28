#!/bin/bash
# Run Backend (Spring Boot)
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

echo "🔧 Starting infrastructure (MySQL + Redis)..."
cd "$PROJECT_DIR/backend"
docker compose up -d mysql redis

echo "⏳ Waiting for MySQL to be ready..."
until docker exec mysql-db-livehealth mysql -uroot -proot123 -e "SELECT 1" &>/dev/null; do
  sleep 1
done
echo "✅ MySQL ready"

echo "☕ Starting Spring Boot backend..."
source "$HOME/.sdkman/bin/sdkman-init.sh"
cd "$PROJECT_DIR/backend"
mvn spring-boot:run
