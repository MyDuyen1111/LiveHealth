#!/bin/bash
# Stop All Services
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

echo "🛑 Stopping all LiveHealth services..."

# Stop Maven/Java
pkill -f "spring-boot:run" 2>/dev/null && echo "✅ Backend stopped" || echo "⚪ Backend was not running"
lsof -tiTCP:62080 -sTCP:LISTEN 2>/dev/null | xargs -r kill 2>/dev/null || true

# Stop Vite
pkill -f "vite" 2>/dev/null && echo "✅ Frontend stopped" || echo "⚪ Frontend was not running"
lsof -tiTCP:62173 -sTCP:LISTEN 2>/dev/null | xargs -r kill 2>/dev/null || true

# Stop AI Service
lsof -tiTCP:62000 -sTCP:LISTEN 2>/dev/null | xargs -r kill 2>/dev/null \
  && echo "✅ AI service stopped" || echo "⚪ AI service was not running"

# Stop Docker containers
cd "$PROJECT_DIR/backend"
docker compose down 2>/dev/null && echo "✅ Docker containers stopped" || echo "⚪ Docker was not running"

echo "🏁 All services stopped."
