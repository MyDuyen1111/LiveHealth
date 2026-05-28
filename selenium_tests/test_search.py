"""4.1.4 — Tìm kiếm sản phẩm.

3 test case:
  TC01 — Từ khóa hợp lệ → trả về danh sách sản phẩm.
  TC02 — Từ khóa không tồn tại → hiển thị "Không tìm thấy sản phẩm nào.".
  TC03 — Ô tìm kiếm trống → điều hướng về trang /shop (không có filter keyword).
"""
import time
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

from config import BASE_URL, DEFAULT_TIMEOUT

SEARCH_INPUT = (By.CSS_SELECTOR, ".h-search input[type='text']")
SEARCH_BTN = (By.CSS_SELECTOR, ".h-search-btn")
SHOP_EMPTY = (By.CSS_SELECTOR, ".shop-empty")
SHOP_GRID = (By.CSS_SELECTOR, ".shop-grid")
RESULT_COUNT = (By.CSS_SELECTOR, ".shop-result-count strong")


def _submit_search(driver, keyword: str):
    wait = WebDriverWait(driver, DEFAULT_TIMEOUT)
    driver.get(BASE_URL + "/")
    box = wait.until(EC.visibility_of_element_located(SEARCH_INPUT))
    box.clear()
    if keyword:
        box.send_keys(keyword)
    driver.find_element(*SEARCH_BTN).click()


def _wait_shop_ready(driver):
    wait = WebDriverWait(driver, DEFAULT_TIMEOUT)
    wait.until(lambda d: "/shop" in d.current_url)
    # Đợi loading "Đang tải..." biến mất
    wait.until(lambda d: "Đang tải" not in (
        d.find_element(*SHOP_EMPTY).text if d.find_elements(*SHOP_EMPTY) else ""))


def test_TC01_search_valid_keyword(driver, report):
    """Tìm kiếm với từ khóa hợp lệ → có ít nhất 1 sản phẩm."""
    keyword = "Cam"
    _submit_search(driver, keyword)
    _wait_shop_ready(driver)
    time.sleep(0.5)

    has_grid = bool(driver.find_elements(*SHOP_GRID))
    product_cards = driver.find_elements(By.CSS_SELECTOR, ".shop-grid .pc")
    count_text = driver.find_element(*RESULT_COUNT).text if driver.find_elements(*RESULT_COUNT) else "0"

    actual = f"grid_visible={has_grid}, cards={len(product_cards)}, result_count_text={count_text}"
    report.case(
        name="TC01 — Tìm kiếm với từ khóa hợp lệ",
        input_=f"keyword='{keyword}'",
        expected="URL chứa /shop?keyword=... AND có ít nhất 1 sản phẩm hiển thị",
        actual=f"URL={driver.current_url} | {actual}",
        passed=(keyword in driver.current_url) and has_grid and len(product_cards) > 0,
    )


def test_TC02_search_no_result(driver, report):
    """Tìm kiếm với từ khóa lạ → 'Không tìm thấy sản phẩm nào.'."""
    keyword = "zzqx_nonexistent_keyword_123"
    _submit_search(driver, keyword)
    _wait_shop_ready(driver)
    time.sleep(0.5)

    empty_text = driver.find_element(*SHOP_EMPTY).text if driver.find_elements(*SHOP_EMPTY) else ""
    has_no_grid = not driver.find_elements(By.CSS_SELECTOR, ".shop-grid .pc")

    report.case(
        name="TC02 — Tìm kiếm không có kết quả",
        input_=f"keyword='{keyword}'",
        expected="Hiển thị 'Không tìm thấy sản phẩm nào.' và không có product card",
        actual=f"empty_text='{empty_text}', no_product_card={has_no_grid}",
        passed=("Không tìm thấy sản phẩm" in empty_text) and has_no_grid,
    )


def test_TC03_search_empty_input(driver, report):
    """Submit form khi ô trống → điều hướng /shop, KHÔNG có ?keyword="""
    _submit_search(driver, "")
    _wait_shop_ready(driver)
    time.sleep(0.5)

    url = driver.current_url
    no_keyword_param = "keyword=" not in url
    on_shop = "/shop" in url

    report.case(
        name="TC03 — Tìm kiếm với ô trống",
        input_="keyword=''",
        expected="Điều hướng tới /shop và KHÔNG có query keyword",
        actual=f"URL={url} | on_shop={on_shop} | no_keyword_param={no_keyword_param}",
        passed=on_shop and no_keyword_param,
    )
