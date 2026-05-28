# ============================================================
# Script chay Backend LiveHealth khong can Docker
# Usage: .\start-dev.ps1
# ============================================================

Write-Host "🚀 Khoi dong LiveHealth Backend..." -ForegroundColor Cyan

# 1. Kiem tra Redis
$redisRunning = Get-Process -Name "redis-server" -ErrorAction SilentlyContinue
if ($redisRunning) {
    Write-Host "✅ Redis da chay (PID: $($redisRunning.Id))" -ForegroundColor Green
} else {
    Write-Host "⚡ Khoi dong Redis tren port 62380..." -ForegroundColor Yellow
    Start-Process -FilePath "C:\Program Files\Redis\redis-server.exe" `
        -ArgumentList "--port 62380 --requirepass khuong123" `
        -WindowStyle Minimized
    Start-Sleep -Seconds 2

    # Kiem tra Redis da pong chua
    $ping = & "C:\Program Files\Redis\redis-cli.exe" -p 62380 -a khuong123 ping 2>&1
    if ($ping -eq "PONG") {
        Write-Host "✅ Redis sẵn sàng" -ForegroundColor Green
    } else {
        Write-Host "❌ Redis khoi dong that bai!" -ForegroundColor Red
        exit 1
    }
}

# 2. Chay Spring Boot
Write-Host "⚡ Khoi dong Spring Boot..." -ForegroundColor Yellow
$env:JAVA_HOME = "C:\Program Files\Java\jdk-23"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

$mvn = (Get-ChildItem "C:\Users\$env:USERNAME\.m2\wrapper\dists" -Recurse -Filter "mvn.cmd" |
        Sort-Object FullName -Descending | Select-Object -First 1).FullName

if (-not $mvn) {
    Write-Host "❌ Khong tim thay Maven! Hay mo IntelliJ va build lai." -ForegroundColor Red
    exit 1
}

Write-Host "✅ Dang su dung Maven: $mvn" -ForegroundColor Green
Write-Host "📦 Spring Boot se chay tai: http://localhost:62080" -ForegroundColor Cyan
Write-Host "📖 Swagger UI: http://localhost:62080/swagger-ui/index.html" -ForegroundColor Cyan
Write-Host ""

& $mvn spring-boot:run
