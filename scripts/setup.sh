#!/usr/bin/env bash
# ═══════════════════════════════════════════════════════════
#  LiveHealth — Cài đặt môi trường lần đầu
#  Chạy 1 lần duy nhất sau khi clone repo
#  Usage: ./scripts/setup.sh
# ═══════════════════════════════════════════════════════════
set -e

PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

echo ""
echo -e "${CYAN}═══════════════════════════════════════════════${NC}"
echo -e "${CYAN}  🌿 LiveHealth — Cài đặt môi trường${NC}"
echo -e "${CYAN}═══════════════════════════════════════════════${NC}"
echo ""

# ── Hàm kiểm tra ──
check_cmd() {
  if command -v "$1" &>/dev/null; then
    echo -e "  ${GREEN}✅ $1${NC} — $(eval "$2" 2>&1 | head -1)"
    return 0
  else
    echo -e "  ${RED}❌ $1${NC} — chưa cài"
    return 1
  fi
}

# ═══════════════════════════════════════════════
# 1. KIỂM TRA PREREQUISITES
# ═══════════════════════════════════════════════
echo -e "${YELLOW}[1/5] Kiểm tra prerequisites...${NC}"
MISSING=0

check_cmd "docker" "docker --version" || MISSING=1
check_cmd "docker" "docker compose version" || MISSING=1
check_cmd "java" "java -version" || MISSING=1
check_cmd "mvn" "mvn -version" || MISSING=1
check_cmd "node" "node -v" || MISSING=1
check_cmd "npm" "npm -v" || MISSING=1
check_cmd "conda" "conda --version" || MISSING=1

echo ""

if [ "$MISSING" -eq 1 ]; then
  echo -e "${RED}⚠️  Thiếu một số công cụ. Vui lòng cài đặt theo README.md mục 0 rồi chạy lại.${NC}"
  echo ""
  echo "  Hướng dẫn nhanh:"
  echo "    Docker:  https://docs.docker.com/engine/install/"
  echo "    Java 21: sdk install java 21.0.6-tem"
  echo "    Maven:   sdk install maven"
  echo "    Node.js: nvm install --lts"
  echo "    Conda:   https://docs.conda.io/en/latest/miniconda.html"
  echo ""
  exit 1
fi

# ═══════════════════════════════════════════════
# 2. TẠO FILE .ENV
# ═══════════════════════════════════════════════
echo -e "${YELLOW}[2/5] Cấu hình file .env...${NC}"

# Backend .env
if [ ! -f "$PROJECT_DIR/backend/.env" ]; then
  cp "$PROJECT_DIR/backend/.env.example" "$PROJECT_DIR/backend/.env"
  echo -e "  ${GREEN}✅ Tạo backend/.env từ .env.example${NC}"
else
  echo -e "  ${CYAN}ℹ️  backend/.env đã tồn tại — giữ nguyên${NC}"
fi

# AI Service .env
if [ ! -f "$PROJECT_DIR/ai-service/.env" ]; then
  cat > "$PROJECT_DIR/ai-service/.env" << 'EOF'
# Gemini API Key (lấy miễn phí tại: https://aistudio.google.com/apikey)
GEMINI_API_KEY=

# MySQL (khớp với backend/docker-compose.yml)
MYSQL_HOST=localhost
MYSQL_PORT=62307
MYSQL_USER=root
MYSQL_PASSWORD=root123
MYSQL_DATABASE=livehealth
EOF
  echo -e "  ${GREEN}✅ Tạo ai-service/.env${NC}"
  echo -e "  ${YELLOW}⚠️  Nhớ điền GEMINI_API_KEY trong ai-service/.env${NC}"
else
  echo -e "  ${CYAN}ℹ️  ai-service/.env đã tồn tại — giữ nguyên${NC}"
fi

echo ""

# ═══════════════════════════════════════════════
# 3. TẠO CONDA ENVIRONMENT + CÀI PYTHON PACKAGES
# ═══════════════════════════════════════════════
echo -e "${YELLOW}[3/5] Cài đặt Python (Conda + pip)...${NC}"

if conda env list | grep -q "livehealth-ai"; then
  echo -e "  ${CYAN}ℹ️  Conda env 'livehealth-ai' đã tồn tại${NC}"
else
  echo -e "  📦 Tạo conda env 'livehealth-ai' (Python 3.11)..."
  conda create -n livehealth-ai python=3.11 -y -q
  echo -e "  ${GREEN}✅ Tạo conda env thành công${NC}"
fi

# Cài pip packages trong conda env
echo -e "  📦 Cài thư viện Python..."
eval "$(conda shell.bash hook)"
conda activate livehealth-ai
pip install -r "$PROJECT_DIR/requirements.txt" -q
echo -e "  ${GREEN}✅ Cài xong thư viện Python${NC}"
echo ""

# ═══════════════════════════════════════════════
# 4. CÀI FRONTEND NODE MODULES
# ═══════════════════════════════════════════════
echo -e "${YELLOW}[4/5] Cài đặt Frontend (npm)...${NC}"

cd "$PROJECT_DIR/frontend"
if [ -d "node_modules" ]; then
  echo -e "  ${CYAN}ℹ️  node_modules đã tồn tại — chạy npm install để đồng bộ${NC}"
fi
npm install --silent
echo -e "  ${GREEN}✅ Cài xong node_modules${NC}"
echo ""

# ═══════════════════════════════════════════════
# 5. KIỂM TRA DOCKER DAEMON
# ═══════════════════════════════════════════════
echo -e "${YELLOW}[5/5] Kiểm tra Docker daemon...${NC}"

if docker info &>/dev/null; then
  echo -e "  ${GREEN}✅ Docker daemon đang chạy${NC}"
else
  echo -e "  ${RED}❌ Docker daemon chưa chạy — hãy khởi động Docker trước khi chạy hệ thống${NC}"
  echo -e "     sudo systemctl start docker"
fi

echo ""
echo -e "${GREEN}═══════════════════════════════════════════════${NC}"
echo -e "${GREEN}  ✅ Cài đặt hoàn tất!${NC}"
echo -e "${GREEN}═══════════════════════════════════════════════${NC}"
echo ""
echo "  Bước tiếp theo:"
echo ""
echo -e "  ${CYAN}1. Chạy hệ thống:${NC}"
echo "     ./scripts/run_all.sh"
echo ""
echo -e "  ${CYAN}2. Dữ liệu mẫu:${NC}"
echo "     run_all.sh sẽ tự nạp bộ 90 sản phẩm mẫu sau khi backend sẵn sàng"
echo "     Muốn chạy thủ công lại: python scripts/gen_100_products.py"
echo ""
echo -e "  ${YELLOW}📌 Đừng quên điền GEMINI_API_KEY trong ai-service/.env${NC}"
echo ""
