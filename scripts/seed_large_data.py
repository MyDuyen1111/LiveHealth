#!/usr/bin/env python3
import json
import urllib.request
import urllib.error
import subprocess
import time
import sys

API_BASE = "http://localhost:62080/api/v1"

def api_request(path, method="GET", data=None, token=None):
    url = f"{API_BASE}{path}"
    headers = {"Content-Type": "application/json"}
    if token:
        headers["Authorization"] = f"Bearer {token}"
    
    req_data = json.dumps(data).encode("utf-8") if data else None
    req = urllib.request.Request(url, data=req_data, headers=headers, method=method)
    
    try:
        with urllib.request.urlopen(req) as res:
            return json.loads(res.read().decode("utf-8"))
    except urllib.error.HTTPError as e:
        print(f"HTTP Error {e.code}: {e.read().decode('utf-8')}")
        sys.exit(1)
    except Exception as e:
        print(f"Error connecting to {url}: {e}")
        sys.exit(1)

def get_docker_command():
    try:
        res = subprocess.run(["docker", "ps"], capture_output=True)
        if res.returncode == 0:
            return ["docker"]
    except Exception:
        pass
    return ["sudo", "docker"]

DOCKER_CMD = get_docker_command()

def run_sql(sql):
    cmd = DOCKER_CMD + [
        "exec", "-i", "mysql-db-livehealth",
        "mysql", "-uroot", "-proot123", "livehealth", "-e", sql
    ]
    res = subprocess.run(cmd, capture_output=True, text=True)
    if res.returncode != 0:
        print(f"SQL Error: {res.stderr}")
        return False
    return True

print("🔑 Logging in as admin...")
login_res = api_request("/auth/login", method="POST", data={
    "email": "admin@livehealth.com",
    "password": "Admin@123"
})
TOKEN = login_res["data"]["accessToken"]
print(" ✅ Login successful.")

print("\n🧹 Cleaning existing product tables...")
clean_sql = """
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE product_tag;
TRUNCATE TABLE product_image;
TRUNCATE TABLE product;
SET FOREIGN_KEY_CHECKS = 1;
"""
if run_sql(clean_sql):
    print(" ✅ Database cleared.")
else:
    print(" ❌ Error cleaning database.")
    sys.exit(1)

print("\n📋 Fetching Category IDs...")
categories_res = api_request("/categories")
categories = {c["name"]: c["id"] for c in categories_res["data"]["items"]}
print(f" ✅ Found categories: {list(categories.keys())}")

print("📋 Fetching Brand IDs...")
brands_res = api_request("/brands")
brands = {b["name"]: b["id"] for b in brands_res["data"]["items"]}
print(f" ✅ Found brands: {list(brands.keys())}")

print("📋 Fetching Tag IDs...")
tags_res = api_request("/tags")
tag_ids = [t["id"] for t in tags_res["data"]["items"]]
print(f" ✅ Found tags: {len(tag_ids)}")

products_data = [
    # === CATEGORY: Thịt ===
    {
        "category": "Thịt",
        "brand": "LiveHealth Farm",
        "name": "Thịt Bò Úc Hữu Cơ",
        "description": "Thịt bò Úc nhập khẩu, nuôi tự nhiên, không hormone. Thích hợp nướng, xào, bò lúc lắc.",
        "sku": "MEAT-001",
        "oldPrice": 320000.0,
        "stock": 50,
        "image": "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Thịt",
        "brand": "LiveHealth Farm",
        "name": "Thịt Heo Ba Chỉ Sạch",
        "description": "Thịt heo ba chỉ từ trang trại sạch, không chất tăng trọng. Phù hợp kho, nướng, luộc.",
        "sku": "MEAT-002",
        "oldPrice": 180000.0,
        "stock": 80,
        "image": "https://images.unsplash.com/photo-1602489114777-628a8d11c080?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Thịt",
        "brand": "LiveHealth Farm",
        "name": "Ức Gà Phi Lê Tươi",
        "description": "Ức gà phi lê ít mỡ, giàu protein. Lý tưởng cho người tập gym và ăn kiêng.",
        "sku": "MEAT-003",
        "oldPrice": 95000.0,
        "stock": 100,
        "image": "https://images.unsplash.com/photo-1604503468506-a8da13d82791?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Thịt",
        "brand": "Nông Trại Việt",
        "name": "Thịt Bò Wagyu A5 Nhật Bản",
        "description": "Thịt bò Wagyu hạng A5 nhập khẩu Nhật Bản, vân mỡ đẹp, tan trong miệng.",
        "sku": "MEAT-004",
        "oldPrice": 1200000.0,
        "stock": 10,
        "image": "https://images.unsplash.com/photo-1588166524941-3bf61a9c41db?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Thịt",
        "brand": "Nông Trại Việt",
        "name": "Sườn Non Heo Hữu Cơ",
        "description": "Sườn non heo hữu cơ, thịt mềm ngọt tự nhiên. Thích hợp nấu canh, kho, nướng.",
        "sku": "MEAT-005",
        "oldPrice": 220000.0,
        "stock": 60,
        "image": "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Thịt",
        "brand": "Nông Trại Việt",
        "name": "Thịt Đùi Cừu Không Xương",
        "description": "Đùi cừu tươi nhập khẩu New Zealand, thịt chắc ngọt, giàu dinh dưỡng.",
        "sku": "MEAT-006",
        "oldPrice": 450000.0,
        "stock": 30,
        "image": "https://images.unsplash.com/photo-1514516345957-556ca7d90a29?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Thịt",
        "brand": "LiveHealth Farm",
        "name": "Thịt Bò Mỹ Ba Chỉ Thái Lát",
        "description": "Ba chỉ bò Mỹ thái lát mỏng, lý tưởng cho món lẩu và nướng BBQ hàn quốc.",
        "sku": "MEAT-007",
        "oldPrice": 240000.0,
        "stock": 90,
        "image": "https://images.unsplash.com/photo-1529692236671-f1f6e998a789?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Thịt",
        "brand": "Nông Trại Việt",
        "name": "Thịt Heo Nạc Vai Sạch",
        "description": "Nạc vai heo sạch, thịt mềm có lẫn mỡ nhẹ, phù hợp xào, kho, làm thịt xá xíu.",
        "sku": "MEAT-008",
        "oldPrice": 165000.0,
        "stock": 70,
        "image": "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Thịt",
        "brand": "LiveHealth Farm",
        "name": "Cánh Gà Tươi Thả Vườn",
        "description": "Cánh gà tươi sạch thả vườn thịt dai ngọt tự nhiên, thích hợp chiên nước mắm, nướng lu.",
        "sku": "MEAT-009",
        "oldPrice": 110000.0,
        "stock": 80,
        "image": "https://images.unsplash.com/photo-1567620832903-9fc6debc209f?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Thịt",
        "brand": "LiveHealth Farm",
        "name": "Đùi Gà Tỏi Thả Vườn",
        "description": "Đùi gà tỏi tươi, giàu nạc, da giòn, rất ngon cho món gà chiên giòn, hầm thuốc bắc.",
        "sku": "MEAT-010",
        "oldPrice": 115000.0,
        "stock": 85,
        "image": "https://images.unsplash.com/photo-1598515214211-89d3c73ae83b?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Thịt",
        "brand": "LiveHealth Farm",
        "name": "Thịt Bò Xay Cao Cấp",
        "description": "Thịt bò xay tươi từ phần thịt nạc đùi ngon, giàu đạm, làm sốt bolognese, hamburger.",
        "sku": "MEAT-011",
        "oldPrice": 250000.0,
        "stock": 40,
        "image": "https://images.unsplash.com/photo-1588166524941-3bf61a9c41db?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Thịt",
        "brand": "Nông Trại Việt",
        "name": "Thịt Chim Bồ Câu Pháp",
        "description": "Bồ câu Pháp nuôi hữu cơ nguyên con làm sạch, thịt ngọt bổ dưỡng tốt cho người già, bà bầu.",
        "sku": "MEAT-012",
        "oldPrice": 130000.0,
        "stock": 35,
        "image": "https://images.unsplash.com/photo-1516685018646-549198525c1b?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Thịt",
        "brand": "Nông Trại Việt",
        "name": "Giò Sống Heo Sạch",
        "description": "Giò sống từ heo sạch xay nhuyễn dai mịn tự nhiên, không hàn bột, thích hợp nấu canh, mọc lẩu.",
        "sku": "MEAT-013",
        "oldPrice": 170000.0,
        "stock": 50,
        "image": "https://images.unsplash.com/photo-1602489114777-628a8d11c080?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Thịt",
        "brand": "LiveHealth Farm",
        "name": "Thịt Heo Nạc Dăm Hữu Cơ",
        "description": "Thịt heo nạc dăm hữu cơ, thớ thịt xen kẽ mỡ mềm ngọt dẻo, hoàn hảo cho món ram, rim cháy cạnh.",
        "sku": "MEAT-014",
        "oldPrice": 195000.0,
        "stock": 65,
        "image": "https://images.unsplash.com/photo-1529692236671-f1f6e998a789?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Thịt",
        "brand": "Nông Trại Việt",
        "name": "Chân Giò Heo Rút Xương",
        "description": "Chân giò heo làm sạch rút xương bó cuộn thơm ngon, phù hợp hầm chiên giòn, luộc thái lát mỏng.",
        "sku": "MEAT-015",
        "oldPrice": 175000.0,
        "stock": 40,
        "image": "https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&q=80&w=600"
    },

    # === CATEGORY: Cá & Hải sản ===
    {
        "category": "Cá & Hải sản",
        "brand": "Cà Mau Seafood",
        "name": "Cá Hồi Na Uy Phi Lê",
        "description": "Cá hồi Na Uy tươi sống phi lê rút xương, giàu Omega-3, ăn sashimi, làm ruốc hoặc áp chảo sốt cam.",
        "sku": "FISH-001",
        "oldPrice": 450000.0,
        "stock": 30,
        "image": "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Cá & Hải sản",
        "brand": "Mekong Fresh",
        "name": "Tôm Sú Sống Size Lớn",
        "description": "Tôm sú sống tự nhiên size lớn 15-20 con/kg, vỏ mỏng thịt dày chắc giòn, luộc hấp xả gừng cực ngọt.",
        "sku": "FISH-002",
        "oldPrice": 350000.0,
        "stock": 40,
        "image": "https://images.unsplash.com/photo-1559742811-824289528574?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Cá & Hải sản",
        "brand": "Mekong Fresh",
        "name": "Cá Thu Cắt Khúc Tươi",
        "description": "Cá thu đại dương tươi sống đánh bắt trong ngày cắt khúc giữa nhiều nạc dày thịt béo ngọt, kho tiêu chiên sốt cà.",
        "sku": "FISH-003",
        "oldPrice": 180000.0,
        "stock": 50,
        "image": "https://images.unsplash.com/photo-1534604973900-c43ab4c2e0ab?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Cá & Hải sản",
        "brand": "Cà Mau Seafood",
        "name": "Nghêu Lụa Tươi Sống",
        "description": "Nghêu lụa tươi sạch cát, ruột to dày thịt giòn dai béo, ăn lẩu thái nấu canh chua hay hấp sả đều rất ngon.",
        "sku": "FISH-004",
        "oldPrice": 85000.0,
        "stock": 70,
        "image": "https://images.unsplash.com/photo-1534080391025-09795d197a5b?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Cá & Hải sản",
        "brand": "Cà Mau Seafood",
        "name": "Mực Ống Tươi Côn Đảo",
        "description": "Mực ống tươi xanh dày cơm, giòn sần sật, xào sa tế nhồi thịt nướng muối ớt.",
        "sku": "FISH-005",
        "oldPrice": 320000.0,
        "stock": 45,
        "image": "https://images.unsplash.com/photo-1534422298391-e4f8c172dddb?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Cá & Hải sản",
        "brand": "Cà Mau Seafood",
        "name": "Cá Hồi Na Uy Nguyên Con",
        "description": "Cá hồi Na Uy nguyên con làm sạch, nhập khẩu đường bay tươi rói chất lượng sashimi.",
        "sku": "FISH-006",
        "oldPrice": 380000.0,
        "stock": 15,
        "image": "https://images.unsplash.com/photo-1534604973900-c43ab4c2e0ab?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Cá & Hải sản",
        "brand": "Cà Mau Seafood",
        "name": "Cua Biển Cà Mau Y4",
        "description": "Cua thịt Cà Mau dây trói mỏng siêu nạc, gạch béo ngọt lịm đặc trưng nổi tiếng nhất.",
        "sku": "FISH-007",
        "oldPrice": 420000.0,
        "stock": 35,
        "image": "https://images.unsplash.com/photo-1551248429-40975aa4de74?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Cá & Hải sản",
        "brand": "Mekong Fresh",
        "name": "Bạch Tuộc Tươi Sống",
        "description": "Bạch tuộc tươi giòn, râu đều chắc thịt, hoàn hảo cho nướng sa tế hay làm gỏi chua cay.",
        "sku": "FISH-008",
        "oldPrice": 190000.0,
        "stock": 60,
        "image": "https://images.unsplash.com/photo-1545671913-b89ac1b4ac10?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Cá & Hải sản",
        "brand": "Mekong Fresh",
        "name": "Cá Điêu Hồng Làm Sạch",
        "description": "Cá điêu hồng tươi sống nuôi lồng bè làm sạch đánh vẩy mang sẵn, thịt ngọt béo, chiên xù nấu canh ngót.",
        "sku": "FISH-009",
        "oldPrice": 90000.0,
        "stock": 80,
        "image": "https://images.unsplash.com/photo-1534604973900-c43ab4c2e0ab?w=600"
    },
    {
        "category": "Cá & Hải sản",
        "brand": "Cà Mau Seafood",
        "name": "Tôm Hùm Bông Khánh Hòa",
        "description": "Tôm hùm bông cao cấp Khánh Hòa, thịt dai ngọt lịm, gạch son vàng ngậy bổ dưỡng thượng hạng.",
        "sku": "FISH-010",
        "oldPrice": 950000.0,
        "stock": 12,
        "image": "https://images.unsplash.com/photo-1559742811-824289528574?w=600"
    },
    {
        "category": "Cá & Hải sản",
        "brand": "Mekong Fresh",
        "name": "Cá Lóc Đồng Làm Sạch",
        "description": "Cá lóc đồng làm sạch mang ruột nhớt sạch sẽ, nấu bánh canh, kho tộ đậm vị quê nhà.",
        "sku": "FISH-011",
        "oldPrice": 120000.0,
        "stock": 50,
        "image": "https://images.unsplash.com/photo-1534604973900-c43ab4c2e0ab?w=600"
    },
    {
        "category": "Cá & Hải sản",
        "brand": "Cà Mau Seafood",
        "name": "Cá Trích Phi Lê Không Xương",
        "description": "Cá trích tươi lóc xương phi lê sẵn, thích hợp nhất làm gỏi cá trích Phú Quốc hay cuộn chiên giòn.",
        "sku": "FISH-012",
        "oldPrice": 140000.0,
        "stock": 40,
        "image": "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?w=600"
    },
    {
        "category": "Cá & Hải sản",
        "brand": "Cà Mau Seafood",
        "name": "Sò Huyết Lạc Biển",
        "description": "Sò huyết Cà Mau ngọt béo giàu huyết, luộc tái xào tỏi nướng mọi bổ máu phục hồi sức khỏe.",
        "sku": "FISH-013",
        "oldPrice": 210000.0,
        "stock": 55,
        "image": "https://images.unsplash.com/photo-1534080391025-09795d197a5b?w=600"
    },
    {
        "category": "Cá & Hải sản",
        "brand": "Cà Mau Seafood",
        "name": "Hàu Sữa Pháp Tươi Sống",
        "description": "Hàu sữa vỏ mỏng ruột múp mọng sữa béo ngậy, nướng mỡ hành phô mai cực ngon ăn sống mù tạt ngọt mát.",
        "sku": "FISH-014",
        "oldPrice": 95000.0,
        "stock": 100,
        "image": "https://images.unsplash.com/photo-1553618551-fba689030290?w=600"
    },
    {
        "category": "Cá & Hải sản",
        "brand": "Mekong Fresh",
        "name": "Cá Bớp Cắt Lát Khúc",
        "description": "Cá bớp biển tươi cắt khúc dày, thịt cá bớp dẻo thơm, da dày ngậy rất hợp nấu lẩu măng chua, kho nghệ.",
        "sku": "FISH-015",
        "oldPrice": 290000.0,
        "stock": 30,
        "image": "https://images.unsplash.com/photo-1534604973900-c43ab4c2e0ab?w=600"
    },

    # === CATEGORY: Trứng & Sữa ===
    {
        "category": "Trứng & Sữa",
        "brand": "Ba Vì Dairy",
        "name": "Trứng Gà Hữu Cơ (10 quả)",
        "description": "Trứng gà hữu cơ từ gà thả vườn ăn ngũ cốc, vỏ dày, lòng đỏ to đậm ngậy tự nhiên.",
        "sku": "DAIRY-001",
        "oldPrice": 55000.0,
        "stock": 200,
        "image": "https://images.unsplash.com/photo-1516448424-47efefefefef?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Trứng & Sữa",
        "brand": "Ba Vì Dairy",
        "name": "Sữa Tươi Ba Vì Thanh Trùng",
        "description": "Sữa tươi thanh trùng nguyên chất vị béo thơm thuần khiết nhất từ Ba Vì, giao lạnh liên tục giữ trọn dưỡng chất.",
        "sku": "DAIRY-002",
        "oldPrice": 38000.0,
        "stock": 150,
        "image": "https://images.unsplash.com/photo-1550583724-b2692b85b150?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Trứng & Sữa",
        "brand": "Ba Vì Dairy",
        "name": "Sữa Chua Ba Vì Không Đường",
        "description": "Sữa chua lên men tự nhiên nguyên chất không đường, vị chua nhẹ thanh mát ngậy dẻo mịn tuyệt vời.",
        "sku": "DAIRY-003",
        "oldPrice": 32000.0,
        "stock": 120,
        "image": "https://images.unsplash.com/photo-1488477181946-6428a0291777?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Trứng & Sữa",
        "brand": "LiveHealth Farm",
        "name": "Phô Mai Con Bò Cười",
        "description": "Phô mai miếng tam giác con bò cười vị truyền thống mềm mịn ngậy bổ sung canxi tối đa cho xương bé chắc khỏe.",
        "sku": "DAIRY-004",
        "oldPrice": 45000.0,
        "stock": 90,
        "image": "https://images.unsplash.com/photo-1528750955902-5b4cf510ec04?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Trứng & Sữa",
        "brand": "LiveHealth Farm",
        "name": "Bơ Lạt Anchor 227g",
        "description": "Bơ lạt Anchor nhập khẩu New Zealand làm bánh béo thơm, không đường, làm cốt bánh, xào nướng cực ngậy hương vị sữa tự nhiên.",
        "sku": "DAIRY-005",
        "oldPrice": 75000.0,
        "stock": 60,
        "image": "https://images.unsplash.com/photo-1589985270826-4b7bb135bc9d?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Trứng & Sữa",
        "brand": "Ba Vì Dairy",
        "name": "Trứng Vịt Hữu Cơ (10 quả)",
        "description": "Trứng vịt sạch nuôi bán chăn thả tự nhiên, trứng to đều béo đậm, làm trứng kho tàu làm bánh cực ngon.",
        "sku": "DAIRY-006",
        "oldPrice": 42000.0,
        "stock": 180,
        "image": "https://images.unsplash.com/photo-1516448424-47efefefefef?w=600"
    },
    {
        "category": "Trứng & Sữa",
        "brand": "Ba Vì Dairy",
        "name": "Trứng Cút Tươi Sạch (30 quả)",
        "description": "Trứng chim cút sạch vỏ cứng trứng tươi nhỏ xinh lòng đỏ nhiều béo ngậy, nấu thịt kho tàu, xào me cực hấp dẫn.",
        "sku": "DAIRY-007",
        "oldPrice": 30000.0,
        "stock": 150,
        "image": "https://images.unsplash.com/photo-1516448424-47efefefefef?w=600"
    },
    {
        "category": "Trứng & Sữa",
        "brand": "LiveHealth Farm",
        "name": "Sữa Đậu Nành Organic",
        "description": "Sữa đậu nành nguyên chất thơm bùi organic, không chất bảo quản, giàu protein thực vật, tốt cho tim mạch.",
        "sku": "DAIRY-008",
        "oldPrice": 22000.0,
        "stock": 100,
        "image": "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=600"
    },
    {
        "category": "Trứng & Sữa",
        "brand": "LiveHealth Farm",
        "name": "Sữa Đặc Có Đường Hoàn Hảo",
        "description": "Sữa đặc có đường vị ngọt béo ngậy truyền thống hoàn hảo để pha cà phê sữa đá Việt Nam, làm sinh tố trái cây.",
        "sku": "DAIRY-009",
        "oldPrice": 28000.0,
        "stock": 120,
        "image": "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=600"
    },
    {
        "category": "Trứng & Sữa",
        "brand": "Ba Vì Dairy",
        "name": "Sữa Tươi Tiệt Trùng Ít Đường",
        "description": "Sữa tươi tiệt trùng nguyên chất ít đường tiện lợi cho bé uống mỗi ngày, đóng hộp 4 hộp x 180ml thơm ngon đầy canxi.",
        "sku": "DAIRY-010",
        "oldPrice": 30000.0,
        "stock": 250,
        "image": "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=600"
    },
    {
        "category": "Trứng & Sữa",
        "brand": "Ba Vì Dairy",
        "name": "Sữa Chua Uống Men Sống",
        "description": "Sữa chua uống tiệt trùng vị dâu thơm ngon chứa lợi khuẩn tốt cho hệ tiêu hóa của trẻ em.",
        "sku": "DAIRY-011",
        "oldPrice": 26000.0,
        "stock": 200,
        "image": "https://images.unsplash.com/photo-1488477181946-6428a0291777?w=600"
    },
    {
        "category": "Trứng & Sữa",
        "brand": "LiveHealth Farm",
        "name": "Phô Mai Mozzarella Bào Sợi",
        "description": "Phô mai Mozzarella bào sợi mịn béo dẻo, tan chảy tạo sợi cực đỉnh cho pizza nướng xào nướng gà phô mai.",
        "sku": "DAIRY-012",
        "oldPrice": 85000.0,
        "stock": 80,
        "image": "https://images.unsplash.com/photo-1528750955902-5b4cf510ec04?w=600"
    },
    {
        "category": "Trứng & Sữa",
        "brand": "LiveHealth Farm",
        "name": "Kem Tươi Whipping Cream 250ml",
        "description": "Kem tươi Whipping Cream béo mịn nguyên chất nhập khẩu từ New Zealand để làm bánh kem sinh nhật nấu súp.",
        "sku": "DAIRY-013",
        "oldPrice": 65000.0,
        "stock": 70,
        "image": "https://images.unsplash.com/photo-1589985270826-4b7bb135bc9d?w=600"
    },
    {
        "category": "Trứng & Sữa",
        "brand": "LiveHealth Farm",
        "name": "Bơ Mặn President Pháp 250g",
        "description": "Bơ mặn cao cấp President của Pháp, có muối nhẹ đậm vị sữa béo nướng bít tết phết bánh mì thượng hạng.",
        "sku": "DAIRY-014",
        "oldPrice": 95000.0,
        "stock": 50,
        "image": "https://images.unsplash.com/photo-1589985270826-4b7bb135bc9d?w=600"
    },
    {
        "category": "Trứng & Sữa",
        "brand": "Ba Vì Dairy",
        "name": "Trứng Gà Ta Thả Vườn",
        "description": "Trứng gà ta thả vườn tự nhiên, quả nhỏ lòng đỏ đỏ đậm béo bùi, luộc ăn sáng chiên cơm cực đỉnh bổ dưỡng.",
        "sku": "DAIRY-015",
        "oldPrice": 60000.0,
        "stock": 160,
        "image": "https://images.unsplash.com/photo-1516448424-47efefefefef?w=600"
    },

    # === CATEGORY: Rau Củ ===
    {
        "category": "Rau Củ",
        "brand": "Đà Lạt Farm",
        "name": "Rau Cải Bó Xôi Hữu Cơ",
        "description": "Cải bó xôi (spinach) hữu cơ, giàu sắt và vitamin K. Luộc, xào, làm salad tốt cho máu và tim mạch.",
        "sku": "VEG-001",
        "oldPrice": 35000.0,
        "stock": 100,
        "image": "https://images.unsplash.com/photo-1576045057995-568f588f82fb?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Rau Củ",
        "brand": "Đà Lạt Farm",
        "name": "Cà Rốt Đà Lạt Tươi Sạch",
        "description": "Cà rốt Đà Lạt tươi, ngọt giòn mọng nước thơm nhẹ tự nhiên, giàu beta-carotene rất tốt cho mắt và da hồng hào.",
        "sku": "VEG-002",
        "oldPrice": 28000.0,
        "stock": 150,
        "image": "https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Rau Củ",
        "brand": "Nông Trại Việt",
        "name": "Bắp Cải Tím Organic",
        "description": "Bắp cải tím organic dày lá giòn ngọt đậm vị, giàu chất chống oxy hóa, tuyệt vời để trộn salad ăn kiêng giảm cân.",
        "sku": "VEG-003",
        "oldPrice": 42000.0,
        "stock": 80,
        "image": "https://images.unsplash.com/photo-1506806732259-39c2d0268443?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Rau Củ",
        "brand": "Đà Lạt Farm",
        "name": "Khoai Lang Nhật Ruột Vàng",
        "description": "Khoai lang Nhật ngọt đậm bùi, ruột vàng cam dẻo thơm ngậy, luộc hấp nướng ăn kiêng cực kỳ bổ dưỡng lành mạnh.",
        "sku": "VEG-004",
        "oldPrice": 45000.0,
        "stock": 90,
        "image": "https://images.unsplash.com/photo-1596461404969-9ae70f2830c1?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Rau Củ",
        "brand": "LiveHealth Farm",
        "name": "Bơ Sáp Đắk Lắk Loại 1",
        "description": "Bơ sáp Đắk Lắk quả to tròn loại 1 ruột vàng dẻo mịn béo ngậy như kem tươi, ăn trực tiếp làm sinh tố hoặc salad bơ tôm.",
        "sku": "VEG-005",
        "oldPrice": 68000.0,
        "stock": 40,
        "image": "https://images.unsplash.com/photo-1523049673857-eb18f1d7b578?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Rau Củ",
        "brand": "Đà Lạt Farm",
        "name": "Cà Chua Beef Hữu Cơ",
        "description": "Cà chua Beef trái to tròn múp, dày cơm mọng nước vị ngọt thanh dịu nhẹ, xào nấu canh làm nước sốt cực chất lượng.",
        "sku": "VEG-006",
        "oldPrice": 38000.0,
        "stock": 120,
        "image": "https://images.unsplash.com/photo-1595855759920-86582396756a?w=600"
    },
    {
        "category": "Rau Củ",
        "brand": "Đà Lạt Farm",
        "name": "Bông Cải Xanh Đà Lạt",
        "description": "Bông cải xanh Đà Lạt búp to cứng cáp tươi rói chứa nhiều vitamin C và chất xơ, xào bò luộc chấm kho quẹt giòn ngọt ngon miệng.",
        "sku": "VEG-007",
        "oldPrice": 52000.0,
        "stock": 100,
        "image": "https://images.unsplash.com/photo-1584270354949-c26b0d5b4a0c?w=600"
    },
    {
        "category": "Rau Củ",
        "brand": "Đà Lạt Farm",
        "name": "Dưa Leo Baby Organic",
        "description": "Dưa leo baby organic quả nhỏ vỏ mỏng giòn rụm không hạt đắng, vị mát ngọt tự nhiên ăn sống trộn gỏi chấm muối ớt tuyệt vời.",
        "sku": "VEG-008",
        "oldPrice": 34000.0,
        "stock": 130,
        "image": "https://images.unsplash.com/photo-1604977042946-1eecc30f269e?w=600"
    },
    {
        "category": "Rau Củ",
        "brand": "Nông Trại Việt",
        "name": "Bí Đỏ Hồ Lô Dẻo Thơm",
        "description": "Bí đỏ hồ lô vỏ xanh dày, ruột vàng cam dẻo quánh bùi ngọt ngậy bổ mắt dưỡng não phù hợp nấu canh súp hầm xương.",
        "sku": "VEG-009",
        "oldPrice": 25000.0,
        "stock": 110,
        "image": "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=600"
    },
    {
        "category": "Rau Củ",
        "brand": "Đà Lạt Farm",
        "name": "Ớt Chuông Đà Lạt Nhiều Màu",
        "description": "Ớt chuông đỏ và vàng tươi mập dày cơm giòn tan, vị ngọt thanh không hăng xào mực bò nấu súp rực rỡ bắt mắt.",
        "sku": "VEG-010",
        "oldPrice": 65000.0,
        "stock": 80,
        "image": "https://images.unsplash.com/photo-1563565088-9134120202e7?w=600"
    },
    {
        "category": "Rau Củ",
        "brand": "LiveHealth Farm",
        "name": "Rau Muống Nước Sạch",
        "description": "Rau muống nước sạch hái tươi trong ngày, ngọn to đều giòn sần sật, luộc vắt chanh xào tỏi cực kỳ trôi cơm mùa hè.",
        "sku": "VEG-011",
        "oldPrice": 15000.0,
        "stock": 150,
        "image": "https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=600"
    },
    {
        "category": "Rau Củ",
        "brand": "LiveHealth Farm",
        "name": "Nấm Đùi Gà Tươi Giòn",
        "description": "Nấm đùi gà tươi thân mập thịt dai ngọt tự nhiên thơm đặc trưng của nấm nướng bơ xào lẩu chay cực ngon.",
        "sku": "VEG-012",
        "oldPrice": 60000.0,
        "stock": 70,
        "image": "https://images.unsplash.com/photo-1534422298391-e4f8c172dddb?w=600"
    },
    {
        "category": "Rau Củ",
        "brand": "LiveHealth Farm",
        "name": "Nấm Kim Châm Sạch",
        "description": "Nấm kim châm trắng tinh tươm sợi giòn dai ăn lẩu thái, bò cuộn nấm kim châm nướng thơm ngậy.",
        "sku": "VEG-013",
        "oldPrice": 18000.0,
        "stock": 160,
        "image": "https://images.unsplash.com/photo-1534422298391-e4f8c172dddb?w=600"
    },
    {
        "category": "Rau Củ",
        "brand": "Đà Lạt Farm",
        "name": "Hành Tây Đà Lạt Ngọt Dịu",
        "description": "Hành tây Đà Lạt quả to da bóng vàng, thịt ngọt ít cay hăng thích hợp trộn gỏi gà xào thịt bò nướng lò.",
        "sku": "VEG-014",
        "oldPrice": 25000.0,
        "stock": 100,
        "image": "https://images.unsplash.com/photo-1580149375544-436f596c2111?w=600"
    },
    {
        "category": "Rau Củ",
        "brand": "Đà Lạt Farm",
        "name": "Khoai Tây Đà Lạt Tươi Sạch",
        "description": "Khoai tây Đà Lạt da vàng nạc bột dẻo ngon nấu canh hầm chiên giòn khoai tây chiên bé nào cũng mê tít.",
        "sku": "VEG-015",
        "oldPrice": 32000.0,
        "stock": 120,
        "image": "https://images.unsplash.com/photo-1518977676601-b53f82aba655?w=600"
    },

    # === CATEGORY: Hoa Quả ===
    {
        "category": "Hoa Quả",
        "brand": "Vườn Nhà Fresh",
        "name": "Táo Envy New Zealand",
        "description": "Táo Envy nhập khẩu New Zealand chính ngạch trái to tròn đỏ thẫm giòn ngọt sắc đượm vị mọng nước thơm ngon cực chất lượng.",
        "sku": "FRUIT-001",
        "oldPrice": 185000.0,
        "stock": 60,
        "image": "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Hoa Quả",
        "brand": "Nông Trại Việt",
        "name": "Chuối Cavendish Hữu Cơ",
        "description": "Chuối tiêu hồng Cavendish trồng hữu cơ chín vàng thơm lừng ngọt đậm đà giàu kali tốt cho cơ bắp tim mạch hệ tiêu hóa.",
        "sku": "FRUIT-002",
        "oldPrice": 38000.0,
        "stock": 200,
        "image": "https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Hoa Quả",
        "brand": "Vườn Nhà Fresh",
        "name": "Nho Mẫu Đơn Nhật Bản",
        "description": "Nho sữa Mẫu Đơn nhập Nhật Bản chùm to quả xanh bóng giòn tan vị ngọt ngào sâu lắng hương thơm hoa hồng ngây ngất đẳng cấp thượng lưu.",
        "sku": "FRUIT-003",
        "oldPrice": 550000.0,
        "stock": 15,
        "image": "https://images.unsplash.com/photo-1537640538966-79f369143f8f?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Hoa Quả",
        "brand": "Mekong Fresh",
        "name": "Xoài Cát Hòa Lộc Loại 1",
        "description": "Xoài cát Hòa Lộc nổi tiếng Nam Bộ chuẩn loại 1 chín cây vỏ vàng ươm thịt dày ít xơ thơm nồng ngọt lịm tan chảy.",
        "sku": "FRUIT-004",
        "oldPrice": 120000.0,
        "stock": 50,
        "image": "https://images.unsplash.com/photo-1553279768-865429fa0078?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Hoa Quả",
        "brand": "Đà Lạt Farm",
        "name": "Dâu Tây Đà Lạt 500g",
        "description": "Dâu tây New Zealand trồng nhà kính Đà Lạt quả to đỏ căng mọng vị chua ngọt hài hòa tự nhiên thơm lừng giàu vitamin C.",
        "sku": "FRUIT-005",
        "oldPrice": 95000.0,
        "stock": 70,
        "image": "https://images.unsplash.com/photo-1464965911861-746a04b4bca6?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Hoa Quả",
        "brand": "Mekong Fresh",
        "name": "Cam Sành Vĩnh Long Căng Mọng",
        "description": "Cam sành Vĩnh Long da sần sùi nhiều nước, vị chua ngọt đậm đà phù hợp nhất để vắt nước uống giải nhiệt mùa hè tăng sức đề kháng.",
        "sku": "FRUIT-006",
        "oldPrice": 30000.0,
        "stock": 150,
        "image": "https://images.unsplash.com/photo-1547514701-42782101795e?w=600"
    },
    {
        "category": "Hoa Quả",
        "brand": "Mekong Fresh",
        "name": "Dưa Hấu Không Hạt",
        "description": "Dưa hấu không hạt ruột đỏ tươi, ngọt lịm giòn tan chứa nhiều nước giải khát hiệu quả tốt cho sức khỏe cả gia đình.",
        "sku": "FRUIT-007",
        "oldPrice": 25000.0,
        "stock": 120,
        "image": "https://images.unsplash.com/photo-1587049352846-4a222e784d38?w=600"
    },
    {
        "category": "Hoa Quả",
        "brand": "Mekong Fresh",
        "name": "Bưởi Da Xanh Bến Tre Ruột Hồng",
        "description": "Bưởi da xanh Bến Tre múi bưởi hồng to ráo nước vị ngọt thanh mát lịm tốt cho hệ miễn dịch thanh lọc cơ thể cực đỉnh.",
        "sku": "FRUIT-008",
        "oldPrice": 75000.0,
        "stock": 90,
        "image": "https://images.unsplash.com/photo-1557800636-894a64c1696f?w=600"
    },
    {
        "category": "Hoa Quả",
        "brand": "Mekong Fresh",
        "name": "Quýt Đường Miền Tây",
        "description": "Quýt đường miền tây vỏ mỏng ruột mọng nước ngọt thanh như đường phèn, tép bưởi tươi thơm dễ bóc cho bé ăn vặt lành mạnh.",
        "sku": "FRUIT-009",
        "oldPrice": 48000.0,
        "stock": 100,
        "image": "https://images.unsplash.com/photo-1547514701-42782101795e?w=600"
    },
    {
        "category": "Hoa Quả",
        "brand": "Mekong Fresh",
        "name": "Mận An Phước Loại 1",
        "description": "Mận roi An Phước quả to màu đỏ sẫm hình chuông giòn ngọt nhiều nước cực giải khát cho ngày hè nóng nực.",
        "sku": "FRUIT-010",
        "oldPrice": 45000.0,
        "stock": 110,
        "image": "https://images.unsplash.com/photo-1598902108854-10e335adac99?w=600"
    },
    {
        "category": "Hoa Quả",
        "brand": "Vườn Nhà Fresh",
        "name": "Kiwi Vàng New Zealand",
        "description": "Kiwi vàng Zespri New Zealand nhập khẩu quả to ngọt ngậy thơm nhẹ, giàu vitamin C E chất chống oxy hóa cực đỉnh.",
        "sku": "FRUIT-011",
        "oldPrice": 140000.0,
        "stock": 65,
        "image": "https://images.unsplash.com/photo-1585059895524-72359e06133a?w=600"
    },
    {
        "category": "Hoa Quả",
        "brand": "Vườn Nhà Fresh",
        "name": "Việt Quất Tươi Nhập Khẩu 125g",
        "description": "Hộp việt quất tươi nhập khẩu Mỹ ruột giòn mọng vị chua ngọt đượm vị cực tốt cho trí não và sáng mắt mỗi ngày.",
        "sku": "FRUIT-012",
        "oldPrice": 85000.0,
        "stock": 50,
        "image": "https://images.unsplash.com/photo-1498557850523-fd3d118b962e?w=600"
    },
    {
        "category": "Hoa Quả",
        "brand": "Vườn Nhà Fresh",
        "name": "Cherry Đỏ Mỹ Size Lớn",
        "description": "Cherry đỏ Mỹ trái to chắc giòn ngọt mọng nước màu đỏ sẫm sang trọng quý phái giàu dinh dưỡng sắt bổ máu cực kỳ.",
        "sku": "FRUIT-013",
        "oldPrice": 390000.0,
        "stock": 20,
        "image": "https://images.unsplash.com/photo-1527661591475-527312dd65f5?w=600"
    },
    {
        "category": "Hoa Quả",
        "brand": "Mekong Fresh",
        "name": "Thanh Long Ruột Đỏ",
        "description": "Thanh long ruột đỏ tươi Bình Thuận ngọt đậm nhiều nước nhiều hạt nhỏ tốt cho tiêu hóa nhuận tràng giải độc cơ thể.",
        "sku": "FRUIT-014",
        "oldPrice": 35000.0,
        "stock": 130,
        "image": "https://images.unsplash.com/photo-1506806732259-39c2d0268443?w=600"
    },
    {
        "category": "Hoa Quả",
        "brand": "Mekong Fresh",
        "name": "Măng Cụt Chợ Lách Bến Tre",
        "description": "Măng cụt Chợ Lách vỏ mỏng múi trắng tinh như tuyết, chua ngọt thanh mát cực thơm ngon dễ bóc ăn hoài không chán.",
        "sku": "FRUIT-015",
        "oldPrice": 85000.0,
        "stock": 60,
        "image": "https://images.unsplash.com/photo-1553279768-865429fa0078?w=600"
    },

    # === CATEGORY: Gạo & Đồ khô ===
    {
        "category": "Gạo & Đồ khô",
        "brand": "Sóc Trăng Rice",
        "name": "Gạo ST25 Sóc Trăng 5kg",
        "description": "Gạo ST25 Sóc Trăng chuẩn hạt dài dẻo nhiều, thơm mùi lá dứa ngào ngạt cơm nguội vẫn dẻo ngon cực chất lượng.",
        "sku": "DRY-001",
        "oldPrice": 190000.0,
        "stock": 100,
        "image": "https://images.unsplash.com/photo-1586201375761-83865001e31c?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Gạo & Đồ khô",
        "brand": "Mekong Fresh",
        "name": "Hạt Điều Rang Muối Bình Phước",
        "description": "Hạt điều vỏ lụa rang muối Bình Phước hạt to tròn loại A giòn rụm vị mặn nhẹ béo ngậy cực đưa miệng bổ dưỡng.",
        "sku": "DRY-002",
        "oldPrice": 140000.0,
        "stock": 80,
        "image": "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Gạo & Đồ khô",
        "brand": "Nông Trại Việt",
        "name": "Đậu Xanh Tách Vỏ Sạch 500g",
        "description": "Đậu xanh tách vỏ sạch sẽ hạt đều tăm tắp, lý tưởng nấu chè nấu xôi làm bánh bánh chưng xanh ngậy bùi ngọt dịu.",
        "sku": "DRY-003",
        "oldPrice": 35000.0,
        "stock": 120,
        "image": "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Gạo & Đồ khô",
        "brand": "Nông Trại Việt",
        "name": "Mì Chũ Bắc Giang Truyền Thống",
        "description": "Mì gạo chũ Bắc Giang làm từ gạo bao thai hồng dẻo dai tự nhiên nấu ăn sáng lẩu xào không nát cực ngon miệng.",
        "sku": "DRY-004",
        "oldPrice": 45000.0,
        "stock": 90,
        "image": "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Gạo & Đồ khô",
        "brand": "Mekong Fresh",
        "name": "Hạt Sen Khô Đồng Tháp",
        "description": "Hạt sen khô Đồng Tháp chọn lọc hạt to tròn đều tăm tắp hầm mau nhừ béo bùi nấu chè hạt sen long nhãn cực bổ dưỡng dưỡng tâm an thần.",
        "sku": "DRY-005",
        "oldPrice": 95000.0,
        "stock": 70,
        "image": "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&q=80&w=600"
    },
    {
        "category": "Gạo & Đồ khô",
        "brand": "Nông Trại Việt",
        "name": "Đậu Đen Xanh Lòng 500g",
        "description": "Đậu đen xanh lòng hạt nhỏ chắc dẻo bùi nấu nước uống thanh nhiệt mát gan giải độc cơ thể cực bổ dưỡng ngày hè.",
        "sku": "DRY-006",
        "oldPrice": 32000.0,
        "stock": 110,
        "image": "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=600"
    },
    {
        "category": "Gạo & Đồ khô",
        "brand": "Nông Trại Việt",
        "name": "Đậu Phộng Nhân Loại A 500g",
        "description": "Đậu phộng nhân lạc nhân to tròn da đỏ căng bóng chắc giòn bùi làm muối lạc rang nhậu tẩm bột vô cùng béo ngậy.",
        "sku": "DRY-007",
        "oldPrice": 28000.0,
        "stock": 130,
        "image": "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=600"
    },
    {
        "category": "Gạo & Đồ khô",
        "brand": "Nông Trại Việt",
        "name": "Nấm Hương Rừng Khô Tây Bắc",
        "description": "Nấm hương rừng khô thu hoạch vùng núi cao Tây Bắc thơm lừng nồng nàn chân nấm ngắn tai nấm dày mập làm nhân giò chả canh măng.",
        "sku": "DRY-008",
        "oldPrice": 120000.0,
        "stock": 60,
        "image": "https://images.unsplash.com/photo-1534422298391-e4f8c172dddb?w=600"
    },
    {
        "category": "Gạo & Đồ khô",
        "brand": "Nông Trại Việt",
        "name": "Mộc Nhĩ Đen Khô Sạch",
        "description": "Mộc nhĩ tai mèo khô cánh dày đen giòn rụm làm nem rán giò xào chân giò hầm măng dòn dai sần sật cực đỉnh.",
        "sku": "DRY-009",
        "oldPrice": 75000.0,
        "stock": 80,
        "image": "https://images.unsplash.com/photo-1534422298391-e4f8c172dddb?w=600"
    },
    {
        "category": "Gạo & Đồ khô",
        "brand": "Mekong Fresh",
        "name": "Hạnh Nhân Sấy Mộc Mỹ 250g",
        "description": "Hạt hạnh nhân nhập khẩu Mỹ sấy mộc nguyên chất giòn tan béo bùi tự nhiên không muối tẩm tốt cho tim mạch trí não bà bầu trẻ em.",
        "sku": "DRY-010",
        "oldPrice": 160000.0,
        "stock": 50,
        "image": "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=600"
    },
    {
        "category": "Gạo & Đồ khô",
        "brand": "Mekong Fresh",
        "name": "Hạt Macca Đắk Lắk Nứt Vỏ",
        "description": "Hạt Macca Đắk Lắk nứt vỏ tự nhiên hạt to bùi béo ngậy ngọt nhẹ kèm dụng cụ tách hạt tiện lợi dinh dưỡng vàng cực bổ.",
        "sku": "DRY-011",
        "oldPrice": 165000.0,
        "stock": 65,
        "image": "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=600"
    },
    {
        "category": "Gạo & Đồ khô",
        "brand": "Nông Trại Việt",
        "name": "Bột Mì Đa Dụng Cao Cấp 1kg",
        "description": "Bột mì đa dụng cao cấp thích hợp làm bánh mì bánh bao bánh bông lan thơm ngon dẻo xốp mịn màng.",
        "sku": "DRY-012",
        "oldPrice": 30000.0,
        "stock": 140,
        "image": "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=600"
    },
    {
        "category": "Gạo & Đồ khô",
        "brand": "Nông Trại Việt",
        "name": "Miến Dong Cao Bằng Sạch",
        "description": "Miến dong Cao Bằng thủ công truyền thống làm từ củ dong riềng dai ngon không hóa chất tẩy rửa nấu súp xào lẩu cực đỉnh.",
        "sku": "DRY-013",
        "oldPrice": 50000.0,
        "stock": 90,
        "image": "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=600"
    },
    {
        "category": "Gạo & Đồ khô",
        "brand": "Sóc Trăng Rice",
        "name": "Gạo Lứt Đỏ Huyết Rồng 1kg",
        "description": "Gạo lứt đỏ huyết rồng dẻo bùi giàu chất xơ vitamin nhóm B hỗ trợ đắc lực cho người ăn kiêng giảm cân tiểu đường tập thể hình.",
        "sku": "DRY-014",
        "oldPrice": 45000.0,
        "stock": 120,
        "image": "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=600"
    },
    {
        "category": "Gạo & Đồ khô",
        "brand": "Sóc Trăng Rice",
        "name": "Gạo Nếp Nương Điện Biên 2kg",
        "description": "Gạo nếp nương vùng cao Điện Biên chuẩn hạt to tròn béo ngọt, nấu xôi dẻo quánh thơm ngậy gói bánh chưng cực phẩm.",
        "sku": "DRY-015",
        "oldPrice": 65000.0,
        "stock": 80,
        "image": "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=600"
    }
]

print(f"\n🚀 Creating {len(products_data)} Products & injecting Unsplash images...")
success_count = 0
for idx, p in enumerate(products_data):
    cat_id = categories.get(p["category"])
    brand_id = brands.get(p["brand"])
    
    if not cat_id or not brand_id:
        print(f" ❌ Skipping {p['name']}: Category '{p['category']}' or Brand '{p['brand']}' not found.")
        continue
    
    # POST to create product
    prod_data = {
        "name": p["name"],
        "description": p["description"],
        "oldPrice": p["oldPrice"],
        "stock": p["stock"],
        "sku": p["sku"],
        "brandId": brand_id,
        "categoryId": cat_id,
        "tagIds": [tag_ids[idx % len(tag_ids)]] if tag_ids else []
    }
    
    res = api_request("/products", method="POST", data=prod_data, token=TOKEN)
    
    if res.get("status") == "SUCCESS" and "data" in res:
        prod_uuid = res["data"]["id"]
        # Inject Unsplash image via SQL
        img_sql = f"INSERT INTO product_image (product_id, image_url) VALUES (UUID_TO_BIN('{prod_uuid}'), '{p['image']}');"
        if run_sql(img_sql):
            print(f" [{idx+1}/{len(products_data)}] ✅ {p['name']} created and image injected.")
            success_count += 1
        else:
            print(f" [{idx+1}/{len(products_data)}] ⚠️ {p['name']} created but image SQL failed.")
    else:
        print(f" [{idx+1}/{len(products_data)}] ❌ Failed to create {p['name']}: {res}")

print("\n=========================================")
print(f"  🌿 Large data seeding complete! ({success_count}/{len(products_data)} products)")
print("=========================================")
