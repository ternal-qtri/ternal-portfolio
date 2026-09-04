SET timezone TO 'Asia/Ho_Chi_Minh';

create database ternal_portfolio

-- Bảng quản trị viên (Admin)
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'ROLE_ADMIN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng Nhóm Kỹ năng
CREATE TABLE skill_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    order_index INT DEFAULT 0
);

-- Bảng Kỹ năng
CREATE TABLE skills (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    icon_url VARCHAR(500), -- Chứa đường dẫn SVG hoặc class icon
    proficiency VARCHAR(100), -- Ví dụ: "Chuyên sâu / Core", "Thành thạo"
    category_id BIGINT REFERENCES skill_categories(id) ON DELETE CASCADE,
    order_index INT DEFAULT 0
);

-- Bảng Dự án
CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    short_description VARCHAR(500),
    type VARCHAR(50), -- PERSONAL, TEAM
    role VARCHAR(100), -- Ví dụ: "Lead Backend Developer"
    timeframe VARCHAR(50), -- Ví dụ: "01/2026 – 04/2026"
    cover_image VARCHAR(500),
    video_url VARCHAR(500),
    github_url VARCHAR(500),
    tags VARCHAR(255), -- Các công nghệ chính: "Spring Boot, PostgreSQL, Docker"
    detailed_description TEXT,
    features TEXT,
    lessons_learned TEXT,
    challenges TEXT,
    status VARCHAR(50), -- ACTIVE, COMPLETED
    order_index INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng Tin nhắn Liên hệ
CREATE TABLE contact_messages (
    id BIGSERIAL PRIMARY KEY,
    sender_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    message TEXT NOT NULL,
    status VARCHAR(50) DEFAULT 'UNREAD', -- UNREAD, READ, REPLIED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

SHOW timezone;