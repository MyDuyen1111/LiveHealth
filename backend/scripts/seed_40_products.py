import io
import os
import sys

import requests

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding="utf-8")
sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding="utf-8")

BASE_URL = os.getenv("API_BASE_URL", "http://localhost:62080/api/v1")
GREEN = "\033[92m"; RED = "\033[91m"; YELLOW = "\033[93m"; CYAN = "\033[96m"; RESET = "\033[0m"


def ok(msg): print(f"{GREEN}  [OK] {msg}{RESET}")
def err(msg): print(f"{RED}  [ERR] {msg}{RESET}")
def warn(msg): print(f"{YELLOW}  [WARN] {msg}{RESET}")
def post(url, body, headers=None): return requests.post(f"{BASE_URL}{url}", json=body, headers=headers or {})
def get(url, headers=None): return requests.get(f"{BASE_URL}{url}", headers=headers or {})


print(f"\n{CYAN}{'=' * 56}\n  Seed 40 San Pham Nong San Bo Sung\n{'=' * 56}{RESET}\n")

r = post("/auth/login", {"email": "admin@livehealth.com", "password": os.getenv("ADMIN_PASSWORD", "Admin@123")})
if r.status_code != 200:
    err(f"Login that bai: {r.status_code} - {r.text}")
    sys.exit(1)

headers = {"Authorization": f"Bearer {r.json()['data']['accessToken']}"}
ok("Login thanh cong")

brands = get("/brands", headers).json().get("data", {}).get("items", [])
cats = get("/categories", headers).json().get("data", {}).get("items", [])
if not brands or not cats:
    err("Chua co brand/category. Chay backend/scripts/seed_data.py truoc.")
    sys.exit(1)

brand_id = next((b["id"] for b in brands if b.get("name") == "LiveHealth Farm"), brands[0]["id"])
category_ids = {c["name"]: c["id"] for c in cats}

products = [
    ("Rau Củ", "Rau Muống VietGAP 500g", "Rau muong non, cong gion, phu hop luoc, xao toi va nau canh.", 18000, 120, "VEG-005"),
    ("Rau Củ", "Bí Đỏ Hồ Lô 1kg", "Bi do ruot vang, deo ngot, dung nau sup, ham xuong hoac lam sua bi do.", 36000, 80, "VEG-006"),
    ("Rau Củ", "Dưa Leo Baby 500g", "Dua leo baby gion mat, it hat, dung an song, salad va muoi chua.", 29000, 95, "VEG-007"),
    ("Rau Củ", "Nấm Đùi Gà 300g", "Nam dui ga tuoi, thit day, vi ngot, phu hop ap chao, lau va xao rau.", 42000, 70, "VEG-008"),
    ("Rau Củ", "Hành Tây Đà Lạt 1kg", "Hanh tay vi ngot nhe, mui thom, dung xao, nau sup va uop thit.", 33000, 90, "VEG-009"),
    ("Rau Củ", "Bắp Cải Tím 1kg", "Bap cai tim mau dep, gion, phu hop salad, dua muoi va mon cuon.", 39000, 65, "VEG-010"),
    ("Rau Củ", "Su Su Tam Đảo 1kg", "Su su non, it xo, vi ngot, phu hop xao toi, luoc cham muoi vung.", 26000, 90, "VEG-011"),
    ("Rau Củ", "Ớt Chuông Mix 500g", "Ot chuong do vang xanh, mau sac bat mat, dung salad, xao bo va nuong.", 59000, 55, "VEG-012"),
    ("Hoa Quả", "Táo Envy New Zealand 1kg", "Tao envy gion ngot, huong thom nhe, bao quan lanh, phu hop an vat.", 145000, 45, "FRU-005"),
    ("Hoa Quả", "Nho Đen Không Hạt 500g", "Nho den khong hat, trai mong, vi ngot thanh, dung an tuoi va lam salad.", 119000, 50, "FRU-006"),
    ("Hoa Quả", "Dưa Hấu Long An 1 Quả", "Dua hau ruot do, nhieu nuoc, ngot mat, phu hop giai nhiet moi ngay.", 69000, 60, "FRU-007"),
    ("Hoa Quả", "Thanh Long Ruột Đỏ 1kg", "Thanh long ruot do, vi ngot nhe, giau chat xo, dung an tuoi va xay sinh to.", 48000, 70, "FRU-008"),
    ("Hoa Quả", "Ổi Nữ Hoàng 1kg", "Oi nu hoang gion, it hat, vi ngot thom, phu hop an kem muoi ot.", 39000, 85, "FRU-009"),
    ("Hoa Quả", "Dâu Tây Đà Lạt 250g", "Dau tay tuoi chon loc, vi chua ngot, giao lanh trong ngay.", 89000, 40, "FRU-010"),
    ("Thịt", "Sườn Non Heo 500g", "Suon non tuoi, nhieu sun, phu hop ram man, nau canh va nuong mat ong.", 98000, 55, "MEA-004"),
    ("Thịt", "Thịt Heo Xay 500g", "Heo xay trong ngay, ty le nac cao, dung lam cha, sot mi va canh nhoi.", 72000, 70, "MEA-005"),
    ("Thịt", "Đùi Gà Góc Tư 500g", "Dui ga tuoi, thit chac, phu hop chien, kho gung, nuong va nau lagu.", 58000, 80, "MEA-006"),
    ("Thịt", "Cánh Gà Tươi 500g", "Canh ga tuoi, da mong, hop chien nuoc mam, nuong sa te va rim me.", 76000, 65, "MEA-007"),
    ("Thịt", "Bò Xay 300g", "Bo xay tuoi, tien loi lam burger, sot bo bam va mon an cho tre.", 109000, 45, "MEA-008"),
    ("Thịt", "Nạc Vai Heo 500g", "Nac vai mem, xen mo nhe, phu hop kho tieu, xao rau va lam thit bam.", 82000, 60, "MEA-009"),
    ("Cá & Hải sản", "Mực Ống Làm Sạch 500g", "Muc ong tuoi da lam sach, thit gion ngot, phu hop hap gung, xao dua va nuong.", 149000, 35, "SEA-004"),
    ("Cá & Hải sản", "Cá Diêu Hồng Nguyên Con 1kg", "Ca dieu hong tuoi, thit ngot, hop hap xi dau, chien gion va nau canh chua.", 89000, 50, "SEA-005"),
    ("Cá & Hải sản", "Nghêu Sạch 1kg", "Ngheu da ngam sach cat, vi ngot, dung hap sa, nau canh chua va nau lau.", 59000, 55, "SEA-006"),
    ("Cá & Hải sản", "Cá Thu Cắt Khoanh 500g", "Ca thu cat khoanh, thit chac beo, phu hop kho thom, sot ca va chien.", 139000, 40, "SEA-007"),
    ("Cá & Hải sản", "Tôm Thẻ Bóc Nõn 300g", "Tom the boc non tien loi, dung xao rau, nau chao, chien bot va salad.", 125000, 45, "SEA-008"),
    ("Trứng & Sữa", "Trứng Vịt 10 Quả", "Trung vit tuoi, long do beo, phu hop luoc, kho thit va lam banh.", 46000, 90, "DAI-003"),
    ("Trứng & Sữa", "Sữa Chua Không Đường 4 Hộp", "Sua chua khong duong, vi thanh, phu hop an kem trai cay va ngu coc.", 32000, 100, "DAI-004"),
    ("Trứng & Sữa", "Phô Mai Tươi 200g", "Pho mai tuoi beo nhe, dung lam salad, sandwich va mon an cho be.", 68000, 45, "DAI-005"),
    ("Trứng & Sữa", "Bơ Lạt 200g", "Bo lat thom beo, phu hop nau an, lam banh va ap chao rau cu.", 79000, 40, "DAI-006"),
    ("Gạo & Đồ khô", "Gạo Lứt Huyết Rồng 2kg", "Gao lut huyet rong hat chac, giau chat xo, phu hop an kieng va nau com tron.", 86000, 70, "DRY-003"),
    ("Gạo & Đồ khô", "Yến Mạch Cán Dẹt 500g", "Yen mach can det, dung nau chao, overnight oats va lam banh an sang.", 59000, 80, "DRY-004"),
    ("Gạo & Đồ khô", "Hạt Điều Rang Mộc 300g", "Hat dieu rang moc khong vo lua, vi beo bùi, dung an vat va lam salad.", 118000, 50, "DRY-005"),
    ("Gạo & Đồ khô", "Mè Đen 300g", "Me den hat dep, thom, dung nau che, rang muoi me va lam sua hat.", 36000, 90, "DRY-006"),
    ("Gạo & Đồ khô", "Đậu Đen Xanh Lòng 500g", "Dau den xanh long sach, dung nau che, nau nuoc mat va ngu coc.", 45000, 85, "DRY-007"),
    ("Gạo & Đồ khô", "Miến Dong Bắc Kạn 500g", "Mien dong soi dai, khong nat, phu hop nau mien ga, mien xao va lau.", 62000, 60, "DRY-008"),
    ("Gạo & Đồ khô", "Nấm Hương Khô 100g", "Nam huong kho mui thom dam, dung nau canh, xao rau va lam nhan banh.", 72000, 55, "DRY-009"),
    ("Gạo & Đồ khô", "Mật Ong Hoa Cà Phê 500ml", "Mat ong hoa ca phe nguyen chat, vi thom, dung pha nuoc am va uop mon nuong.", 135000, 45, "DRY-010"),
    ("Hoa Quả", "Lê Hàn Quốc 1kg", "Le Han Quoc qua lon, nhieu nuoc, vi ngot mat, phu hop trang mieng.", 125000, 45, "FRU-011"),
    ("Rau Củ", "Rau Gia Vị Mix 200g", "Bo rau gia vi gom hanh, ngo, thi la, hung que tuy mua, tien loi nau an.", 22000, 100, "VEG-013"),
    ("Cá & Hải sản", "Cua Đồng Xay 500g", "Cua dong xay tuoi, loc san, phu hop nau canh cua rau day, bun rieu.", 68000, 50, "SEA-009"),
]

created = 0
for i, (category_name, name, description, price, stock, sku) in enumerate(products, 1):
    cat_id = category_ids.get(category_name)
    if not cat_id:
        warn(f"Thieu category {category_name}, bo qua {name}")
        continue
    r = post(
        "/products",
        {
            "name": name,
            "description": description,
            "oldPrice": float(price),
            "stock": stock,
            "sku": sku,
            "brandId": brand_id,
            "categoryId": cat_id,
        },
        headers,
    )
    if r.status_code in (200, 201):
        ok(f"[{i:02d}/40] {name}")
        created += 1
    else:
        warn(f"[{i:02d}/40] Bo qua {name}: {r.status_code} - {r.text[:100]}")

print(f"\n{GREEN}{'=' * 56}")
print(f"  XONG: da tao {created}/40 san pham nong san bo sung")
print(f"{'=' * 56}{RESET}")
