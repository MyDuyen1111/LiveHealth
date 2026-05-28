"""
LiveHealth Seed Data Script
--------------------------
Tao du lieu nen cho web ban nong san:
  - Brand nong san
  - Category nong san
  - San pham rau cu, trai cay, thit ca, trung sua, gao/hat
  - Tai khoan user demo
"""
import io
import sys

import os

import requests

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8")
sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding="utf-8")

BASE_URL = os.getenv("API_BASE_URL", "http://localhost:62080/api/v1")
ADMIN_PASSWORD = os.getenv("ADMIN_PASSWORD", "Admin@123")

GREEN = "\033[92m"
RED = "\033[91m"
YELLOW = "\033[93m"
CYAN = "\033[96m"
RESET = "\033[0m"


def ok(msg): print(f"{GREEN}  [OK] {msg}{RESET}")
def err(msg): print(f"{RED}  [ERR] {msg}{RESET}")
def warn(msg): print(f"{YELLOW}  [WARN] {msg}{RESET}")


def post(url, body, headers=None):
    return requests.post(f"{BASE_URL}{url}", json=body, headers=headers or {})


def get(url, headers=None):
    return requests.get(f"{BASE_URL}{url}", headers=headers or {})


def create_or_pick(endpoint, payload, headers):
    r = post(endpoint, payload, headers)
    if r.status_code in (200, 201):
      return r.json()["data"]["id"]

    items = get(endpoint, headers).json().get("data", {}).get("items", [])
    for item in items:
        if item.get("name") == payload["name"]:
            return item["id"]
    if items:
        return items[0]["id"]
    raise RuntimeError(f"Khong tao/lay duoc {endpoint}: {r.status_code} {r.text}")


print(f"\n{CYAN}{'=' * 56}\n  LiveHealth Seed Data - Nong San\n{'=' * 56}{RESET}\n")

print("[1/5] Login admin...")
r = post("/auth/login", {"email": "admin@livehealth.com", "password": ADMIN_PASSWORD})
if r.status_code != 200:
    err(f"Login that bai: {r.status_code} - {r.text}")
    sys.exit(1)

token = r.json()["data"]["accessToken"]
headers = {"Authorization": f"Bearer {token}"}
ok("Login thanh cong!")

print("\n[2/5] Tao brand nong san...")
brand_id = create_or_pick(
    "/brands",
    {"name": "LiveHealth Farm", "description": "Nong san tuoi sach tu cac vung trong dat chuan"},
    headers,
)
ok(f"Brand ID: {brand_id}")

print("\n[3/5] Tao category...")
categories = [
    {"name": "Rau Củ", "description": "Rau xanh, cu qua tuoi moi moi ngay"},
    {"name": "Hoa Quả", "description": "Trai cay theo mua, chon loc tu nha vuon"},
    {"name": "Thịt", "description": "Thit tuoi, so che sach, nguon goc ro rang"},
    {"name": "Cá & Hải sản", "description": "Ca, tom, muc va hai san tuoi song"},
    {"name": "Trứng & Sữa", "description": "Trung ga, sua tuoi va san pham tu sua"},
    {"name": "Gạo & Đồ khô", "description": "Gao, dau hat, ngu coc va dac san kho"},
]

category_ids = {}
for cat in categories:
    category_ids[cat["name"]] = create_or_pick("/categories", cat, headers)
    ok(cat["name"])

print("\n[4/5] Tao san pham nong san...")
products = [
    ("Rau Củ", "Cải Bó Xôi Đà Lạt 500g", "Rau cai bo xoi tuoi, la non, phu hop xao toi, nau canh hoac lam salad.", 32000, 80, "VEG-001"),
    ("Rau Củ", "Cà Rốt Đà Lạt 1kg", "Ca rot gion ngot, mau sac dep, dung cho nuoc ep, ham canh va mon kho.", 28000, 120, "VEG-002"),
    ("Rau Củ", "Khoai Tây Vàng 1kg", "Khoai tay ruot vang, beo bui, hop nau sup, chien, ham bo va salad.", 35000, 100, "VEG-003"),
    ("Rau Củ", "Cà Chua Beef 1kg", "Ca chua chin tu nhien, vi chua ngot can bang, dung nau sot va salad.", 42000, 90, "VEG-004"),
    ("Hoa Quả", "Cam Sành Vĩnh Long 1kg", "Cam sanh mong nuoc, vi ngot thanh, phu hop vat nuoc moi ngay.", 52000, 100, "FRU-001"),
    ("Hoa Quả", "Xoài Cát Chu Đồng Tháp 1kg", "Xoai cat chu thom, thit vang min, ngot diu, an chin hoac lam sinh to.", 68000, 70, "FRU-002"),
    ("Hoa Quả", "Bơ Sáp Đắk Lắk 1kg", "Bo sap beo, hat nho, thit deo, phu hop lam salad, sinh to va mon an sang.", 79000, 65, "FRU-003"),
    ("Hoa Quả", "Chuối Laba Lâm Đồng 1 nải", "Chuoi laba chin tu nhien, ngot thom, tien loi cho bua phu lanh manh.", 45000, 85, "FRU-004"),
    ("Thịt", "Thịt Heo Ba Rọi Sạch 500g", "Ba roi heo tuoi, ty le nac mo can bang, phu hop kho, nuong va chien ap chao.", 89000, 50, "MEA-001"),
    ("Thịt", "Ức Gà Tươi 500g", "Uc ga tuoi, giau dam, it beo, phu hop ap chao, salad va thuc don eat clean.", 62000, 75, "MEA-002"),
    ("Thịt", "Thịt Bò Thăn Nội 300g", "Bo than noi mem, it gan, phu hop bit tet, xao nhanh va mon ap chao.", 185000, 40, "MEA-003"),
    ("Cá & Hải sản", "Cá Hồi Phi Lê 300g", "Ca hoi phi le tuoi, giau omega-3, phu hop ap chao, nuong va sushi tai nha.", 159000, 45, "SEA-001"),
    ("Cá & Hải sản", "Tôm Sú Cà Mau 500g", "Tom su tuoi, thit chac ngot, dung hap, nuong, rim hoac nau lau.", 175000, 35, "SEA-002"),
    ("Cá & Hải sản", "Cá Basa Phi Lê 500g", "Ca basa phi le khong xuong, thit mem, phu hop kho to, chien xot va nau canh chua.", 69000, 65, "SEA-003"),
    ("Trứng & Sữa", "Trứng Gà Ta 10 Quả", "Trung ga ta long do dam, vo sach, phu hop bua sang va lam banh.", 42000, 120, "DAI-001"),
    ("Trứng & Sữa", "Sữa Tươi Thanh Trùng 1L", "Sua tuoi thanh trung it duong, vi beo nhe, giao lanh trong ngay.", 39000, 90, "DAI-002"),
    ("Gạo & Đồ khô", "Gạo ST25 Sóc Trăng 5kg", "Gao ST25 hat dai, com deo thom, dong goi moi, phu hop bua com gia dinh.", 185000, 60, "DRY-001"),
    ("Gạo & Đồ khô", "Đậu Xanh Cà Vỏ 500g", "Dau xanh ca vo sach, dung nau che, nau xoi, lam nhan banh va ngu coc.", 42000, 80, "DRY-002"),
]

created = 0
for category_name, name, description, price, stock, sku in products:
    body = {
        "name": name,
        "description": description,
        "oldPrice": float(price),
        "stock": stock,
        "sku": sku,
        "brandId": brand_id,
        "categoryId": category_ids[category_name],
    }
    r = post("/products", body, headers)
    if r.status_code in (200, 201):
        ok(name)
        created += 1
    else:
        warn(f"Bo qua {name}: {r.status_code} - {r.text[:120]}")

print("\n[5/5] Tao tai khoan user demo...")
r = post(
    "/admin/create-user",
    {
        "email": "user@livehealth.vn",
        "password": "User123@",
        "firstName": "Khach",
        "lastName": "LiveHealth",
        "phone": "0901234567",
        "role": "USER",
    },
    headers,
)
if r.status_code in (200, 201):
    ok("Tao user thanh cong")
elif r.status_code == 409 or "already" in r.text.lower():
    warn("User demo da ton tai, bo qua")
else:
    warn(f"Tao user that bai: {r.status_code} - {r.text[:160]}")

print(f"\n{GREEN}{'=' * 56}")
print("  [DONE] LiveHealth seed data hoan thanh")
print(f"{'=' * 56}{RESET}")
print(f"""
  USER DEMO
  Email   : user@livehealth.vn
  Password: User123@

  SAN PHAM : {created} san pham nong san
  BRAND    : LiveHealth Farm
  API      : http://localhost:62080
""")
