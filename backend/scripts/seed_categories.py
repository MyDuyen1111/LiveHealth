import io
import os
import sys

import requests

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8")
sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding="utf-8")

BASE_URL = os.getenv("API_BASE_URL", "http://localhost:62080/api/v1")
GREEN = "\033[92m"; RED = "\033[91m"; CYAN = "\033[96m"; RESET = "\033[0m"


def ok(msg): print(f"{GREEN}  [OK] {msg}{RESET}")
def err(msg): print(f"{RED}  [ERR] {msg}{RESET}")
def post(url, body, headers=None): return requests.post(f"{BASE_URL}{url}", json=body, headers=headers or {})


print(f"\n{CYAN}{'=' * 60}\n  Seed Danh Muc Nong San\n{'=' * 60}{RESET}\n")

r = post("/auth/login", {"email": "admin@livehealth.com", "password": os.getenv("ADMIN_PASSWORD", "Admin@123")})
if r.status_code != 200:
    err(f"Login that bai: {r.status_code} - {r.text}")
    sys.exit(1)

headers = {"Authorization": f"Bearer {r.json()['data']['accessToken']}"}
ok("Login thanh cong")

categories = [
    ("Rau Củ", "Rau xanh, cu qua tuoi moi moi ngay"),
    ("Hoa Quả", "Trai cay noi dia va nhap khau theo mua"),
    ("Thịt", "Thit heo, bo, ga tuoi so che sach"),
    ("Cá & Hải sản", "Ca, tom, muc, ngheu va hai san tuoi"),
    ("Trứng & Sữa", "Trung tuoi, sua tuoi va che pham tu sua"),
    ("Gạo & Đồ khô", "Gao, dau hat, ngu coc, nam kho va dac san kho"),
]

for name, description in categories:
    r = post("/categories", {"name": name, "description": description}, headers)
    if r.status_code in (200, 201):
        ok(name)
    else:
        err(f"{name}: {r.status_code} - {r.text[:120]}")

print(f"\n{GREEN}Hoan thanh seed danh muc nong san.{RESET}")
