# LiveHealth

LiveHealth là website thương mại điện tử thực phẩm sạch kết hợp trợ lý AI sức khỏe. Hệ thống hỗ trợ khách hàng xem sản phẩm, tìm kiếm/lọc/sắp xếp, quản lý giỏ hàng, đặt hàng, xem và hủy đơn hàng; đồng thời cung cấp trang quản trị để quản lý sản phẩm, danh mục, đơn hàng, bài viết và dữ liệu hiển thị trên website.

## Chức năng chính

- Đăng ký, đăng nhập, đăng xuất bằng JWT.
- Xem danh sách sản phẩm, chi tiết sản phẩm, tìm kiếm, lọc danh mục, sắp xếp giá.
- Quản lý giỏ hàng: thêm sản phẩm, cập nhật số lượng, xóa sản phẩm.
- Đặt hàng, chọn phương thức giao hàng/thanh toán, xem lịch sử và chi tiết đơn hàng.
- Hủy đơn hàng khi đơn còn ở trạng thái được phép hủy.
- Thanh toán VNPay sandbox.
- AI Sức khỏe: trò chuyện tư vấn dinh dưỡng, BMI/BMR/TDEE, gợi ý thực đơn và sản phẩm phù hợp.
- Blog/tin tức và trang giới thiệu.
- Admin quản lý sản phẩm, danh mục, thương hiệu, tag, đơn hàng, phương thức giao hàng/thanh toán, tin tức, thành viên, nhận xét.

## Công nghệ sử dụng

| Thành phần | Công nghệ |
| --- | --- |
| Frontend | React 19, Vite 8, React Router, Lucide Icons |
| Backend | Spring Boot 3.5, Java 21, Maven, Spring Security, JPA/Hibernate |
| AI Service | Python, FastAPI, OpenAI-compatible API |
| Database | MySQL 8 |
| Cache | Redis |
| Tài liệu API | Swagger/OpenAPI |

## Cấu trúc thư mục

```text
LiveHealth/
├── frontend/                 # React SPA
│   ├── src/pages/            # Home, Shop, Checkout, HealthAI, Admin...
│   ├── src/components/       # Header, Footer, AccountSidebar...
│   ├── src/context/          # AuthContext, CartContext
│   └── src/api/              # API client
├── backend/                  # Spring Boot API
│   ├── src/main/java/com/livehealth/
│   │   ├── auth/
│   │   ├── cart/
│   │   ├── order/
│   │   ├── product/
│   │   ├── news/
│   │   ├── payment/
│   │   ├── user/
│   │   └── shared/
│   ├── SQL.sql
│   └── docker-compose.yml
├── ai-service/               # FastAPI AI service
└── scripts/                  # Script setup, chạy hệ thống, seed dữ liệu
```

## URL chạy local

| Service | URL |
| --- | --- |
| Frontend | http://localhost:62173 |
| Backend API | http://localhost:62080 |
| Swagger UI | http://localhost:62080/swagger-ui/index.html |
| AI Service | http://localhost:62000 |
| AI Docs | http://localhost:62000/docs |
| MySQL | localhost:62307 |
| Redis | localhost:62380 |

## Tài khoản test

| Vai trò | Email | Mật khẩu | Ghi chú |
| --- | --- | --- | --- |
| Admin | `admin@livehealth.com` | `Admin@123` | Được tự động tạo/cập nhật khi backend khởi động. |
| Khách hàng demo | `user@livehealth.vn` | `User123@` | Được tự động tạo/cập nhật khi backend khởi động. |

Nếu database đã có dữ liệu cũ, backend vẫn sẽ tự đảm bảo hai tài khoản test trên tồn tại và đăng nhập được.

## Yêu cầu môi trường

- Java 21
- Maven 3.9+
- Node.js 18+ và npm
- Python 3.11+
- Docker/Docker Compose nếu muốn chạy MySQL và Redis bằng container
- MySQL và Redis đang chạy đúng port nếu không dùng Docker

## Cài đặt nhanh

```bash
git clone https://github.com/MyDuyen1111/LiveHealth.git
cd LiveHealth

# Tạo file .env và cài dependency cần thiết
bash scripts/setup.sh

# Chạy toàn bộ hệ thống
bash scripts/run_all.sh
```

Lệnh `scripts/run_all.sh` sẽ tự khởi động MySQL/Redis/backend/AI/frontend và tự nạp bộ 90 sản phẩm mẫu nếu database chưa có đủ dữ liệu theo SKU.

Sau khi hệ thống chạy xong, mở:

```text
http://localhost:62173
```

## Chạy thủ công từng service

### 1. MySQL và Redis

```bash
cd backend
cp .env.example .env
docker compose up -d mysql redis
```

Nếu máy không dùng Docker, cần đảm bảo:

- MySQL chạy tại `localhost:62307`
- Redis chạy tại `localhost:62380`
- Database tên `livehealth`
- Tài khoản MySQL khớp với `backend/.env`

### 2. Backend

```bash
cd backend
mvn spring-boot:run
```

Backend chạy tại:

```text
http://localhost:62080
```

Swagger:

```text
http://localhost:62080/swagger-ui/index.html
```

### 3. AI Service

```bash
cd ai-service
pip install -r requirements.txt
python main.py
```

AI service chạy tại:

```text
http://localhost:62000
```

Kiểm tra health:

```bash
curl http://localhost:62000/api/ai/health
```

### 4. Frontend

```bash
cd frontend
npm install
npm run dev -- --port 62173 --strictPort
```

Frontend chạy tại:

```text
http://localhost:62173
```

## Cấu hình môi trường

### Backend

File cấu hình:

```text
backend/.env
```

Các biến quan trọng:

```env
SPRING_SERVER_PORT=62080
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:62307/livehealth?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh
SPRING_DATASOURCE_USERNAME=root
MYSQL_ROOT_PASSWORD=root123
MYSQL_DATABASE=livehealth
JWT_ACCESS_EXPIRATION_TIME=3600000
JWT_REFRESH_EXPIRATION_TIME=259200000
ADMIN_PASSWORD=Admin@123
```

### AI Service

File cấu hình:

```text
ai-service/.env
```

Các biến quan trọng:

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

Không commit file `.env` thật lên Git. Chỉ commit `.env.example`.

## Seed dữ liệu mẫu

Khi chạy bằng `scripts/run_all.sh`, hệ thống tự gọi script nạp bộ 90 sản phẩm mẫu nên máy mới clone project vẫn có sản phẩm hiển thị trên màn cửa hàng.

Nếu cần chạy thủ công lại bộ 90 sản phẩm, chạy backend trước rồi dùng:

```bash
python scripts/gen_100_products.py
```

Script này có kiểm tra SKU để không tạo trùng các sản phẩm mẫu đã tồn tại.

Các script seed cũ vẫn được giữ lại để tham khảo dữ liệu nền:

```bash
python backend/scripts/seed_data.py
bash scripts/seed_data.sh
```

Không nên chạy các script seed cũ nếu mục tiêu là giữ đúng bộ 90 sản phẩm hiện tại.

## Kiểm tra nhanh

```bash
curl http://localhost:62080/swagger-ui/index.html
curl http://localhost:62000/api/ai/health
curl "http://localhost:62080/api/v1/products?pageNum=1&pageSize=5"
```

Build frontend:

```bash
cd frontend
npm run build
```

Compile backend:

```bash
cd backend
mvn -q -DskipTests compile
```

## Ghi chú vận hành

- Frontend proxy `/api` sang backend `62080`.
- Frontend proxy `/api/ai` sang AI service `62000`.
- `scripts/run_all.sh` tự nạp/cập nhật bộ 90 sản phẩm mẫu sau khi backend khởi động.
- Backend tự tạo/cập nhật tài khoản test, phương thức giao hàng và phương thức thanh toán khi khởi động.
- Chức năng đăng ký hiện không dùng OTP; người dùng nhập thông tin hợp lệ là tạo tài khoản.
- Chức năng quên mật khẩu vẫn dùng OTP email, cần cấu hình SMTP nếu muốn gửi email thật.
- VNPay đang dùng sandbox config trong `backend/.env`.

## Troubleshooting

| Lỗi | Cách xử lý |
| --- | --- |
| Port đã được sử dụng | Kiểm tra bằng `lsof -nP -iTCP:<port> -sTCP:LISTEN` rồi tắt tiến trình cũ. |
| Backend không kết nối MySQL | Kiểm tra MySQL đang chạy ở `62307`, database `livehealth`, mật khẩu trong `backend/.env`. |
| AI trả lỗi cấu hình | Kiểm tra `ai-service/.env`, đặc biệt `OPENAI_API_BASE`, `OPENAI_API_KEY`, `OPENAI_MODEL`. |
| Frontend gọi API lỗi | Kiểm tra backend chạy ở `62080` và Vite proxy trong `frontend/vite.config.js`. |
| Không đăng nhập được admin | Kiểm tra `ADMIN_PASSWORD` trong `backend/.env`; mặc định là `Admin@123`. |
