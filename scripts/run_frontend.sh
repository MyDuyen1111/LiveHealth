#!/bin/bash
# Run Frontend (React + Vite)
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

cd "$PROJECT_DIR/frontend"

if [ ! -d "node_modules" ]; then
  echo "📦 Installing frontend dependencies..."
  npm install
fi

echo "⚛️  Starting React frontend..."
npm run dev -- --port 62173 --strictPort
