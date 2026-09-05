# ⚡ Ternal Portfolio — Website Cá Nhân

Website portfolio cá nhân của **Nguyễn Quốc Trí (Ternal)**, giới thiệu hồ sơ năng lực, các dự án thực tế, kỹ năng chuyên môn và kết nối liên hệ trực tiếp.

🔗 **Website trực tuyến:** [ternal-nguyenquoctri.io.vn](https://ternal-nguyenquoctri.io.vn)

---

## 🌟 Tính năng chính

### 1. Dành cho khách truy cập (Client)
* **Trang chủ & Giới thiệu:** Tổng quan về kinh nghiệm, định hướng nghề nghiệp và các dự án tiêu biểu.
* **Danh mục Dự án:** Danh sách dự án kèm bộ lọc theo loại (Cá nhân / Dự án Nhóm).
* **Chi tiết Dự án:** 
  * Hiển thị mô tả chi tiết, chức năng chính, công nghệ sử dụng, bài học kinh nghiệm và giải pháp.
  * Tích hợp xem trực tiếp video demo (YouTube Embed). Nếu dự án chưa có video hoặc nhập `0`, thẻ video sẽ tự động ẩn để giao diện luôn gọn gàng.
* **Kỹ năng chuyên môn:** Trình bày danh mục kỹ năng theo nhóm kèm mức độ thành thạo trực quan.
* **Liên hệ trực tiếp:** Form gửi tin nhắn nhanh, tự động gửi email xác nhận cho người gửi và email thông báo đến chủ website.
* **Trải nghiệm mượt mà:** Điều hướng trang nhanh không cần tải lại toàn bộ trang, giao diện tương thích tốt trên máy tính và điện thoại.

### 2. Dành cho quản trị viên (Admin)
* **Đăng nhập quản trị:** Đăng nhập an toàn và thuận tiện qua tài khoản Google.
* **Quản lý Dự án:** Thêm mới, chỉnh sửa thông tin, tải ảnh bìa, cập nhật link video demo hoặc mã nguồn GitHub, sắp xếp thứ tự và xóa dự án.
* **Quản lý Kỹ năng:** Thêm, sửa, sắp xếp các nhóm kỹ năng và danh sách kỹ năng chuyên môn.
* **Quản lý Hộp thư liên hệ:** Xem tin nhắn từ khách truy cập, lọc trạng thái, đánh dấu đã đọc hoặc đã phản hồi.

---

## 🛠️ Công nghệ sử dụng

* **Backend:** Java 21, Spring Boot, Spring Data JPA
* **Cơ sở dữ liệu:** PostgreSQL, Hibernate
* **Giao diện & Tương tác:** Thymeleaf, HTMX, Tailwind CSS
* **Lưu trữ hình ảnh:** Cloudinary
* **Dịch vụ gửi email:** Resend API

---

## 🚀 Hướng dẫn cài đặt & Khởi chạy

### 1. Yêu cầu môi trường
* **Java 21** trở lên
* **Maven 3.8+**
* Cơ sở dữ liệu **PostgreSQL**

### 2. Cấu hình biến môi trường
Tạo file `.env` tại thư mục gốc của dự án với các thông số cơ bản:

```properties
# Cơ sở dữ liệu PostgreSQL
DB_PORT=5432
DB_NAME=ternal_portfolio
DB_USERNAME=postgres
DB_PASSWORD=your_postgres_password

# Google OAuth2 (Đăng nhập Admin)
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret

# Cloudinary (Lưu trữ ảnh bìa dự án)
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

# Dịch vụ gửi email (Resend API)
RESEND_API_KEY=your_resend_api_key
EMAIL_USERNAME=ternal.qtri@gmail.com
```

### 3. Khởi chạy ứng dụng
Mở terminal tại thư mục dự án và chạy lệnh:

```bash
mvn spring-boot:run
```

Sau khi ứng dụng khởi động thành công, truy cập website tại: `http://localhost:8080`

---

## 👨‍💻 Tác giả

* **Họ và tên:** Nguyễn Quốc Trí (Ternal)
* **Vai trò:** Backend Developer
* **Email:** [ternal.qtri@gmail.com](mailto:ternal.qtri@gmail.com)
* **GitHub:** [@ternal-qtri](https://github.com/ternal-qtri)
* **Khu vực:** Thành phố Hồ Chí Minh, Việt Nam
