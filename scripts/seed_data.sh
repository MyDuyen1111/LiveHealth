#!/bin/bash
# Seed LiveHealth database with agricultural sample data
# Usage: bash scripts/seed_data.sh
set -e

API="${API:-http://localhost:62080/api/v1}"

echo "🔑 Getting admin token..."
TOKEN=$(curl -s -X POST "$API/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@livehealth.com","password":"Admin@123"}' \
  | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['accessToken'])")

AUTH="Authorization: Bearer $TOKEN"

post() { curl -s -X POST "$API$1" -H "Content-Type: application/json" -H "$AUTH" -d "$2"; }
postForm() { curl -s -X POST "$API$1" -H "$AUTH" -F "file=@$2"; }

echo ""
echo "📦 Creating Categories..."
for name in "Rau Củ" "Hoa Quả" "Thịt" "Cá & Hải sản" "Trứng & Sữa" "Gạo & Đồ khô"; do
  post "/categories" "{\"name\":\"$name\",\"description\":\"Danh mục $name tươi sạch, chất lượng cao\"}"
  echo " ✅ $name"
done

echo ""
echo "🏷️ Creating Brands..."
for name in "LiveHealth Farm" "Đà Lạt Farm" "Mekong Fresh" "Cà Mau Seafood" "Ba Vì Dairy" "Sóc Trăng Rice" "Nông Trại Việt" "Vườn Nhà Fresh"; do
  post "/brands" "{\"name\":\"$name\",\"description\":\"Thương hiệu $name\"}"
  echo " ✅ $name"
done

echo ""
echo "🔖 Creating Tags..."
for name in "Tươi trong ngày" "VietGAP" "Theo mùa" "Bán chạy" "Mới thu hoạch" "Đặc sản vùng miền" "Nguồn gốc rõ ràng" "Giao lạnh"; do
  post "/tags" "{\"name\":\"$name\"}"
  echo " ✅ $name"
done

echo ""
echo "💳 Creating Payment Methods..."
post "/payment-methods" '{"name":"Thanh toán khi nhận hàng (COD)","isActive":true}'
echo " ✅ COD"
post "/payment-methods" '{"name":"Chuyển khoản ngân hàng","isActive":true}'
echo " ✅ Bank Transfer"
post "/payment-methods" '{"name":"Ví MoMo","isActive":true}'
echo " ✅ MoMo"
post "/payment-methods" '{"name":"VNPay","isActive":true}'
echo " ✅ VNPay"

echo ""
echo "🚚 Creating Shipping Methods..."
post "/shipping-methods" '{"name":"Giao hàng tiêu chuẩn","description":"Giao trong 3-5 ngày","price":15000,"isActive":true}'
echo " ✅ Standard"
post "/shipping-methods" '{"name":"Giao hàng nhanh","description":"Giao trong 1-2 ngày","price":20000,"isActive":true}'
echo " ✅ Express"
post "/shipping-methods" '{"name":"Giao hàng hỏa tốc","description":"Giao trong 2 giờ (nội thành)","price":30000,"isActive":true}'
echo " ✅ Same Day"

# Get category IDs
echo ""
echo "📋 Fetching category IDs..."
CATS=$(curl -s "$API/categories")
get_cat_id() { echo "$CATS" | python3 -c "import sys,json; cats=json.load(sys.stdin)['data']['items']; print(next(c['id'] for c in cats if '$1' in c['name']))"; }

CAT_MEAT=$(get_cat_id "Thịt")
CAT_FISH=$(get_cat_id "Cá")
CAT_DAIRY=$(get_cat_id "Trứng")
CAT_VEG=$(get_cat_id "Rau")
CAT_FRUIT=$(get_cat_id "Hoa")

# Get brand IDs
BRANDS=$(curl -s "$API/brands")
get_brand_id() { echo "$BRANDS" | python3 -c "import sys,json; brands=json.load(sys.stdin)['data']['items']; print(next(b['id'] for b in brands if '$1' in b['name']))"; }

BRAND_LH=$(get_brand_id "LiveHealth")
BRAND_OG=$(get_brand_id "Nông Trại")
BRAND_TH=$(get_brand_id "Ba Vì")
BRAND_VN=$(get_brand_id "Ba Vì")
BRAND_MD=$(get_brand_id "Nông Trại")
BRAND_AQ=$(get_brand_id "Cà Mau")
BRAND_DL=$(get_brand_id "Đà Lạt")
BRAND_MK=$(get_brand_id "Mekong")

echo ""
echo "🥩 Creating Products - Thịt..."
post "/products" "{\"name\":\"Thịt Bò Úc Hữu Cơ\",\"description\":\"Thịt bò Úc nhập khẩu, nuôi tự nhiên, không hormone. Thích hợp nướng, xào, bò lúc lắc.\",\"sku\":\"MEAT-001\",\"oldPrice\":320000,\"stock\":50,\"categoryId\":\"$CAT_MEAT\",\"brandId\":\"$BRAND_MD\"}"
echo " ✅"
post "/products" "{\"name\":\"Thịt Heo Ba Chỉ Sạch\",\"description\":\"Thịt heo ba chỉ từ trang trại sạch, không chất tăng trọng. Phù hợp kho, nướng, luộc.\",\"sku\":\"MEAT-002\",\"oldPrice\":180000,\"stock\":80,\"categoryId\":\"$CAT_MEAT\",\"brandId\":\"$BRAND_MD\"}"
echo " ✅"
post "/products" "{\"name\":\"Ức Gà Phi Lê Tươi\",\"description\":\"Ức gà phi lê ít mỡ, giàu protein. Lý tưởng cho người tập gym và ăn kiêng.\",\"sku\":\"MEAT-003\",\"oldPrice\":95000,\"stock\":100,\"categoryId\":\"$CAT_MEAT\",\"brandId\":\"$BRAND_LH\"}"
echo " ✅"
post "/products" "{\"name\":\"Thịt Bò Wagyu A5 Nhật Bản\",\"description\":\"Thịt bò Wagyu hạng A5 nhập khẩu Nhật Bản, vân mỡ đẹp, tan trong miệng.\",\"sku\":\"MEAT-004\",\"oldPrice\":1200000,\"stock\":10,\"categoryId\":\"$CAT_MEAT\",\"brandId\":\"$BRAND_MD\"}"
echo " ✅"
post "/products" "{\"name\":\"Sườn Non Heo Hữu Cơ\",\"description\":\"Sườn non heo hữu cơ, thịt mềm ngọt tự nhiên. Thích hợp nấu canh, kho, nướng.\",\"sku\":\"MEAT-005\",\"oldPrice\":220000,\"stock\":60,\"categoryId\":\"$CAT_MEAT\",\"brandId\":\"$BRAND_OG\"}"
echo " ✅"

echo ""
echo "🐟 Creating Products - Cá & Hải sản..."
post "/products" "{\"name\":\"Cá Hồi Na Uy Phi Lê\",\"description\":\"Cá hồi Na Uy tươi sống, giàu Omega-3 và DHA. Ăn sashimi hoặc áp chảo.\",\"sku\":\"FISH-001\",\"oldPrice\":450000,\"stock\":30,\"categoryId\":\"$CAT_FISH\",\"brandId\":\"$BRAND_AQ\"}"
echo " ✅"
post "/products" "{\"name\":\"Tôm Sú Sống Size Lớn\",\"description\":\"Tôm sú sống size lớn 15-20 con/kg, thịt chắc ngọt. Hấp, nướng, lẩu đều ngon.\",\"sku\":\"FISH-002\",\"oldPrice\":350000,\"stock\":40,\"categoryId\":\"$CAT_FISH\",\"brandId\":\"$BRAND_MK\"}"
echo " ✅"
post "/products" "{\"name\":\"Cá Thu Cắt Khúc\",\"description\":\"Cá thu tươi cắt khúc, thịt dày béo. Kho, chiên, nướng đều tuyệt vời.\",\"sku\":\"FISH-003\",\"oldPrice\":180000,\"stock\":50,\"categoryId\":\"$CAT_FISH\",\"brandId\":\"$BRAND_MK\"}"
echo " ✅"
post "/products" "{\"name\":\"Nghêu Lụa Tươi 1kg\",\"description\":\"Nghêu lụa tươi sống, thịt ngọt thanh. Xào tỏi, nấu canh chua, hấp sả.\",\"sku\":\"FISH-004\",\"oldPrice\":85000,\"stock\":70,\"categoryId\":\"$CAT_FISH\",\"brandId\":\"$BRAND_AQ\"}"
echo " ✅"

echo ""
echo "🥚 Creating Products - Trứng & Sữa..."
post "/products" "{\"name\":\"Trứng Gà Hữu Cơ (10 quả)\",\"description\":\"Trứng gà hữu cơ từ gà thả vườn, lòng đỏ đậm, giàu dinh dưỡng.\",\"sku\":\"DAIRY-001\",\"oldPrice\":55000,\"stock\":200,\"categoryId\":\"$CAT_DAIRY\",\"brandId\":\"$BRAND_OG\"}"
echo " ✅"
post "/products" "{\"name\":\"Sữa Tươi Ba Vì 1L\",\"description\":\"Sữa tươi thanh trùng Ba Vì, vị béo nhẹ, giao lạnh trong ngày.\",\"sku\":\"DAIRY-002\",\"oldPrice\":38000,\"stock\":150,\"categoryId\":\"$CAT_DAIRY\",\"brandId\":\"$BRAND_TH\"}"
echo " ✅"
post "/products" "{\"name\":\"Sữa Chua Ba Vì Không Đường\",\"description\":\"Sữa chua không đường từ sữa tươi Ba Vì, vị thanh, dùng kèm trái cây hoặc yến mạch.\",\"sku\":\"DAIRY-003\",\"oldPrice\":32000,\"stock\":120,\"categoryId\":\"$CAT_DAIRY\",\"brandId\":\"$BRAND_VN\"}"
echo " ✅"
post "/products" "{\"name\":\"Phô Mai Con Bò Cười (8 miếng)\",\"description\":\"Phô mai Con Bò Cười giàu canxi, bổ sung dinh dưỡng cho trẻ em.\",\"sku\":\"DAIRY-004\",\"oldPrice\":45000,\"stock\":90,\"categoryId\":\"$CAT_DAIRY\",\"brandId\":\"$BRAND_VN\"}"
echo " ✅"
post "/products" "{\"name\":\"Bơ Lạt Anchor 227g\",\"description\":\"Bơ lạt Anchor nhập khẩu New Zealand, thơm béo tự nhiên.\",\"sku\":\"DAIRY-005\",\"oldPrice\":75000,\"stock\":60,\"categoryId\":\"$CAT_DAIRY\",\"brandId\":\"$BRAND_LH\"}"
echo " ✅"

echo ""
echo "🥬 Creating Products - Rau Củ..."
post "/products" "{\"name\":\"Rau Cải Bó Xôi Hữu Cơ\",\"description\":\"Cải bó xôi (spinach) hữu cơ, giàu sắt và vitamin K. Luộc, xào, làm salad.\",\"sku\":\"VEG-001\",\"oldPrice\":35000,\"stock\":100,\"categoryId\":\"$CAT_VEG\",\"brandId\":\"$BRAND_DL\"}"
echo " ✅"
post "/products" "{\"name\":\"Cà Rốt Đà Lạt 1kg\",\"description\":\"Cà rốt Đà Lạt tươi, ngọt giòn, giàu beta-carotene tốt cho mắt.\",\"sku\":\"VEG-002\",\"oldPrice\":28000,\"stock\":150,\"categoryId\":\"$CAT_VEG\",\"brandId\":\"$BRAND_DL\"}"
echo " ✅"
post "/products" "{\"name\":\"Bắp Cải Tím Organic\",\"description\":\"Bắp cải tím organic, giàu chất chống oxy hóa. Làm salad, xào, muối chua.\",\"sku\":\"VEG-003\",\"oldPrice\":42000,\"stock\":80,\"categoryId\":\"$CAT_VEG\",\"brandId\":\"$BRAND_OG\"}"
echo " ✅"
post "/products" "{\"name\":\"Khoai Lang Nhật\",\"description\":\"Khoai lang Nhật ruột vàng, ngọt bùi. Hấp, nướng, nấu chè đều ngon.\",\"sku\":\"VEG-004\",\"oldPrice\":45000,\"stock\":90,\"categoryId\":\"$CAT_VEG\",\"brandId\":\"$BRAND_DL\"}"
echo " ✅"
post "/products" "{\"name\":\"Bơ Sáp Đắk Lắk\",\"description\":\"Bơ sáp Đắk Lắk loại 1, ruột vàng béo, dẻo mịn. Ăn trực tiếp hoặc làm sinh tố.\",\"sku\":\"VEG-005\",\"oldPrice\":68000,\"stock\":40,\"categoryId\":\"$CAT_VEG\",\"brandId\":\"$BRAND_LH\"}"
echo " ✅"

echo ""
echo "🍎 Creating Products - Hoa Quả..."
post "/products" "{\"name\":\"Táo Envy New Zealand\",\"description\":\"Táo Envy nhập khẩu New Zealand, giòn ngọt thanh, giàu vitamin C.\",\"sku\":\"FRUIT-001\",\"oldPrice\":185000,\"stock\":60,\"categoryId\":\"$CAT_FRUIT\",\"brandId\":\"$BRAND_LH\"}"
echo " ✅"
post "/products" "{\"name\":\"Chuối Cavendish Hữu Cơ\",\"description\":\"Chuối Cavendish hữu cơ, ngọt tự nhiên, giàu kali tốt cho tim mạch.\",\"sku\":\"FRUIT-002\",\"oldPrice\":38000,\"stock\":200,\"categoryId\":\"$CAT_FRUIT\",\"brandId\":\"$BRAND_OG\"}"
echo " ✅"
post "/products" "{\"name\":\"Nho Mẫu Đơn Nhật Bản\",\"description\":\"Nho Mẫu Đơn Nhật Bản, hạt nhỏ, vị ngọt đậm đà, thơm mùi hoa hồng.\",\"sku\":\"FRUIT-003\",\"oldPrice\":550000,\"stock\":15,\"categoryId\":\"$CAT_FRUIT\",\"brandId\":\"$BRAND_LH\"}"
echo " ✅"
post "/products" "{\"name\":\"Xoài Cát Hòa Lộc\",\"description\":\"Xoài cát Hòa Lộc loại 1, thịt dày, ngọt thơm, không xơ.\",\"sku\":\"FRUIT-004\",\"oldPrice\":120000,\"stock\":50,\"categoryId\":\"$CAT_FRUIT\",\"brandId\":\"$BRAND_MK\"}"
echo " ✅"
post "/products" "{\"name\":\"Dâu Tây Đà Lạt 500g\",\"description\":\"Dâu tây Đà Lạt tươi, vị chua ngọt tự nhiên, giàu vitamin C và chất chống oxy hóa.\",\"sku\":\"FRUIT-005\",\"oldPrice\":95000,\"stock\":70,\"categoryId\":\"$CAT_FRUIT\",\"brandId\":\"$BRAND_DL\"}"
echo " ✅"

echo ""
echo "👤 Creating Demo User..."
post "/admin/create-user" '{"email":"user@livehealth.vn","password":"User123@","firstName":"Khách","lastName":"LiveHealth","phone":"0901234567","role":"USER"}' >/dev/null
echo " ✅ user@livehealth.vn / User123@"

echo ""
echo "========================================="
echo "  🌿 Seed data complete!"
echo "========================================="
echo "  Categories:       5"
echo "  Brands:           8"
echo "  Tags:             8"
echo "  Products:         24"
echo "  Payment Methods:  4"
echo "  Shipping Methods: 3"
echo "  Demo User:        user@livehealth.vn / User123@"
echo "========================================="
