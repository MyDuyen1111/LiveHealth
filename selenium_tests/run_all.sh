#!/usr/bin/env bash
# Chạy toàn bộ Selenium test cho 4.1.4 / 4.1.5 / 4.1.6.
# Yêu cầu: backend (62080) + frontend (5173) đã chạy + DB có sản phẩm.

set -e
cd "$(dirname "$0")"

if [[ -z "$VIRTUAL_ENV" ]]; then
  echo "[INFO] Chưa kích hoạt venv. Đang kích hoạt ../venv ..."
  source ../venv/bin/activate
fi

echo "==> 4.1.4 Search"
pytest -v --tb=short test_search.py "$@"

echo
echo "==> 4.1.5 Order"
pytest -v --tb=short test_order.py "$@"

echo
echo "==> 4.1.6 Order Detail"
pytest -v --tb=short test_order_detail.py "$@"
