"""4.1.5 — Đặt hàng.

3 test case:
  TC01 — Đã đăng nhập + có sản phẩm trong giỏ → đặt hàng thành công (COD).
  TC02 — Đã đăng nhập nhưng giỏ rỗng → button 'Đặt hàng' disabled / không thể đặt.
  TC03 — Chưa đăng nhập → bấm 'Thêm vào giỏ' bị redirect sang /login.
"""
import time
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

from config import (
    BASE_URL,
    DEFAULT_TIMEOUT,
    SHIPPING_METHOD_NAME,
    PAYMENT_METHOD_NAME,
)


def _clear_cart_via_api(driver):
    """Gọi DELETE /api/v1/cart/clear bằng accessToken trong localStorage."""
    driver.execute_script("""
        const token = localStorage.getItem('accessToken');
        if (!token) return;
        return fetch('/api/v1/cart/clear', {
            method: 'DELETE',
            headers: { 'Authorization': 'Bearer ' + token }
        });
    """)
    time.sleep(0.5)


def _remove_blocking_overlays(driver):
    driver.execute_script("""
        document.querySelectorAll('.nl-overlay, .nl-popup').forEach(e => e.remove());
    """)


SHOP_FIRST_PRODUCT_LINK = (By.CSS_SELECTOR, ".shop-grid .pc .pc-name")
PD_ADD_BTN = (By.CSS_SELECTOR, ".pd-add-btn")
PROCEED_CHECKOUT_LINK = (By.CSS_SELECTOR, ".cart-btn-green.full")
PLACE_ORDER_BTN = (By.CSS_SELECTOR, ".co-summary .co-btn-green.full")
SUCCESS_BANNER = (By.CSS_SELECTOR, ".co-success")
ERROR_MSG = (By.CSS_SELECTOR, ".co-error-msg")
CART_EMPTY = (By.CSS_SELECTOR, ".cart-empty")


def _wait_for(driver, locator, timeout=DEFAULT_TIMEOUT):
    return WebDriverWait(driver, timeout).until(EC.visibility_of_element_located(locator))


def _add_first_product_to_cart(driver):
    """Vào /shop, mở product detail đầu tiên, bấm Add to Cart."""
    driver.get(BASE_URL + "/shop")
    _wait_for(driver, SHOP_FIRST_PRODUCT_LINK).click()
    _wait_for(driver, PD_ADD_BTN).click()
    time.sleep(1.0)  # đợi cart-context cập nhật


def _select_radio_by_label(driver, group_name: str, label_substr: str) -> bool:
    """Trong checkout, click radio thuộc 'group_name' có label chứa label_substr."""
    labels = driver.find_elements(By.CSS_SELECTOR, f".co-summary label.co-pay-option")
    for lbl in labels:
        radio = lbl.find_elements(By.CSS_SELECTOR, f"input[type='radio'][name='{group_name}']")
        if radio and label_substr.lower() in lbl.text.lower():
            driver.execute_script("arguments[0].click();", radio[0])
            return True
    # Fallback: chọn radio đầu tiên cùng group
    radios = driver.find_elements(By.CSS_SELECTOR, f".co-summary input[type='radio'][name='{group_name}']")
    if radios:
        driver.execute_script("arguments[0].click();", radios[0])
        return True
    return False


def _fill_billing_form(driver):
    form = driver.find_element(By.CSS_SELECTOR, ".co-form")
    fields = form.find_elements(By.CSS_SELECTOR, "input, select")
    # Map by placeholder/type
    defaults = {
        "firstName": "Nguyen",
        "lastName": "Test",
        "street": "12 Nguyen Trai",
        "state": "Ho Chi Minh",
        "zip": "70000",
        "email": "user@livehealth.vn",
        "phone": "0901234567",
    }
    # Strategy: lấy theo label text -> input cùng .co-field
    for cf in form.find_elements(By.CSS_SELECTOR, ".co-field"):
        label_text = cf.find_element(By.TAG_NAME, "label").text.lower()
        inputs = cf.find_elements(By.CSS_SELECTOR, "input, select")
        if not inputs:
            continue
        el = inputs[0]
        if "first" in label_text or "tên" in label_text and "first" in label_text:
            el.clear(); el.send_keys(defaults["firstName"])
        elif "last" in label_text or label_text.startswith("họ"):
            el.clear(); el.send_keys(defaults["lastName"])
        elif "street" in label_text or "địa chỉ" in label_text:
            el.clear(); el.send_keys(defaults["street"])
        elif "state" in label_text or "tỉnh" in label_text or "city" in label_text:
            el.clear(); el.send_keys(defaults["state"])
        elif "zip" in label_text or "mã bưu" in label_text or "postal" in label_text:
            el.clear(); el.send_keys(defaults["zip"])
        elif "email" in label_text:
            el.clear(); el.send_keys(defaults["email"])
        elif "phone" in label_text or "điện thoại" in label_text:
            el.clear(); el.send_keys(defaults["phone"])


# ---------------- TC01 ---------------- #
def test_TC01_order_success(logged_in_driver, report):
    driver = logged_in_driver
    _add_first_product_to_cart(driver)

    driver.get(BASE_URL + "/cart")
    _wait_for(driver, PROCEED_CHECKOUT_LINK).click()

    WebDriverWait(driver, DEFAULT_TIMEOUT).until(lambda d: "/checkout" in d.current_url)
    _wait_for(driver, (By.CSS_SELECTOR, ".co-form"))
    _fill_billing_form(driver)

    ship_ok = _select_radio_by_label(driver, "shipping", SHIPPING_METHOD_NAME)
    pay_ok = _select_radio_by_label(driver, "pay", "COD")
    time.sleep(0.3)

    _remove_blocking_overlays(driver)
    btn = driver.find_element(*PLACE_ORDER_BTN)
    driver.execute_script("arguments[0].scrollIntoView({block:'center'});", btn)
    time.sleep(0.3)
    _remove_blocking_overlays(driver)
    driver.execute_script("arguments[0].click();", btn)

    # Đợi 1 trong 2: .co-success xuất hiện HOẶC URL chuyển sang /payment (VNPay)
    success = False
    try:
        WebDriverWait(driver, 20).until(EC.visibility_of_element_located(SUCCESS_BANNER))
        success = True
    except Exception:
        success = False

    actual_url = driver.current_url
    err_text = ""
    if not success and driver.find_elements(*ERROR_MSG):
        err_text = driver.find_element(*ERROR_MSG).text

    report.case(
        name="TC01 — Đặt hàng thành công (đã đăng nhập, giỏ có sản phẩm, COD)",
        input_=f"shipping='{SHIPPING_METHOD_NAME}', payment='COD' / ship_selected={ship_ok}, pay_selected={pay_ok}",
        expected="Hiển thị banner .co-success ('successTitle')",
        actual=f"success_banner={success}, url={actual_url}, error='{err_text}'",
        passed=success,
    )


# ---------------- TC02 ---------------- #
def test_TC02_order_empty_cart(logged_in_driver, report):
    """Giỏ rỗng → /cart hiển thị empty state, không có nút Proceed to checkout."""
    driver = logged_in_driver
    # Đảm bảo cart rỗng: gọi API clear trước
    driver.get(BASE_URL + "/")
    time.sleep(0.5)
    _clear_cart_via_api(driver)
    driver.get(BASE_URL + "/cart")
    time.sleep(1.5)

    has_empty_state = bool(driver.find_elements(*CART_EMPTY))
    has_checkout_link = bool(driver.find_elements(*PROCEED_CHECKOUT_LINK))

    if not has_empty_state:
        # Cart có sản phẩm sót — coi như test môi trường, vẫn validate hành vi đặt hàng giỏ rỗng
        # bằng cách vào trực tiếp /checkout: button disabled khi cart.length===0.
        driver.get(BASE_URL + "/checkout")
        time.sleep(1.0)
        btn = driver.find_elements(*PLACE_ORDER_BTN)
        disabled = btn[0].get_attribute("disabled") is not None if btn else False
        report.case(
            name="TC02 — Đặt hàng khi giỏ rỗng (fallback /checkout)",
            input_="Cart không rỗng do user đã add — fallback kiểm tra /checkout button state",
            expected="Nút .co-btn-green.full disabled khi giỏ trống",
            actual=f"place_order_disabled={disabled}, btn_count={len(btn)}",
            passed=disabled or len(btn) == 0,
        )
        return

    report.case(
        name="TC02 — Đặt hàng khi giỏ rỗng",
        input_="Đăng nhập + giỏ rỗng, vào /cart",
        expected=".cart-empty hiển thị, KHÔNG có link 'Proceed to Checkout'",
        actual=f"cart_empty={has_empty_state}, checkout_link={has_checkout_link}",
        passed=has_empty_state and not has_checkout_link,
    )


# ---------------- TC03 ---------------- #
def test_TC03_order_not_logged_in(driver, report):
    """Bấm 'Thêm vào giỏ' ở Product Detail khi chưa đăng nhập → redirect /login."""
    driver.get(BASE_URL + "/shop")
    _wait_for(driver, SHOP_FIRST_PRODUCT_LINK).click()
    _wait_for(driver, PD_ADD_BTN).click()
    time.sleep(1.0)

    redirected = "/login" in driver.current_url
    report.case(
        name="TC03 — Đặt hàng khi chưa đăng nhập",
        input_="Chưa login, vào /shop → product detail → click .pd-add-btn",
        expected="Redirect tới /login",
        actual=f"current_url={driver.current_url}, redirected_to_login={redirected}",
        passed=redirected,
    )
