#!/bin/bash
# Run AI Service (FastAPI + Gemini)
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
AI_DIR="$PROJECT_DIR/ai-service"

echo "🤖 Starting LiveHealth AI Service..."

# Activate conda
source "$HOME/miniconda3/etc/profile.d/conda.sh"

# Create env if needed
if ! conda env list | grep -q "livehealth-ai"; then
  echo "📦 Creating conda env 'livehealth-ai'..."
  conda create -n livehealth-ai python=3.11 -y
fi

conda activate livehealth-ai

# Install deps
echo "📥 Installing dependencies..."
pip install -r "$AI_DIR/requirements.txt" -q

# Run
echo "🚀 Starting FastAPI on port 62000..."
cd "$AI_DIR"
AI_SERVICE_PORT=62000 python main.py
