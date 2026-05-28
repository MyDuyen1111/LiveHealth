@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "PROJECT_DIR=%SCRIPT_DIR%.."

cd /d "%PROJECT_DIR%\frontend"

if not exist node_modules\ (
  echo [*] Installing frontend dependencies...
  call npm install
)

echo [*] Starting React frontend...
call npm run dev
