@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "PROJECT_DIR=%SCRIPT_DIR%.."

echo ==============================================================
echo   LiveHealth - Setup Environment
echo ==============================================================

echo [1/5] Checking prerequisites...
where docker >nul 2>&1 || echo ERROR: Docker not found
where java >nul 2>&1 || echo ERROR: Java not found
where mvn >nul 2>&1 || echo ERROR: Maven not found
where node >nul 2>&1 || echo ERROR: Node not found
where npm >nul 2>&1 || echo ERROR: NPM not found
where conda >nul 2>&1 || echo ERROR: Conda not found

echo [2/5] Configuring .env...
if not exist "%PROJECT_DIR%\backend\.env" (
  copy "%PROJECT_DIR%\backend\.env.example" "%PROJECT_DIR%\backend\.env" >nul
  echo OK: Created backend\.env
)

if not exist "%PROJECT_DIR%\ai-service\.env" (
  echo GEMINI_API_KEY=> "%PROJECT_DIR%\ai-service\.env"
  echo MYSQL_HOST=localhost>> "%PROJECT_DIR%\ai-service\.env"
  echo MYSQL_PORT=62307>> "%PROJECT_DIR%\ai-service\.env"
  echo MYSQL_USER=root>> "%PROJECT_DIR%\ai-service\.env"
  echo MYSQL_PASSWORD=root123>> "%PROJECT_DIR%\ai-service\.env"
  echo MYSQL_DATABASE=livehealth>> "%PROJECT_DIR%\ai-service\.env"
  echo OK: Created ai-service\.env
)

echo [3/5] Setup Python (Conda)...
call conda activate livehealth-ai 2>nul
if errorlevel 1 (
  call conda create -n livehealth-ai python=3.11 -y
  call conda activate livehealth-ai
)
if exist "%PROJECT_DIR%\requirements.txt" (
  pip install -r "%PROJECT_DIR%\requirements.txt" -q
) else if exist "%PROJECT_DIR%\ai-service\requirements.txt" (
  pip install -r "%PROJECT_DIR%\ai-service\requirements.txt" -q
)

echo [4/5] Setup Frontend (npm)...
cd /d "%PROJECT_DIR%\frontend"
if not exist "node_modules\" (
  call npm install --silent
)

echo [5/5] Checking Docker...
docker info >nul 2>&1
if errorlevel 1 (
  echo ERROR: Docker is not running. Please start Docker.
) else (
  echo OK: Docker daemon is active.
)

echo ==============================================================
echo   Setup complete!
echo ==============================================================
