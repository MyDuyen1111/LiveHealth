# LiveHealth

LiveHealth là website thương mại điện tử thực phẩm sạch kết hợp trợ lý AI tư vấn sức khỏe. Hệ thống hỗ trợ khách hàng xem sản phẩm, tìm kiếm/lọc/sắp xếp, quản lý giỏ hàng, đặt hàng, thanh toán VNPay và xem/hủy đơn hàng; đồng thời cung cấp trang quản trị (Admin) toàn diện để quản lý sản phẩm, danh mục, đơn hàng, bài viết và dữ liệu hệ thống.

Hệ thống backend của LiveHealth được xây dựng trên nền tảng **Quarkus 3.12.0** với kiến trúc Reactive non-blocking (JAX-RS Resteasy Reactive & Vert.x), giúp tối ưu hiệu năng và tài nguyên.

## Chức năng chính

- **Đăng ký / Đăng nhập / Đăng xuất**: Xác thực an toàn bằng JWT.
- **Mua sắm**: Xem danh sách sản phẩm, chi tiết sản phẩm, tìm kiếm, lọc danh mục, sắp xếp giá.
- **Giỏ hàng**: Thêm sản phẩm, cập nhật số lượng, áp dụng mã giảm giá, chọn đơn vị vận chuyển/thanh toán.
- **Thanh toán & Đơn hàng**: Tích hợp cổng thanh toán VNPay Sandbox, đặt hàng COD/chuyển khoản, xem lịch sử mua hàng, và hủy đơn hàng.
- **AI Sức khỏe**: Trò chuyện tư vấn dinh dưỡng trực tuyến, tính chỉ số BMI/BMR/TDEE, tự động gợi ý thực đơn và sản phẩm phù hợp.
- **Tin tức & Blog**: Hiển thị các bài viết chia sẻ kiến thức sống sạch và khỏe.
- **Quản trị (Admin)**: Quản lý sản phẩm, danh mục, thương hiệu, tag, đơn hàng, cấu hình vận chuyển/thanh toán, tin tức, thành viên, và phản hồi góp ý.

## Công nghệ sử dụng

| Thành phần | Công nghệ |
| --- | --- |
| **Frontend** | React 19, Vite 8, React Router, Lucide Icons |
| **Backend** | Quarkus 3.12.0, Java 21, Maven, RESTEasy Reactive, Hibernate ORM (Panache), Narayana JTA |
| **AI Service** | Python, FastAPI, OpenAI-compatible API (LLM Integration) |
| **Database** | MySQL 8 |
| **Cache** | Redis |
| **Tài liệu API** | OpenAPI (Swagger UI) |

## Cấu trúc thư mục

```text
LiveHealth/
├── frontend/                 # React SPA (Vite + React Router)
│   ├── src/pages/            # Các trang chính: Home, Shop, Checkout, HealthAI, Admin...
│   ├── src/components/       # Header, Footer, AccountSidebar, các UI elements...
│   ├── src/context/          # State toàn cục: AuthContext, CartContext...
│   └── src/api/              # REST Client gọi API Backend
├── backend/                  # Quarkus REST API Server
│   ├── src/main/java/com/livehealth/
│   │   ├── auth/             # Quản lý xác thực JWT & OTP
│   │   ├── cart/             # Quản lý giỏ hàng & sản phẩm yêu thích
│   │   ├── order/            # Xử lý đơn hàng, giao hàng & thanh toán
│   │   ├── product/          # Quản lý sản phẩm, danh mục, thương hiệu, tag & khuyến mãi
│   │   ├── news/             # Hệ thống tin tức, danh mục bài viết & bình luận
│   │   ├── payment/          # Tích hợp VNPay thanh toán & webhook IPN
│   │   ├── user/             # Hồ sơ cá nhân, địa chỉ nhận hàng
│   │   └── shared/           # Cấu hình chung, xử lý lỗi ngoại lệ, phân trang & bảo mật
│   └── src/main/resources/   # File application.properties & file SQL schema
├── ai-service/               # FastAPI service kết nối mô hình ngôn ngữ lớn (LLM)
└── scripts/                  # Bộ script tự động cài đặt hệ thống, khởi chạy & seed dữ liệu
```

## URL chạy local

| Service | URL |
| --- | --- |
| **Frontend** | [http://localhost:62173](http://localhost:62173) |
| **Backend API** | [http://localhost:62080](http://localhost:62080) |
| **Swagger UI** | [http://localhost:62080/swagger-ui](http://localhost:62080/swagger-ui) |
| **OpenAPI Docs** | [http://localhost:62080/v3/api-docs](http://localhost:62080/v3/api-docs) |
| **AI Service** | [http://localhost:62000](http://localhost:62000) |
| **AI Docs** | [http://localhost:62000/docs](http://localhost:62000/docs) |
| **MySQL Database** | `localhost:62307` |
| **Redis Cache** | `localhost:62380` |

## Tài khoản test hệ thống

Khi backend khởi động lần đầu, hệ thống sẽ tự động tạo/cập nhật các tài khoản thử nghiệm sau:

| Vai trò | Email | Mật khẩu | Ghi chú |
| --- | --- | --- | --- |
| **Quản trị viên (Admin)** | `admin@livehealth.com` | `Admin@123` | Có toàn quyền truy cập trang Admin Dashboard |
| **Khách hàng mẫu (User)** | `user@livehealth.vn` | `User123@` | Tài khoản thường phục vụ trải nghiệm đặt hàng & AI |

> [!TIP]
> Hệ thống sẽ luôn đảm bảo các tài khoản test này tồn tại trong database (được khởi tạo/cập nhật tự động thông qua class [Application.java](file:///home/hahoang/Documents/LiveHealth/backend/src/main/java/com/livehealth/Application.java)) mỗi khi ứng dụng khởi chạy.

## Yêu cầu môi trường

- **Java 21**
- **Maven 3.9+**
- **Node.js 18+** & npm
- **Python 3.11+**
- **Docker** & Docker Compose (khuyến nghị dùng để khởi chạy nhanh DB và Cache)

## Cài đặt & Chạy nhanh (Quick Start)

1. Clone dự án về máy:
   ```bash
   git clone https://github.com/MyDuyen1111/LiveHealth.git
   cd LiveHealth
   ```

2. Khởi tạo file `.env` và các cài đặt phụ thuộc:
   ```bash
   bash scripts/setup.sh
   ```

3. Khởi chạy toàn bộ hệ thống bằng Docker Compose & Dev script:
   ```bash
   bash scripts/run_all.sh
   ```
   *Lệnh `run_all.sh` sẽ tự động khởi chạy database/cache bằng docker-compose, biên dịch các service, nạp 90 sản phẩm mẫu (seed data) nếu DB trống, và mở cổng cho các service.*

Mở trình duyệt truy cập: [http://localhost:62173](http://localhost:62173)

---

## Chạy thủ công từng thành phần

### 1. Database & Cache (MySQL, Redis)
```bash
cd backend
cp .env.example .env
docker-compose up -d mysql redis
```
*Đảm bảo các cấu hình trùng khớp trong file `.env` nếu bạn chạy MySQL/Redis trực tiếp trên máy không qua Docker.*

### 2. Backend (Quarkus)
```bash
cd backend
# Chạy ở chế độ Dev Mode hỗ trợ Hot-Reload (live coding)
mvn quarkus:dev
```
Backend sẽ khởi động tại: [http://localhost:62080](http://localhost:62080)
Tài liệu OpenAPI Swagger: [http://localhost:62080/swagger-ui](http://localhost:62080/swagger-ui)

### 3. AI Service (FastAPI)
```bash
cd ai-service
pip install -r requirements.txt
python main.py
```
AI Service chạy tại: [http://localhost:62000](http://localhost:62000)

### 4. Frontend (React + Vite)
```bash
cd frontend
npm install
npm run dev -- --port 62173 --strictPort
```
Frontend chạy tại: [http://localhost:62173](http://localhost:62173)

---

## Cấu hình môi trường (`.env` files)

### Cấu hình Backend (`backend/.env`)
Các tham số cấu hình hệ thống:
```env
SPRING_SERVER_PORT=62080
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:62307/livehealth?createDatabaseIfNotExists=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh
SPRING_DATASOURCE_USERNAME=root
MYSQL_ROOT_PASSWORD=root123
MYSQL_DATABASE=livehealth
JWT_SECRET=YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkwYWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODkw
JWT_ACCESS_EXPIRATION_TIME=3600000
JWT_REFRESH_EXPIRATION_TIME=259200000
ADMIN_PASSWORD=Admin@123
VNPAY_TMN_CODE=TESTTMN
VNPAY_HASH_SECRET=DUMMYHASHSECRETFORDEVELOPMENTONLY
VNPAY_RETURN_URL=http://localhost:62173/payment/vnpay/return
```

### Cấu hình AI Service (`ai-service/.env`)
Tham số kết nối LLMs & Database:
```env
OPENAI_API_BASE=http://13.54.246.217:20128/v1
OPENAI_API_KEY=<your_api_key>
OPENAI_MODEL=cx/gpt-4o
MYSQL_HOST=localhost
MYSQL_PORT=62307
MYSQL_USER=root
MYSQL_PASSWORD=root123
MYSQL_DATABASE=livehealth
```

---

## Seed dữ liệu mẫu (90 sản phẩm)

Nếu database trống, hệ thống sẽ tự nạp 90 sản phẩm khi khởi chạy lần đầu thông qua `scripts/run_all.sh`.
Bạn có thể tự khởi tạo lại dữ liệu mẫu bất kỳ lúc nào bằng lệnh:
```bash
python scripts/gen_100_products.py
```
*Script này kiểm tra mã SKU để bảo đảm không tạo trùng lặp các sản phẩm mẫu.*

---

## Kiểm tra nhanh & Biên dịch

- **Biên dịch thử Backend**:
  ```bash
  cd backend
  mvn compile
  ```
- **Build Frontend đóng gói sản phẩm**:
  ```bash
  cd frontend
  npm run build
  ```
- **Đường dẫn Swagger API**: [http://localhost:62080/swagger-ui](http://localhost:62080/swagger-ui)
- **Kiểm tra trạng thái AI**:
  ```bash
  curl http://localhost:62000/api/ai/health
  ```

## Ghi chú vận hành

- Frontend sử dụng proxy cấu hình trong `frontend/vite.config.js` để chuyển tiếp:
  - `/api` sang backend port `62080`
  - `/api/ai` sang AI service port `62000`
- Tính năng đăng ký thành viên tạo tài khoản trực tiếp (không gửi OTP ở môi trường dev).
- Cổng thanh toán VNPay đang chạy chế độ **Sandbox** thông qua cấu hình trong file cấu hình môi trường.

## Xử lý sự cố thường gặp (Troubleshooting)

| Lỗi | Nguyên nhân & Cách khắc phục |
| --- | --- |
| **Port đã được sử dụng (Address already in use)** | Kiểm tra tiến trình đang chiếm dụng port bằng `lsof -nP -iTCP:<port> -sTCP:LISTEN` và dùng `kill -9 <PID>` để giải phóng port. |
| **Backend lỗi kết nối MySQL** | Kiểm tra MySQL container hoặc database cục bộ có đang chạy ở port `62307` không. Đảm bảo tên DB `livehealth` và mật khẩu khớp với `.env`. |
| **AI service trả lỗi cấu hình** | Xác minh cấu hình `OPENAI_API_BASE` và `OPENAI_API_KEY` trong file `ai-service/.env` có hợp lệ không. |
| **Lỗi 'KeyError: ContainerConfig' khi Compose Rebuild** | Xảy ra do bug kiểm tra metadata của Docker-Compose phiên bản cũ. Khắc phục bằng cách chạy `sudo docker container prune -f && sudo docker-compose down && sudo docker-compose up -d`. |
