@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "PROJECT_DIR=%SCRIPT_DIR%.."

echo [*] Stopping all LiveHealth services...

echo.
echo [*] Stopping Backend (port 62080)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :62080 ^| find "LISTENING"') do taskkill /f /pid %%a 2>nul

echo [*] Stopping Frontend (port 62173)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :62173 ^| find "LISTENING"') do taskkill /f /pid %%a 2>nul

echo [*] Stopping AI Service (port 62000)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :62000 ^| find "LISTENING"') do taskkill /f /pid %%a 2>nul

echo.
echo [*] Stopping Docker containers...
cd /d "%PROJECT_DIR%\backend"
docker compose down 2>nul

echo [+] All services stopped.
