@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "PROJECT_DIR=%SCRIPT_DIR%.."

echo [*] Starting infrastructure (MySQL + Redis)...
cd /d "%PROJECT_DIR%\backend"
docker compose up -d

for /f "tokens=1* delims==" %%A in ('type "%PROJECT_DIR%\backend\.env" ^| findstr "^MYSQL_ROOT_PASSWORD="') do set "DB_PASS=%%B"
if not defined DB_PASS set "DB_PASS=root123"

echo [*] Waiting for MySQL to be ready...
:wait_mysql
docker exec livehealth-mysql mysql -uroot -p%DB_PASS% -e "SELECT 1" >nul 2>&1
if errorlevel 1 (
  docker exec mysql-db-livehealth mysql -uroot -p%DB_PASS% -e "SELECT 1" >nul 2>&1
  timeout /t 2 /nobreak >nul
  goto wait_mysql
)
echo OK: MySQL ready

echo [*] Starting Spring Boot backend...
cd /d "%PROJECT_DIR%\backend"
mvn spring-boot:run
