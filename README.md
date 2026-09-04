# ⚡ Ternal Portfolio — Backend Developer Platform

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring_Boot-4.0.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/PostgreSQL-15+-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/HTMX-2.0-3366CC?style=for-the-badge&logo=htmx&logoColor=white" alt="HTMX" />
  <img src="https://img.shields.io/badge/Tailwind_CSS-3.x-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white" alt="Tailwind CSS" />
  <img src="https://img.shields.io/badge/Docker-Enabled-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
</p>

---

## 📌 Giới thiệu tổng quan (Overview)

**Ternal Portfolio** là nền tảng website cá nhân hiện đại dành riêng cho kỹ sư Backend, phát triển bởi **Nguyễn Quốc Trí (Ternal)**. Dự án được thiết kế theo triết lý **Minimalist Engineering (Linear / Vercel Aesthetic)**: tập trung vào độ tương phản cao, giao diện phẳng đơn sắc (Flat/Solid Bento Grid), viền cơ khí 1px, loại bỏ hoàn toàn các dải màu loang mờ (fuzzy gradients).

Dự án kết hợp sức mạnh của hệ sinh thái **Java 21 / Spring Boot**, cơ sở dữ liệu **PostgreSQL**, trải nghiệm SPA mượt mà qua **HTMX 2.0**, cùng quy trình mã hóa dữ liệu đầu cuối **Web Crypto API (RSA-2048 + AES-256-GCM)** bảo vệ tuyệt đối thông tin liên hệ của khách hàng.

🔗 **Website trực tuyến:** [ternal-nguyenquoctri.io.vn](https://ternal-nguyenquoctri.io.vn)

---

## ✨ Điểm nhấn kiến trúc & Tính năng nổi bật

### 1. 🌐 Trải nghiệm Single Page Application (SPA) với HTMX
* Điều hướng trang tức thì không tải lại toàn bộ trang (`hx-boost="true"`).
* Thanh tiến trình tải trang (Glow Loading Bar) thời gian thực.
* Tự động đồng bộ URL lịch sử (`pushState`), quản lý title động và re-init hiệu ứng animation trơn tru qua sự kiện `htmx:afterSettle`.

### 2. 🧊 3D Wireframe System Architecture HUD (Interactive Animation)
* Mô hình kiến trúc phân tán dạng khối lập phương 3D Wireframe cố định theo dõi toàn bộ hành trình cuộn trang qua **GSAP ScrollTrigger Scrub**.
* Tự động thắp sáng các Node kiến trúc tương ứng và phát xung dữ liệu:
  * `Node 1: API Gateway & Routing` (Hero Section)
  * `Node 2: Core Backend Engine` (About Section)
  * `Node 3: Database & Cache Cluster` (Projects Section)
  * `Node 4: Event Stream & Notification` (Contact Section)

### 3. 🛡️ Cơ chế Bảo mật & Mã hóa dữ liệu đầu cuối (End-to-End Cryptography)
* **Client-Side Encryption**: Trình duyệt tạo khóa phiên `AES-256-GCM` ngẫu nhiên để mã hóa thông tin liên hệ, sau đó dùng khóa công khai `RSA-2048 (RSA-OAEP with SHA-256)` từ server để bọc khóa AES.
* **Server-Side Decryption**: [`CryptoService`](file:///C:/Workspace/Learn-space/o3-back-end/a1-SpringBoot/Ternal-Portfolio/Ternal-Portfolio/src/main/java/com/ternal/ternalportfolio/service/CryptoService.java) giải mã an toàn khóa phiên và nội dung tin nhắn.
* **Chống Replay Attack**: Kiểm tra sai lệch Timestamp giữa client và server (giới hạn hiệu lực 15 phút).
* **Bẫy Spambot Honeypot**: Trường ẩn phát hiện bot tự động submit form.
* **In-Memory Sliding Window Rate Limiter**: [`RateLimiterService`](file:///C:/Workspace/Learn-space/o3-back-end/a1-SpringBoot/Ternal-Portfolio/Ternal-Portfolio/src/main/java/com/ternal/ternalportfolio/service/RateLimiterService.java) giới hạn 3 yêu cầu/60 giây trên mỗi địa chỉ IP (HTTP 429 Too Many Requests).
* **Trang lỗi chuyên nghiệp**: Tùy biến mã lỗi HTTP `401`, `403`, `404`, `500` chuẩn Bento Grid.

### 4. 📬 Hệ thống Email bất đồng bộ (Dual Async Mail Engine)
* Xử lý gửi email nền không chặn luồng chính (`@Async` với custom Thread Pool).
* **Gửi đa luồng**: 1 email xác nhận cho khách hàng + 1 email cảnh báo tin nhắn mới cho quản trị viên.
* **Chuẩn hóa chống Spam RFC 3834**: Hỗ trợ định dạng `multipart/alternative` (Plain Text + Bento HTML), thêm header `Auto-Submitted: auto-replied` giúp email không bị lọc vào hộp thư rác (Spam).
* Thông báo Toast Notyf kép: Thông báo thành công + Nhắc nhở kiểm tra hòm thư rác.

### 5. 🛠️ Cổng Quản trị Admin chuyên sâu
* Xác thực bảo mật với **Spring Security** & **Google OAuth2 Login**.
* Quản lý dự án (CRUD), đồng bộ tải ảnh bìa lên **Cloudinary** (tự động xóa ảnh trên Cloud khi xóa dự án).
* Hộp thư quản lý liên hệ: xem chi tiết, đánh dấu đã đọc, trả lời nhanh qua mailto.
* Quản lý danh mục kỹ năng và kỹ năng công nghệ.

---

## 🏗️ Công nghệ sử dụng (Tech Stack)

| Tầng kiến trúc | Công nghệ |
| :--- | :--- |
| **Backend Core** | Java 21, Spring Boot 4.x / 3.x, Spring MVC, Spring Data JPA |
| **Database** | PostgreSQL 15+, Hibernate 7.x, HikariCP Connection Pool |
| **Security & Auth** | Spring Security 6.x, Google OAuth2 Client, Web Crypto API, RSA-OAEP, AES-GCM |
| **Frontend Engine** | Thymeleaf, Thymeleaf Layout Dialect, HTMX 2.0 |
| **Styling & UI** | Tailwind CSS (CDN Configured), Font Awesome 6, Notyf Toast |
| **Motion & FX** | GSAP 3.12 (ScrollTrigger Plugin), Anime.js |
| **Media Cloud** | Cloudinary Java SDK 1.38 (Auto upload & destroy assets) |
| **DevOps & Build** | Maven 3.9+, Docker (Multi-Stage Build Alpine JRE), JVM Container Tuning |

---

## 📁 Cấu trúc thư mục (Project Structure)

```plaintext
Ternal-Portfolio/
├── src/
│   ├── main/
│   │   ├── java/com/ternal/ternalportfolio/
│   │   │   ├── config/              # Security, Async, Cloudinary, Web MVC configs
│   │   │   ├── controller/          # Client & Admin Web Controllers
│   │   │   ├── entity/              # JPA Entities (Project, ContactMessage, Skill...)
│   │   │   ├── repository/          # Spring Data JPA Repositories
│   │   │   └── service/             # Business Logic, Crypto, RateLimit, Email, Cloudinary
│   │   └── resources/
│   │       ├── static/assets/       # CSS, JavaScript (main.js, showAlert.js), Images
│   │       ├── templates/
│   │       │   ├── client/          # Client Pages (home, projects, contact, detail)
│   │       │   │   ├── email/       # Bento-styled Email Templates
│   │       │   │   └── layouts/     # SPA Layout, Navbar, Footer
│   │       │   ├── admin/           # Admin Management Pages & Layouts
│   │       │   └── error/           # Custom HTTP Error Pages (401, 403, 404, 500)
│   │       └── application.properties
│   └── test/java/com/ternal/        # JUnit 5 & Spring Boot Tests (Security, Crypto, Email)
├── Dockerfile                       # Multi-stage Docker build (Alpine JRE)
├── .dockerignore                    # Tối ưu hóa Docker build context
├── design-system.md                 # Quy chuẩn thiết kế Linear/Vercel Aesthetic
└── pom.xml                          # Maven Dependencies & Build Configuration
```

---

## 🚀 Hướng dẫn cài đặt & Khởi chạy (Getting Started)

### 1. Yêu cầu môi trường (Prerequisites)
* **JDK 21** trở lên.
* **Maven 3.9+** (hoặc sử dụng `mvnw`).
* **PostgreSQL 15+** đang chạy.
* **Docker & Docker Compose** (Tùy chọn nếu muốn chạy container).

### 2. Cấu hình biến môi trường
Tạo file `.env` tại thư mục gốc của dự án:

```properties
# Database PostgreSQL
DB_PORT=5432
DB_NAME=ternal_portfolio
DB_USERNAME=postgres
DB_PASSWORD=your_postgres_password

# Google OAuth2 (Dành cho đăng nhập Admin)
GOOGLE_CLIENT_ID=your_google_client_id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your_google_client_secret

# Gmail SMTP Service
EMAIL_USERNAME=your_gmail@gmail.com
EMAIL_PASSWORD=your_google_app_password

# Cloudinary Media Storage
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

# Admin Notification Email
admin.notification.email=your_gmail@gmail.com
```

### 3. Chạy ứng dụng nội bộ (Local Development)

```bash
# Cài đặt dependencies và chạy ứng dụng
mvn clean spring-boot:run
```
Ứng dụng sẽ khởi chạy tại: `http://localhost:8080`

### 4. Chạy kiểm thử tự động (Unit & Integration Tests)

```bash
mvn test -DargLine="-Duser.timezone=UTC"
```

---

## 🐳 Triển khai với Docker (Containerization)

Dự án đã được đóng gói sẵn file `Dockerfile` chuẩn **Multi-stage build**:
* **Giai đoạn 1**: Build ứng dụng bằng Maven và cache dependencies.
* **Giai đoạn 2**: Sử dụng **Alpine Linux JRE 21** siêu nhẹ (~180MB), user không đặc quyền (`spring`), tích hợp Healthcheck và cgroups container-aware JVM flags.

```bash
# 1. Build Docker Image
docker build -t ternal-portfolio:latest .

# 2. Khởi chạy Container
docker run -d \
  --name ternal-portfolio-app \
  -p 8080:8080 \
  --env-file .env \
  --restart unless-stopped \
  ternal-portfolio:latest

# 3. Xem log hoạt động
docker logs -f ternal-portfolio-app
```

---

## 👨‍💻 Tác giả (Author)

* **Họ và tên:** Nguyễn Quốc Trí (Ternal)
* **Vai trò:** Backend Developer (Java / Spring Boot Ecosystem)
* **Email:** [ternal.qtri@gmail.com](mailto:ternal.qtri@gmail.com)
* **Hotline / Zalo:** 0973 346 041
* **GitHub:** [@ternal-qtri](https://github.com/ternal-qtri)
* **Khu vực:** Quận 12, Thành phố Hồ Chí Minh, Việt Nam
