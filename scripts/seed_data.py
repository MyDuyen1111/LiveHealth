import os, re, sys, urllib.request, json
api_port = "62080"
admin_pass = "Admin@123"
try:
    with open(os.path.join(os.path.dirname(os.path.dirname(__file__)), "backend", ".env")) as f:
        env_content = f.read()
        match_port = re.search(r"^SPRING_SERVER_PORT=(\d+)", env_content, re.M)
        if match_port: api_port = match_port.group(1).strip()
        match_pass = re.search(r"^ADMIN_PASSWORD=(.*)", env_content, re.M)
        if match_pass: admin_pass = match_pass.group(1).strip()
except Exception as e: pass

API = f"http://localhost:{api_port}/api/v1"

print("[*] Getting admin token...")
try:
    req = urllib.request.Request(f"{API}/auth/login", data=json.dumps({"email":"admin@livehealth.com","password":admin_pass}).encode(), headers={"Content-Type": "application/json"})
    res = json.loads(urllib.request.urlopen(req).read().decode())
    TOKEN = res["data"]["accessToken"]
except Exception as e:
    print("ERROR: Failed to get Token! Check if Backend is running or credentials are correct.")
    print(e)
    sys.exit(1)

H = {"Authorization": f"Bearer {TOKEN}", "Content-Type": "application/json"}

def post(endpoint, data):
    try:
        req = urllib.request.Request(f"{API}{endpoint}", data=json.dumps(data).encode(), headers=H, method='POST')
        urllib.request.urlopen(req)
        print(f" ✅ {data.get('name')}")
    except Exception as e:
        print(f" ❌ Failed {data.get('name')}: {e}")

print("\n📦 Creating Categories...")
for name in ["Rau Củ", "Hoa Quả", "Thịt", "Cá & Hải sản", "Trứng & Sữa", "Gạo & Đồ khô"]:
    post("/categories", {"name":name,"description":f"Danh mục {name}"})

print("\n🏷️ Creating Brands...")
for name in ["LiveHealth Farm", "Đà Lạt Farm", "Mekong Fresh", "Cà Mau Seafood", "Ba Vì Dairy", "Sóc Trăng Rice", "Nông Trại Việt", "Vườn Nhà Fresh"]:
    post("/brands", {"name":name,"description":f"Thương hiệu {name}"})

print("\n🔖 Creating Tags...")
for name in ["Tươi trong ngày", "VietGAP", "Theo mùa", "Bán chạy", "Mới thu hoạch", "Đặc sản vùng miền"]:
    post("/tags", {"name":name})

print("\n💳 Creating Payment Methods...")
post("/payment-methods", {"name":"Thanh toán khi nhận hàng (COD)","isActive":True})
post("/payment-methods", {"name":"VNPay","isActive":True})

print("\n🚚 Creating Shipping Methods...")
post("/shipping-methods", {"name":"Giao hàng tiêu chuẩn","description":"3-5 ngày","price":15000,"isActive":True})
post("/shipping-methods", {"name":"Giao hàng nhanh","description":"1-2 ngày","price":20000,"isActive":True})
post("/shipping-methods", {"name":"Giao hàng hỏa tốc","description":"2 giờ","price":30000,"isActive":True})

print("\n=========================================")
print("  🌿 Seed data complete!")
print("=========================================")
