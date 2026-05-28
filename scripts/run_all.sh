#!/bin/bash
# ═══════════════════════════════════════════════════════════
#  LiveHealth — Chạy tất cả services
#  Usage: ./scripts/run_all.sh
#  Ctrl+C để dừng tất cả
# ═══════════════════════════════════════════════════════════
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
FRONTEND_PORT=62173
BACKEND_PORT=62080
AI_PORT=62000
MYSQL_PORT=62307
REDIS_PORT=62380
AUTO_SEED_PRODUCTS="${AUTO_SEED_PRODUCTS:-true}"

# ── Cleanup khi thoát ──
cleanup() {
  echo ""
  echo "🛑 Đang dừng services..."
  [ -n "$FRONTEND_PID" ] && kill $FRONTEND_PID 2>/dev/null
  [ -n "$AI_PID" ]       && kill $AI_PID 2>/dev/null
  [ -n "$BACKEND_PID" ]  && kill $BACKEND_PID 2>/dev/null
  docker compose -f "$PROJECT_DIR/backend/docker-compose.yml" down 2>/dev/null
  echo "✅ Đã dừng tất cả"
}
trap cleanup SIGINT SIGTERM EXIT

echo ""
echo "═══════════════════════════════════════════"
echo "  🌿 LiveHealth — Starting All Services"
echo "═══════════════════════════════════════════"

# ═══ 1. Infrastructure ═══
echo ""
echo "🔧 [1/4] Starting MySQL + Redis..."
cd "$PROJECT_DIR/backend"
docker compose up -d mysql redis 2>/dev/null

echo "⏳ Waiting for MySQL..."
TRIES=0
until docker exec mysql-db-livehealth mysql -uroot -proot123 -e "SELECT 1" &>/dev/null; do
  sleep 1
  TRIES=$((TRIES + 1))
  if [ "$TRIES" -ge 60 ]; then
    echo "❌ MySQL không khởi động được sau 60 giây"
    exit 1
  fi
done
echo "✅ MySQL ready (port ${MYSQL_PORT})"

echo "⏳ Waiting for Redis..."
REDIS_PASSWORD_VAL=""
if [ -f "$PROJECT_DIR/backend/.env" ]; then
  REDIS_PASSWORD_VAL=$(grep -E '^REDIS_PASSWORD=' "$PROJECT_DIR/backend/.env" | head -1 | cut -d= -f2-)
fi
TRIES=0
until docker exec redis-cache-livehealth sh -c "if [ -n \"$REDIS_PASSWORD_VAL\" ]; then redis-cli -a \"$REDIS_PASSWORD_VAL\" ping; else redis-cli ping; fi" 2>/dev/null | grep -q PONG; do
  sleep 1
  TRIES=$((TRIES + 1))
  if [ "$TRIES" -ge 60 ]; then
    echo "❌ Redis không khởi động được sau 60 giây"
    echo "📄 Redis log gần nhất:"
    docker compose -f "$PROJECT_DIR/backend/docker-compose.yml" logs redis --tail=50 2>/dev/null || true
    exit 1
  fi
done
echo "✅ Redis ready (port ${REDIS_PORT})"

# ═══ 2. Backend ═══
echo ""
echo "☕ [2/4] Starting Spring Boot backend..."

# Load SDKMan nếu có
[ -f "$HOME/.sdkman/bin/sdkman-init.sh" ] && source "$HOME/.sdkman/bin/sdkman-init.sh"

cd "$PROJECT_DIR/backend"
BACKEND_LOG="$PROJECT_DIR/backend/.backend-run.log"
rm -f "$BACKEND_LOG"
mvn spring-boot:run > "$BACKEND_LOG" 2>&1 &
BACKEND_PID=$!

echo "⏳ Waiting for backend..."
TRIES=0
until curl -s -o /dev/null "http://localhost:${BACKEND_PORT}/swagger-ui/index.html" 2>/dev/null; do
  # Process died before passing health check
  if ! kill -0 "$BACKEND_PID" 2>/dev/null; then
    echo "❌ Backend process đã dừng sớm."
    echo "📄 50 dòng log cuối:"
    tail -n 50 "$BACKEND_LOG" 2>/dev/null || true
    exit 1
  fi

  sleep 2
  TRIES=$((TRIES + 1))
  if [ $((TRIES % 5)) -eq 0 ]; then
    echo "   ...đang chờ backend (${TRIES}s x 2 = $((TRIES * 2)) giây)"
  fi
  if [ "$TRIES" -ge 60 ]; then
    echo "❌ Backend không khởi động được sau 120 giây"
    echo "📄 50 dòng log cuối:"
    tail -n 50 "$BACKEND_LOG" 2>/dev/null || true
    exit 1
  fi
done
echo "✅ Backend ready (port ${BACKEND_PORT})"
echo "📄 Backend log: $BACKEND_LOG"

# ═══ 3. Seed demo products ═══
if [ "$AUTO_SEED_PRODUCTS" = "true" ]; then
  echo ""
  echo "🌾 [3/5] Ensuring 90 demo products..."
  mkdir -p "$PROJECT_DIR/.run"
  SEED_LOG="$PROJECT_DIR/.run/seed-90-products.log"
  rm -f "$SEED_LOG"

  if [ -f "$HOME/miniconda3/etc/profile.d/conda.sh" ]; then
    source "$HOME/miniconda3/etc/profile.d/conda.sh"
    if conda env list | grep -q "livehealth-ai"; then
      conda activate livehealth-ai
    fi
  fi

  PYTHON_BIN="${PYTHON_BIN:-python}"
  if ! command -v "$PYTHON_BIN" >/dev/null 2>&1; then
    PYTHON_BIN="python3"
  fi

  if "$PYTHON_BIN" "$PROJECT_DIR/scripts/gen_100_products.py" > "$SEED_LOG" 2>&1; then
    PRODUCT_TOTAL=$(curl -s "http://localhost:${BACKEND_PORT}/api/v1/products?pageNum=1&pageSize=1" \
      | "$PYTHON_BIN" -c "import sys,json; print(json.load(sys.stdin)['data']['meta']['totalElement'])" 2>/dev/null || echo "unknown")
    echo "✅ Product seed ready (total products: ${PRODUCT_TOTAL})"
    echo "📄 Product seed log: $SEED_LOG"
  else
    echo "❌ Không nạp được bộ 90 sản phẩm."
    echo "📄 50 dòng log cuối:"
    tail -n 50 "$SEED_LOG" 2>/dev/null || true
    exit 1
  fi
else
  echo ""
  echo "🌾 [3/5] Skipping product seed (AUTO_SEED_PRODUCTS=false)"
fi

# ═══ 4. AI Service ═══
echo ""
echo "🤖 [4/5] Starting AI service..."
cd "$PROJECT_DIR/ai-service"
AI_LOG="$PROJECT_DIR/ai-service/.ai-run.log"
rm -f "$AI_LOG"

if [ -f "$HOME/miniconda3/etc/profile.d/conda.sh" ]; then
  source "$HOME/miniconda3/etc/profile.d/conda.sh"
  if conda env list | grep -q "livehealth-ai"; then
    conda activate livehealth-ai
  fi
fi

AI_SERVICE_PORT="$AI_PORT" python main.py > "$AI_LOG" 2>&1 &
AI_PID=$!

echo "⏳ Waiting for AI service..."
TRIES=0
until curl -s -o /dev/null "http://localhost:${AI_PORT}/api/ai/health" 2>/dev/null; do
  if ! kill -0 "$AI_PID" 2>/dev/null; then
    echo "❌ AI service process đã dừng sớm."
    echo "📄 50 dòng log cuối:"
    tail -n 50 "$AI_LOG" 2>/dev/null || true
    exit 1
  fi

  sleep 1
  TRIES=$((TRIES + 1))
  if [ "$TRIES" -ge 60 ]; then
    echo "❌ AI service không khởi động được sau 60 giây"
    echo "📄 50 dòng log cuối:"
    tail -n 50 "$AI_LOG" 2>/dev/null || true
    exit 1
  fi
done
echo "✅ AI service ready (port ${AI_PORT})"
echo "📄 AI log: $AI_LOG"

# ═══ 5. Frontend ═══
echo ""
echo "⚛️  [5/5] Starting React frontend..."
cd "$PROJECT_DIR/frontend"

# Cài node_modules nếu chưa có
[ ! -d "node_modules" ] && npm install

# Backend CORS currently allows the configured frontend port.
if lsof -i ":${FRONTEND_PORT}" -sTCP:LISTEN -t >/dev/null 2>&1; then
  echo "❌ Port ${FRONTEND_PORT} đang được sử dụng bởi tiến trình khác."
  echo "   Backend hiện chỉ cho phép CORS origin http://localhost:${FRONTEND_PORT}"
  echo "   Vui lòng dừng tiến trình đang chiếm cổng ${FRONTEND_PORT} rồi chạy lại run_all.sh"
  lsof -i ":${FRONTEND_PORT}" -P -n 2>/dev/null || true
  exit 1
fi

npm run dev -- --port "$FRONTEND_PORT" --strictPort &
FRONTEND_PID=$!

sleep 3

echo ""
echo "═══════════════════════════════════════════"
echo "  🌿 All services running!"
echo "═══════════════════════════════════════════"
echo "  Frontend:  http://localhost:${FRONTEND_PORT}"
echo "  Backend:   http://localhost:${BACKEND_PORT}"
echo "  AI:        http://localhost:${AI_PORT}"
echo "  Swagger:   http://localhost:${BACKEND_PORT}/swagger-ui/index.html"
echo "  MySQL:     localhost:${MYSQL_PORT}"
echo "  Redis:     localhost:${REDIS_PORT}"
echo "═══════════════════════════════════════════"
echo ""
echo "  Press Ctrl+C to stop all services"

# Giữ script chạy chờ Ctrl+C
wait
