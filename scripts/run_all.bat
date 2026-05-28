@echo off
setlocal DisableDelayedExpansion

set "SCRIPT_DIR=%~dp0"
set "PROJECT_DIR=%SCRIPT_DIR%.."

echo =================================================
echo   LiveHealth - Starting All Services
echo =================================================

echo [1/3] Starting MySQL + Redis...
cd /d "%PROJECT_DIR%\backend"
docker compose up -d mysql redis
if errorlevel 1 docker compose up -d

for /f "tokens=1* delims==" %%A in ('type "%PROJECT_DIR%\backend\.env" ^| findstr "^MYSQL_ROOT_PASSWORD="') do set "DB_PASS=%%B"
if not defined DB_PASS set "DB_PASS=root123"

echo Waiting for MySQL...
:wait_mysql
docker exec mysql-db-livehealth mysql -uroot -p%DB_PASS% -e "SELECT 1" >nul 2>&1
if errorlevel 1 (
  docker exec livehealth-mysql mysql -uroot -p%DB_PASS% -e "SELECT 1" >nul 2>&1
  if errorlevel 1 (
    timeout /t 2 /nobreak >nul
    goto wait_mysql
  )
)
echo OK: MySQL ready

echo [2/3] Starting Spring Boot backend in new window...
start "LiveHealth Backend" /d "%PROJECT_DIR%\backend" cmd /c "mvn spring-boot:run"

for /f "tokens=1* delims==" %%A in ('type "%PROJECT_DIR%\backend\.env" ^| findstr "^SPRING_SERVER_PORT="') do set "API_PORT=%%B"
if not defined API_PORT set "API_PORT=62080"

echo Waiting for backend to be ready (Port %API_PORT%)...
:wait_backend
timeout /t 2 /nobreak >nul
curl -s -f http://localhost:%API_PORT%/swagger-ui/index.html >nul 2>&1
if errorlevel 1 goto wait_backend
echo OK: Backend ready

echo [3/3] Starting React frontend in new window...
cd /d "%PROJECT_DIR%\frontend"
if not exist "node_modules\" call npm install
start "LiveHealth AI" /d "%PROJECT_DIR%\ai-service" cmd /c "set AI_SERVICE_PORT=62000&& python main.py"

start "LiveHealth Frontend" /d "%PROJECT_DIR%\frontend" cmd /c "npm run dev -- --port 62173 --strictPort"

echo =================================================
echo   All services are now running!
echo   Frontend: http://localhost:62173
echo   Backend:  http://localhost:%API_PORT%
echo   AI:       http://localhost:62000
echo   To stop all, run scripts\stop_all.bat
echo =================================================
