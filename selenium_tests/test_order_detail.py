"""4.1.6 — Xem chi tiết đơn hàng.

3 test case:
  TC01 — Đã đăng nhập + có đơn → từ /account/orders click 'View Details' → trang chi tiết hiển thị.
  TC02 — Đăng nhập + truy cập ID đơn không thuộc về user → 'Không tìm thấy đơn hàng.'.
  TC03 — Chưa đăng nhập → /account/orders/<id> redirect /login.
"""
import time
import uuid
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

from config import BASE_URL, DEFAULT_TIMEOUT


VIEW_DETAIL_LINKS = (By.CSS_SELECTOR, ".oh-view-detail")
ORDER_ROWS = (By.CSS_SELECTOR, ".oh-table tbody tr")
ORDER_DETAIL_CARD = (By.CSS_SELECTOR, ".od-card")
ORDER_DETAIL_TITLE = (By.CSS_SELECTOR, ".od-title")


def _wait_for(driver, locator, timeout=DEFAULT_TIMEOUT):
    return WebDriverWait(driver, timeout).until(EC.visibility_of_element_located(locator))


# ---------------- TC01 ---------------- #
def test_TC01_view_order_detail_success(logged_in_driver, report):
    driver = logged_in_driver
    driver.get(BASE_URL + "/account/orders")
    time.sleep(1.0)

    links = driver.find_elements(*VIEW_DETAIL_LINKS)
    if not links:
        report.case(
            name="TC01 — Xem chi tiết đơn hàng tồn tại",
            input_="user đăng nhập, vào /account/orders",
            expected="Có ít nhất 1 link 'View Details' để click",
            actual="Không có đơn nào trong lịch sử — cần test_order TC01 chạy trước hoặc seed đơn hàng",
            passed=False,
        )
        return

    target_href = links[0].get_attribute("href")
    links[0].click()
    _wait_for(driver, ORDER_DETAIL_CARD)
    time.sleep(0.5)

    on_detail_url = "/account/orders/" in driver.current_url
    has_title = bool(driver.find_elements(*ORDER_DETAIL_TITLE))
    has_summary = bool(driver.find_elements(By.CSS_SELECTOR, ".od-summary-card"))
    has_products_table = bool(driver.find_elements(By.CSS_SELECTOR, ".od-products-table"))

    report.case(
        name="TC01 — Xem chi tiết đơn hàng tồn tại",
        input_=f"click link đầu tiên href={target_href}",
        expected="URL chứa /account/orders/<id> + hiện .od-card, .od-title, .od-summary-card, .od-products-table",
        actual=f"url={driver.current_url} | title={has_title} | summary={has_summary} | products={has_products_table}",
        passed=on_detail_url and has_title and has_summary and has_products_table,
    )


# ---------------- TC02 ---------------- #
def test_TC02_view_other_users_order(logged_in_driver, report):
    """ID UUID hợp lệ nhưng không thuộc về user → backend trả lỗi → UI 'Không tìm thấy đơn hàng.'."""
    driver = logged_in_driver
    fake_id = str(uuid.uuid4())
    driver.get(f"{BASE_URL}/account/orders/{fake_id}")
    time.sleep(2.0)

    page_text = driver.find_element(By.TAG_NAME, "body").text
    has_not_found = "Không tìm thấy đơn hàng" in page_text
    has_detail_card = bool(driver.find_elements(*ORDER_DETAIL_CARD))

    report.case(
        name="TC02 — Xem đơn không thuộc về user",
        input_=f"GET /account/orders/{fake_id} (UUID random)",
        expected="UI hiển thị 'Không tìm thấy đơn hàng.' và không có .od-card",
        actual=f"not_found_msg={has_not_found}, detail_card_visible={has_detail_card}",
        passed=has_not_found and not has_detail_card,
    )


# ---------------- TC03 ---------------- #
def test_TC03_view_order_detail_not_logged_in(driver, report):
    """Chưa đăng nhập + vào /account/orders/<id> → redirect /login."""
    fake_id = str(uuid.uuid4())
    driver.get(f"{BASE_URL}/account/orders/{fake_id}")
    # ProtectedRoute redirect ngay, AuthContext có loading→sau đó replace /login
    WebDriverWait(driver, DEFAULT_TIMEOUT).until(lambda d: "/login" in d.current_url)

    on_login = "/login" in driver.current_url
    report.case(
        name="TC03 — Xem chi tiết đơn khi chưa đăng nhập",
        input_=f"chưa login, GET /account/orders/{fake_id}",
        expected="Redirect tới /login (ProtectedRoute)",
        actual=f"final_url={driver.current_url}, on_login={on_login}",
        passed=on_login,
    )
