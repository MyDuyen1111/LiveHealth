# Selenium tests — LiveHealth (mục 4.1.4 / 4.1.5 / 4.1.6)

## 1. Yêu cầu môi trường
- Backend Spring Boot chạy ở `http://localhost:62080`
- Frontend Vite chạy ở `http://localhost:5173`
- DB đã seed sản phẩm (chạy `backend/scripts/seed_data.py`)
- Tài khoản test (Application.java tự seed lúc start backend):
  - Email: `user@livehealth.vn`
  - Password: `User123@`

## 2. Cài đặt
```bash
cd ~/Documents/LiveHealth
python3 -m venv venv
source venv/bin/activate
pip install -r selenium_tests/requirements.txt
```

## 3. Chạy
```bash
source venv/bin/activate
cd selenium_tests
chmod +x run_all.sh
./run_all.sh 2>&1 | tee test_results.txt
```

## 4. Đè cấu hình bằng env var (tuỳ chọn)
```bash
LIVEHEALTH_BASE_URL=http://localhost:5174 \
LIVEHEALTH_TEST_EMAIL=other@user.com \
LIVEHEALTH_TEST_PASSWORD='SomethingElse' \
LIVEHEALTH_HEADLESS=true \
  ./run_all.sh
```

## 5. Cấu trúc file
- `config.py` — URL, tài khoản, timeout
- `conftest.py` — driver fixture + login helper + reporter PASS/FAIL
- `test_search.py` — 4.1.4 (3 TC)
- `test_order.py` — 4.1.5 (3 TC)
- `test_order_detail.py` — 4.1.6 (3 TC)
- `run_all.sh` — orchestrator

## 6. Lưu ý
- TC01 của `test_order_detail.py` cần ít nhất 1 đơn hàng tồn tại. Nếu DB chưa có đơn, hãy chạy `test_order.py::TC01` trước (sẽ tạo đơn), rồi chạy `test_order_detail.py`.
- `test_order.py` TC02 (giỏ rỗng) giả định session sạch: nếu user đã có cart sót, fallback kiểm tra button disabled trên `/checkout`.
