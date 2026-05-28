"""Cấu hình chung cho Selenium tests (4.1.4 / 4.1.5 / 4.1.6)."""
import os

# URL frontend (Vite mặc định 5173)
BASE_URL = os.getenv("LIVEHEALTH_BASE_URL", "http://localhost:62173")

# Tài khoản test (seeded sẵn trong backend Application.java)
TEST_EMAIL = os.getenv("LIVEHEALTH_TEST_EMAIL", "user@livehealth.vn")
TEST_PASSWORD = os.getenv("LIVEHEALTH_TEST_PASSWORD", "User123@")

# Tên phương thức được auto-seed trong Application.java
SHIPPING_METHOD_NAME = "Giao hàng tiêu chuẩn"
PAYMENT_METHOD_NAME = "Thanh toán khi nhận hàng (COD)"

# Timeout chờ phần tử (giây)
DEFAULT_TIMEOUT = 40

# Để xem trực tiếp khi test chạy: False. Headless trong CI: True
HEADLESS = os.getenv("LIVEHEALTH_HEADLESS", "false").lower() in ("1", "true", "yes")
