@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "PROJECT_DIR=%SCRIPT_DIR%.."
set "AI_DIR=%PROJECT_DIR%\ai-service"

echo [*] Starting LiveHealth AI Service...

:: Try to activate conda environment
call conda activate livehealth-ai 2>nul
if errorlevel 1 (
  echo [*] Creating conda env 'livehealth-ai'...
  call conda create -n livehealth-ai python=3.11 -y
  call conda activate livehealth-ai
)

echo [*] Installing dependencies...
if exist "%AI_DIR%\requirements.txt" (
  pip install -r "%AI_DIR%\requirements.txt" -q
)

echo [+] Starting FastAPI on port 62000...
cd /d "%AI_DIR%"
set AI_SERVICE_PORT=62000
python main.py
