"""Pytest fixtures: driver, login helper, reporter PASS/FAIL."""
import os
import time
import pytest
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from webdriver_manager.chrome import ChromeDriverManager

from config import BASE_URL, TEST_EMAIL, TEST_PASSWORD, DEFAULT_TIMEOUT, HEADLESS

SCREENSHOT_DIR = os.path.join(os.path.dirname(__file__), "screenshots")
os.makedirs(SCREENSHOT_DIR, exist_ok=True)


def _build_driver():
    opts = Options()
    if HEADLESS:
        opts.add_argument("--headless=new")
    opts.add_argument("--window-size=1366,900")
    opts.add_argument("--no-sandbox")
    opts.add_argument("--disable-dev-shm-usage")
    opts.add_argument("--disable-gpu")
    opts.add_argument("--disable-blink-features=AutomationControlled")
    service = Service(ChromeDriverManager().install())
    return webdriver.Chrome(service=service, options=opts)


@pytest.fixture()
def driver():
    drv = _build_driver()
    drv.implicitly_wait(2)
    # Set flag để popup newsletter không bao giờ hiện trong suốt session
    try:
        drv.get(BASE_URL + "/")
        drv.execute_script("localStorage.setItem('nl_dismissed', '1');")
    except Exception:
        pass
    yield drv
    drv.quit()


@pytest.fixture()
def base_url():
    return BASE_URL


def _dismiss_overlays(driver):
    """Đóng newsletter popup nếu nó che layout."""
    for sel in [".nl-close", ".np-close", ".newsletter-close"]:
        try:
            btns = driver.find_elements(By.CSS_SELECTOR, sel)
            for b in btns:
                if b.is_displayed():
                    driver.execute_script("arguments[0].click();", b)
                    time.sleep(0.2)
        except Exception:
            pass
    # Xoá overlay khỏi DOM nếu vẫn còn
    try:
        driver.execute_script(
            "document.querySelectorAll('.nl-overlay').forEach(e => e.remove());")
    except Exception:
        pass


def _dump_on_failure(driver, name):
    try:
        path = f"/tmp/livehealth-debug-{name}.png"
        driver.save_screenshot(path)
        print(f"[DEBUG] screenshot saved: {path}")
        print(f"[DEBUG] current_url: {driver.current_url}")
        err_els = driver.find_elements(By.CSS_SELECTOR, ".auth-error")
        if err_els:
            print(f"[DEBUG] auth-error: {err_els[0].text}")
    except Exception:
        pass


def login(driver, email=TEST_EMAIL, password=TEST_PASSWORD):
    """Đăng nhập rồi đợi điều hướng sang /account."""
    driver.get(f"{BASE_URL}/login")
    time.sleep(0.5)
    _dismiss_overlays(driver)
    wait = WebDriverWait(driver, DEFAULT_TIMEOUT)
    email_input = wait.until(EC.visibility_of_element_located(
        (By.CSS_SELECTOR, ".auth-card input[type='email']")))
    pwd_input = driver.find_element(By.CSS_SELECTOR, ".auth-card input[type='password']")
    email_input.clear()
    email_input.send_keys(email)
    pwd_input.clear()
    pwd_input.send_keys(password)
    _dismiss_overlays(driver)
    submit = driver.find_element(By.CSS_SELECTOR, ".auth-submit-btn")
    driver.execute_script("arguments[0].click();", submit)
    try:
        wait.until(lambda d: "/login" not in d.current_url)
    except Exception:
        _dump_on_failure(driver, "login")
        raise


@pytest.fixture()
def logged_in_driver(driver):
    login(driver)
    return driver


# ---------- Reporter PASS/FAIL ---------- #
import unicodedata


def _slugify(text: str) -> str:
    keep = "abcdefghijklmnopqrstuvwxyz0123456789_"
    norm = unicodedata.normalize("NFD", text)
    ascii_text = "".join(c for c in norm if unicodedata.category(c) != "Mn")
    ascii_text = ascii_text.replace("đ", "d").replace("Đ", "d")
    s = ascii_text.lower().replace(" ", "_").replace("—", "_").replace("-", "_")
    while "__" in s:
        s = s.replace("__", "_")
    return "".join(c for c in s if c in keep).strip("_")[:80]


class _Reporter:
    def __init__(self, driver, test_name):
        self._driver = driver
        self._test_name = test_name
        self.results = []

    def case(self, name, input_, expected, actual, passed):
        status = "PASS" if passed else "FAIL"
        # Lưu screenshot — remove popup overlay + scroll lên top để không cắt mất banner
        try:
            self._driver.execute_script(
                "document.querySelectorAll('.nl-overlay, .nl-popup').forEach(e => e.remove());"
                "window.scrollTo({top: 0, left: 0, behavior: 'instant'});"
            )
            time.sleep(0.3)
        except Exception:
            pass
        slug = _slugify(name) or _slugify(self._test_name)
        shot_path = os.path.join(SCREENSHOT_DIR, f"{slug}_{status}.png")
        try:
            self._driver.save_screenshot(shot_path)
        except Exception as exc:
            shot_path = f"<screenshot failed: {exc}>"

        line = (
            f"\n[{status}] {name}"
            f"\n  Input      : {input_}"
            f"\n  Expected   : {expected}"
            f"\n  Actual     : {actual}"
            f"\n  Screenshot : {shot_path}\n"
        )
        print(line)
        self.results.append((name, status, shot_path))
        assert passed, f"{name} FAILED — expected={expected} actual={actual}"


@pytest.fixture()
def report(request, driver):
    return _Reporter(driver, request.node.name)
