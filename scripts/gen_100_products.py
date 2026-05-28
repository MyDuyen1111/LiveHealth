#!/usr/bin/env python3
"""Generate 90 products for LiveHealth via REST API"""
import requests, json, sys

import os, re
api_port = "62080"
admin_pass = "Admin@123"
try:
    with open(os.path.join(os.path.dirname(os.path.dirname(__file__)), "backend", ".env")) as f:
        env_content = f.read()
        match_port = re.search(r"^SPRING_SERVER_PORT=(\d+)", env_content, re.M)
        if match_port: api_port = match_port.group(1).strip()
        match_pass = re.search(r"^ADMIN_PASSWORD=(.*)", env_content, re.M)
        if match_pass: admin_pass = match_pass.group(1).strip()
except: pass

API = f"http://localhost:{api_port}/api/v1"

# Login
r = requests.post(f"{API}/auth/login", json={"email":"admin@livehealth.com","password":admin_pass})
if r.status_code != 200:
    print(f"Login failed! {r.text}")
    sys.exit(1)
TOKEN = r.json()["data"]["accessToken"]
H = {"Authorization": f"Bearer {TOKEN}", "Content-Type": "application/json"}

# Get IDs
cats = {c["name"]: c["id"] for c in requests.get(f"{API}/categories?pageSize=200").json()["data"]["items"]}
brands = {b["name"]: b["id"] for b in requests.get(f"{API}/brands?pageSize=200").json()["data"]["items"]}

# Check existing SKUs
existing = requests.get(f"{API}/products?pageSize=200").json()["data"]["items"]
existing_skus = {p["sku"] for p in existing}
print(f"Existing products: {len(existing_skus)}")

# Product data: (name, desc, sku, price, stock, category_key, brand_key)
products = [
    # ═══ THỊT (20 total, need 15 more) ═══
    ("Thịt Bò Nạm Tươi", "Thịt bò nạm tươi, thích hợp nấu phở, hầm, kho.", "MEAT-006", 280000, 45, "Thịt", "Nông Trại Việt"),
    ("Đùi Gà Góc Tư", "Đùi gà góc tư tươi, thịt chắc ngọt, thích hợp chiên, nướng.", "MEAT-007", 75000, 120, "Thịt", "LiveHealth Farm"),
    ("Thịt Heo Nạc Vai", "Thịt heo nạc vai, ít mỡ, phù hợp xào, nấu canh.", "MEAT-008", 150000, 70, "Thịt", "Nông Trại Việt"),
    ("Ba Rọi Bò Mỹ", "Ba rọi bò Mỹ nhập khẩu, vân mỡ đều, nướng BBQ tuyệt vời.", "MEAT-009", 380000, 25, "Thịt", "Nông Trại Việt"),
    ("Cánh Gà Chiên Sẵn", "Cánh gà tẩm bột chiên sẵn, giòn rụm, tiện lợi.", "MEAT-010", 89000, 90, "Thịt", "LiveHealth Farm"),
    ("Thịt Cừu Úc Rack", "Thịt cừu Úc rack cao cấp, thơm mềm, nướng rosemary.", "MEAT-011", 550000, 15, "Thịt", "Nông Trại Việt"),
    ("Xúc Xích Heo Đức", "Xúc xích heo kiểu Đức, hun khói tự nhiên.", "MEAT-012", 120000, 80, "Thịt", "Nông Trại Việt"),
    ("Thịt Vịt Nguyên Con", "Vịt nguyên con làm sạch, thịt đậm đà, nấu cháo, kho gừng.", "MEAT-013", 165000, 40, "Thịt", "LiveHealth Farm"),
    ("Giò Heo Muối", "Giò heo muối truyền thống, da giòn, thịt mềm.", "MEAT-014", 195000, 35, "Thịt", "Nông Trại Việt"),
    ("Thịt Bò Phi Lê Mignon", "Phi lê mignon bò Úc, phần thịt mềm nhất, áp chảo.", "MEAT-015", 480000, 20, "Thịt", "Nông Trại Việt"),
    ("Nạm Bò Hầm Sốt Vang", "Nạm bò cắt miếng sẵn cho món hầm sốt vang.", "MEAT-016", 260000, 30, "Thịt", "Nông Trại Việt"),
    ("Thịt Heo Xay Tươi", "Thịt heo xay tươi nguyên chất, làm chả, viên thịt.", "MEAT-017", 110000, 100, "Thịt", "LiveHealth Farm"),
    ("Thịt Gà Ta Nguyên Con", "Gà ta thả vườn, thịt chắc thơm, luộc, hấp, nấu cháo.", "MEAT-018", 135000, 55, "Thịt", "Nông Trại Việt"),
    ("Thăn Nội Bò Mỹ", "Thăn nội bò Mỹ Black Angus, mềm tan, steak hoàn hảo.", "MEAT-019", 650000, 12, "Thịt", "Nông Trại Việt"),
    ("Nem Chua Thanh Hóa", "Nem chua Thanh Hóa truyền thống, chua cay đậm vị.", "MEAT-020", 65000, 85, "Thịt", "Nông Trại Việt"),

    # ═══ CÁ & HẢI SẢN (20 total, need 16 more) ═══
    ("Cá Basa Phi Lê", "Cá basa phi lê tươi, thịt trắng mềm, chiên, kho đều ngon.", "FISH-005", 85000, 80, "Cá", "Mekong Fresh"),
    ("Mực Ống Tươi 500g", "Mực ống tươi sống, thịt giòn ngọt, nướng, xào, hấp.", "FISH-006", 180000, 40, "Cá", "Cà Mau Seafood"),
    ("Cua Biển Cà Mau", "Cua biển Cà Mau loại 1, gạch nhiều, thịt chắc ngọt.", "FISH-007", 450000, 20, "Cá", "Mekong Fresh"),
    ("Cá Ngừ Đại Dương", "Cá ngừ đại dương phi lê, giàu protein, ít béo.", "FISH-008", 220000, 35, "Cá", "Cà Mau Seafood"),
    ("Hàu Sữa Tươi Sống", "Hàu sữa tươi sống Phú Quốc, béo ngậy, ăn sống hoặc nướng.", "FISH-009", 280000, 25, "Cá", "Mekong Fresh"),
    ("Cá Diêu Hồng Nguyên Con", "Cá diêu hồng nguyên con, thịt ngọt, hấp, chiên giòn.", "FISH-010", 95000, 60, "Cá", "Mekong Fresh"),
    ("Tôm Hùm Baby", "Tôm hùm baby tươi sống, thịt ngọt dai, hấp bơ tỏi.", "FISH-011", 650000, 10, "Cá", "Cà Mau Seafood"),
    ("Cá Chẽm Phi Lê", "Cá chẽm phi lê tươi, thịt trắng, ít xương, chiên sốt.", "FISH-012", 145000, 50, "Cá", "Mekong Fresh"),
    ("Sò Điệp Tươi 500g", "Sò điệp tươi sống, thịt ngọt mềm, nướng mỡ hành.", "FISH-013", 320000, 30, "Cá", "Cà Mau Seafood"),
    ("Cá Hồi Xông Khói", "Cá hồi xông khói Na Uy, ăn kèm salad, bánh mì.", "FISH-014", 350000, 25, "Cá", "Cà Mau Seafood"),
    ("Tôm Thẻ Sống 1kg", "Tôm thẻ chân trắng sống, size vừa, tươi ngon.", "FISH-015", 195000, 60, "Cá", "Mekong Fresh"),
    ("Cá Cơm Tươi 500g", "Cá cơm tươi biển, chiên giòn, kho tiêu, làm mắm.", "FISH-016", 55000, 100, "Cá", "Mekong Fresh"),
    ("Ghẹ Xanh Tươi Sống", "Ghẹ xanh tươi sống, thịt chắc ngọt, hấp bia.", "FISH-017", 380000, 15, "Cá", "Cà Mau Seafood"),
    ("Bạch Tuộc Baby", "Bạch tuộc baby tươi, thịt giòn, nướng sa tế.", "FISH-018", 165000, 40, "Cá", "Cà Mau Seafood"),
    ("Cá Trắm Kho Tộ", "Cá trắm cắt khúc sẵn, kho tộ, nấu canh chua.", "FISH-019", 78000, 70, "Cá", "Mekong Fresh"),
    ("Phi Lê Cá Dứa", "Phi lê cá dứa tươi, thịt mềm thơm, nấu lẩu, chiên.", "FISH-020", 125000, 45, "Cá", "Mekong Fresh"),

    # ═══ TRỨNG & SỮA (20 total, need 15 more) ═══
    ("Sữa Hạt Óc Chó TH", "Sữa hạt óc chó TH True Nut, giàu dinh dưỡng.", "DAIRY-006", 42000, 100, "Trứng", "Ba Vì Dairy"),
    ("Trứng Vịt Muối (4 quả)", "Trứng vịt muối chín, lòng đỏ bùi béo, ăn kèm cơm.", "DAIRY-007", 35000, 150, "Trứng", "Nông Trại Việt"),
    ("Kem Tươi Vanilla 500ml", "Kem tươi vanilla Ba Vì Dairy, béo mịn thơm.", "DAIRY-008", 85000, 60, "Trứng", "Ba Vì Dairy"),
    ("Sữa Đậu Nành Fami", "Sữa đậu nành Fami nguyên chất, giàu isoflavone.", "DAIRY-009", 28000, 200, "Trứng", "Ba Vì Dairy"),
    ("Phô Mai Mozzarella 200g", "Phô mai Mozzarella tươi, làm pizza, salad.", "DAIRY-010", 95000, 40, "Trứng", "LiveHealth Farm"),
    ("Sữa Tươi Organic TH 1L", "Sữa tươi organic Ba Vì Dairy, 100% hữu cơ.", "DAIRY-011", 52000, 80, "Trứng", "Ba Vì Dairy"),
    ("Trứng Cút Tươi (20 quả)", "Trứng cút tươi, giàu dinh dưỡng, luộc, chiên.", "DAIRY-012", 25000, 180, "Trứng", "Nông Trại Việt"),
    ("Sữa Chua Hy Lạp Greek", "Sữa chua Hy Lạp không đường, giàu protein, ít béo.", "DAIRY-013", 48000, 70, "Trứng", "Ba Vì Dairy"),
    ("Bơ Mặn Président 200g", "Bơ mặn Président nhập Pháp, thơm béo đặc trưng.", "DAIRY-014", 98000, 35, "Trứng", "LiveHealth Farm"),
    ("Sữa Tươi Không Đường TH", "Sữa tươi không đường TH, ít calo, tốt cho sức khỏe.", "DAIRY-015", 36000, 120, "Trứng", "Ba Vì Dairy"),
    ("Phô Mai Cheddar Lát", "Phô mai Cheddar lát Anchor, béo ngậy, kẹp bánh mì.", "DAIRY-016", 68000, 55, "Trứng", "LiveHealth Farm"),
    ("Trứng Gà Công Nghiệp (10)", "Trứng gà công nghiệp 10 quả, giá rẻ, tiện dụng.", "DAIRY-017", 32000, 300, "Trứng", "Nông Trại Việt"),
    ("Sữa Chua Uống Probi", "Sữa chua uống Probi men sống, hỗ trợ tiêu hóa.", "DAIRY-018", 30000, 160, "Trứng", "Ba Vì Dairy"),
    ("Kem Cheese Cake 450ml", "Kem cheese cake thượng hạng, béo mịn, thơm phô mai.", "DAIRY-019", 110000, 30, "Trứng", "Ba Vì Dairy"),
    ("Sữa Tươi Thanh Trùng 1L", "Sữa tươi thanh trùng, vị béo nhẹ, dùng uống trực tiếp hoặc pha chế món ăn.", "DAIRY-020", 72000, 45, "Trứng", "LiveHealth Farm"),

    # ═══ RAU CỦ (20 total, need 15 more) ═══
    ("Cà Chua Beef 1kg", "Cà chua beef đỏ mọng, thịt dày, ít hạt, làm sốt.", "VEG-006", 38000, 120, "Rau", "Đà Lạt Farm"),
    ("Bông Cải Xanh Organic", "Bông cải xanh organic, giàu vitamin C, luộc, xào.", "VEG-007", 45000, 80, "Rau", "Nông Trại Việt"),
    ("Ớt Chuông 3 Màu", "Ớt chuông đỏ, vàng, xanh Đà Lạt, giòn ngọt.", "VEG-008", 55000, 60, "Rau", "Đà Lạt Farm"),
    ("Nấm Đùi Gà 300g", "Nấm đùi gà tươi, thịt dày giòn, xào, nướng, lẩu.", "VEG-009", 48000, 70, "Rau", "Nông Trại Việt"),
    ("Xà Lách Romaine", "Xà lách Romaine giòn tươi, làm Caesar salad.", "VEG-010", 30000, 100, "Rau", "Đà Lạt Farm"),
    ("Hành Tây Tím 1kg", "Hành tây tím, vị ngọt nhẹ, xào, salad, caramel hóa.", "VEG-011", 25000, 200, "Rau", "Đà Lạt Farm"),
    ("Tỏi Lý Sơn 500g", "Tỏi Lý Sơn một nhánh, thơm nồng, gia vị quý.", "VEG-012", 120000, 30, "Rau", "Nông Trại Việt"),
    ("Bắp Ngọt Mỹ (3 trái)", "Bắp ngọt Mỹ, hạt vàng căng, luộc, nướng, nấu súp.", "VEG-013", 35000, 90, "Rau", "LiveHealth Farm"),
    ("Rau Muống Hữu Cơ", "Rau muống hữu cơ tươi, xào tỏi, luộc chấm mắm.", "VEG-014", 18000, 150, "Rau", "Nông Trại Việt"),
    ("Dưa Leo Baby 500g", "Dưa leo baby giòn tươi, ăn sống, salad, cuốn.", "VEG-015", 22000, 120, "Rau", "Đà Lạt Farm"),
    ("Gừng Tươi 300g", "Gừng tươi già, thơm cay, pha trà, nấu ăn.", "VEG-016", 20000, 140, "Rau", "Nông Trại Việt"),
    ("Bí Đỏ Hokkaido", "Bí đỏ Hokkaido Nhật Bản, bột dẻo ngọt, nấu súp.", "VEG-017", 55000, 50, "Rau", "LiveHealth Farm"),
    ("Măng Tây Xanh 250g", "Măng tây xanh tươi, giòn ngọt, xào bơ tỏi.", "VEG-018", 65000, 40, "Rau", "Đà Lạt Farm"),
    ("Đậu Bắp Tươi 500g", "Đậu bắp tươi, giòn, nhiều chất nhầy tốt cho tiêu hóa.", "VEG-019", 28000, 80, "Rau", "Nông Trại Việt"),
    ("Khoai Tây Đà Lạt 1kg", "Khoai tây Đà Lạt vỏ mỏng, bở tơi, chiên, nấu canh.", "VEG-020", 32000, 130, "Rau", "Đà Lạt Farm"),

    # ═══ HOA QUẢ (20 total, need 15 more) ═══
    ("Cam Sành Vĩnh Long 1kg", "Cam sành Vĩnh Long, ngọt thanh, nhiều nước.", "FRUIT-006", 45000, 120, "Hoa", "Mekong Fresh"),
    ("Bưởi Da Xanh 1 trái", "Bưởi da xanh Bến Tre, tép mọng nước, ngọt thanh.", "FRUIT-007", 55000, 80, "Hoa", "Mekong Fresh"),
    ("Thanh Long Ruột Đỏ", "Thanh long ruột đỏ Bình Thuận, ngọt mát, làm sinh tố.", "FRUIT-008", 48000, 90, "Hoa", "Mekong Fresh"),
    ("Lựu Đỏ Mỹ (2 trái)", "Lựu đỏ nhập Mỹ, hạt đỏ mọng, giàu chất chống oxy hóa.", "FRUIT-009", 135000, 35, "Hoa", "LiveHealth Farm"),
    ("Kiwi Zespri Xanh 4 trái", "Kiwi xanh Zespri New Zealand, chua ngọt, giàu vitamin C.", "FRUIT-010", 95000, 50, "Hoa", "LiveHealth Farm"),
    ("Dưa Hấu Không Hạt", "Dưa hấu không hạt, ruột đỏ ngọt mát, giải nhiệt.", "FRUIT-011", 55000, 70, "Hoa", "Mekong Fresh"),
    ("Ổi Đài Loan 1kg", "Ổi Đài Loan ruột trắng, giòn ngọt, giàu vitamin C.", "FRUIT-012", 42000, 100, "Hoa", "LiveHealth Farm"),
    ("Mận Đỏ Chile 500g", "Mận đỏ nhập Chile, vị ngọt chua nhẹ, tươi mát.", "FRUIT-013", 110000, 40, "Hoa", "LiveHealth Farm"),
    ("Chôm Chôm Java 1kg", "Chôm chôm Java tươi, thịt dày, ngọt thanh.", "FRUIT-014", 65000, 60, "Hoa", "Mekong Fresh"),
    ("Lê Hàn Quốc 3 trái", "Lê vàng Hàn Quốc, giòn mọng nước, thơm nhẹ.", "FRUIT-015", 125000, 30, "Hoa", "LiveHealth Farm"),
    ("Sầu Riêng Monthong 1kg", "Sầu riêng Monthong Thái, ruột vàng, béo ngậy.", "FRUIT-016", 220000, 20, "Hoa", "Mekong Fresh"),
    ("Việt Quất Mỹ 125g", "Việt quất (blueberry) nhập Mỹ, siêu thực phẩm.", "FRUIT-017", 145000, 30, "Hoa", "LiveHealth Farm"),
    ("Bơ 034 Đắk Lắk", "Bơ 034 Đắk Lắk, vỏ xanh, ruột vàng kem, béo dẻo.", "FRUIT-018", 75000, 55, "Hoa", "Mekong Fresh"),
    ("Quýt Đường Lai Vung", "Quýt đường Lai Vung, vỏ mỏng, ngọt lịm.", "FRUIT-019", 48000, 90, "Hoa", "Mekong Fresh"),
    ("Cherry Đỏ Mỹ 500g", "Cherry đỏ Mỹ size lớn, ngọt giòn, giàu anthocyanin.", "FRUIT-020", 350000, 15, "Hoa", "LiveHealth Farm"),

    # ═══ GẠO & ĐỒ KHÔ (14 products) ═══
    ("Gạo thơm Vua Gạo ST25 túi 5kg", "Gạo thơm ST25 Vua Gạo túi 5kg, hạt dài, cơm dẻo thơm, phù hợp bữa cơm gia đình.", "DRY-011", 139000, 100, "Gạo", "Vua Gạo"),
    ("Yến mạch nguyên chất Yumfood hũ 800 g", "Yến mạch nguyên chất Yumfood hũ 800g, dùng nấu cháo, overnight oats và bữa sáng lành mạnh.", "DRY-012", 139000, 100, "Gạo", "Yumfood"),
    ("Ngũ cốc VinaCafé B'fast 500g", "Ngũ cốc VinaCafé B'fast 500g tiện lợi, pha nhanh cho bữa sáng đủ năng lượng.", "DRY-013", 68000, 100, "Gạo", "VinaCafé"),
    ("Hạt chia Sunrise gói 300g", "Hạt chia Sunrise gói 300g, giàu chất xơ, dùng pha nước, làm pudding hoặc trộn sữa chua.", "DRY-014", 102000, 100, "Gạo", "Sunrise"),
    ("Đậu đỏ 150g", "Đậu đỏ sạch gói 150g, phù hợp nấu chè, nấu cháo, làm nhân bánh và các món dinh dưỡng.", "DRY-015", 16000, 100, "Gạo", "LiveHealth Farm"),
    ("Gạo nếp cái hoa vàng Vinh Hiển túi 1kg", "Gạo nếp cái hoa vàng Vinh Hiển túi 1kg, dẻo thơm, dùng nấu xôi và làm bánh truyền thống.", "DRY-016", 36000, 100, "Gạo", "Vinh Hiển"),
    ("Rong nho tách nước Top Food 50g", "Rong nho tách nước Top Food 50g, giòn mát, dùng kèm nước sốt mè hoặc salad.", "DRY-017", 37000, 100, "Gạo", "Top Food"),
    ("Rong biển nấu canh Ottogi gói 50g", "Rong biển nấu canh Ottogi gói 50g, tiện nấu canh, súp và các món ăn Hàn Quốc.", "DRY-018", 57000, 100, "Gạo", "Ottogi"),
    ("Gạo lứt tím Vinh Hiển túi 1kg", "Gạo lứt tím Vinh Hiển túi 1kg, giàu chất xơ, phù hợp chế độ ăn lành mạnh.", "DRY-019", 48000, 100, "Gạo", "Vinh Hiển"),
    ("Bún gạo lứt Jimmy túi 250g", "Bún gạo lứt Jimmy túi 250g, sợi dai, dễ chế biến cho món bún trộn, bún nước và thực đơn eat clean.", "DRY-020", 28000, 100, "Gạo", "Jimmy"),
    ("Ngũ cốc gạo lứt Xuân An 400g", "Ngũ cốc gạo lứt Xuân An 400g, thơm bùi, pha uống hoặc dùng bổ sung bữa sáng.", "DRY-021", 79000, 100, "Gạo", "Xuân An"),
    ("Đậu gà hữu cơ OnFod túi 300g", "Đậu gà hữu cơ OnFod túi 300g, giàu đạm thực vật, dùng nấu súp, salad hoặc hummus.", "DRY-022", 69000, 100, "Gạo", "OnFod"),
    ("Đậu lăng đỏ hữu cơ tách vỏ OnFod túi 500g", "Đậu lăng đỏ hữu cơ tách vỏ OnFod túi 500g, nhanh chín, phù hợp nấu súp và món chay.", "DRY-023", 102000, 100, "Gạo", "OnFod"),
    ("Đậu hà lan hữu cơ OnFod túi 500g", "Đậu hà lan hữu cơ OnFod túi 500g, hạt chắc, dùng nấu cháo, súp, salad và món chay.", "DRY-024", 69000, 100, "Gạo", "OnFod"),
]

category_seed = {
    "Thịt": {"name": "Thịt", "description": "Thịt tươi, sơ chế sạch, nguồn gốc rõ ràng"},
    "Cá": {"name": "Cá & Hải sản", "description": "Cá, tôm, mực và hải sản tươi sống"},
    "Trứng": {"name": "Trứng & Sữa", "description": "Trứng gà, sữa tươi và sản phẩm từ sữa"},
    "Rau": {"name": "Rau Củ", "description": "Rau xanh, củ quả tươi mới mỗi ngày"},
    "Hoa": {"name": "Hoa Quả", "description": "Trái cây theo mùa, chọn lọc từ nhà vườn"},
    "Gạo": {"name": "Gạo & Đồ khô", "description": "Gạo, đậu hạt, ngũ cốc và đặc sản khô"},
}

for cat_key, payload in category_seed.items():
    if not any(cat_key in name for name in cats):
        requests.post(f"{API}/categories", json=payload, headers=H)

cats = {c["name"]: c["id"] for c in requests.get(f"{API}/categories?pageSize=200").json()["data"]["items"]}

for brand_name in sorted({product[6] for product in products}):
    if not any(brand_name in name for name in brands):
        requests.post(
            f"{API}/brands",
            json={"name": brand_name, "description": f"Thương hiệu {brand_name}"},
            headers=H,
        )

brands = {b["name"]: b["id"] for b in requests.get(f"{API}/brands?pageSize=200").json()["data"]["items"]}

print(f"Categories: {list(cats.keys())}")
print(f"Brands: {list(brands.keys())}")

print(f"\nCreating {len(products)} new products...")
created = 0
failed = 0

for name, desc, sku, price, stock, cat_key, brand_key in products:
    if sku in existing_skus:
        continue

    # Find category & brand ID by partial match
    cat_id = next((v for k,v in cats.items() if cat_key in k), None)
    brand_id = next((v for k,v in brands.items() if brand_key in k), None)

    if not cat_id or not brand_id:
        print(f"  ❌ {sku} {name}: cat={cat_key} brand={brand_key} not found")
        failed += 1
        continue

    data = {
        "name": name, "description": desc, "sku": sku,
        "oldPrice": price, "stock": stock,
        "categoryId": cat_id, "brandId": brand_id
    }
    r = requests.post(f"{API}/products", json=data, headers=H)
    if r.status_code in [200, 201]:
        created += 1
        if created % 10 == 0:
            print(f"  ... {created} created")
    else:
        print(f"  ❌ {sku} {name}: {r.status_code} {r.text[:100]}")
        failed += 1

print(f"\n✅ Done! Created: {created}, Failed: {failed}")
total = requests.get(f"{API}/products?pageSize=1").json()["data"]["meta"]["totalElement"]
print(f"📦 Total products in DB: {total}")
