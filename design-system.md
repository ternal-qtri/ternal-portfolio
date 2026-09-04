# 📐 HỆ THỐNG THIẾT KẾ (DESIGN SYSTEM) - TERNAL PORTFOLIO

> **Phiên bản:** 2.0.0 (Cập nhật: Linear / Vercel Aesthetic & Interactive System Node Architecture)  
> **Chủ sở hữu:** Nguyễn Quốc Trí (Ternal) — Backend Developer  
> **Ngôn ngữ chuẩn:** Tiếng Việt  
> **Phong cách:** Minimalist Engineering (Linear / Vercel Aesthetic), Bento Grid phẳng đơn sắc (Flat/Solid), High Contrast, No Blurry Gradients  
> **Điểm nhấn chuyển động xuyên suốt:** Mô phỏng cụm Node kiến trúc hệ thống 3D / Wireframe (System Architecture Node HUD) bám theo toàn bộ hành trình cuộn trang từ đầu đến cuối  
> **Tech Stack:** HTML5 (Semantic Hardcoded) + Tailwind CSS (CDN Config) + Vanilla JS + GSAP (ScrollTrigger Scrub) + Anime.js  
> **Mục tiêu tương thích:** 100% UI tĩnh, dễ dàng chuyển đổi sang Spring Boot (Thymeleaf templates)

---

## 📑 MỤC LỤC
1. [Triết Lý Thiết Kế: Linear / Vercel Aesthetic](#1-triết-lý-thiết-kế-linear--vercel-aesthetic)
2. [Hệ Thống Màu Sắc Đơn Sắc (Solid Color Tokens)](#2-hệ-thống-màu-sắc-đơn-sắc-solid-color-tokens)
3. [Typography & Phông Chữ Sắc Nét](#3-typography--phông-chữ-sắc-nét)
4. [Bố Cục, Spacing & Bento Grid Phẳng](#4-bố-cục-spacing--bento-grid-phẳng)
5. [Quy Chuẩn Thành Phần Giao Diện (Solid Component Specs)](#5-quy-chuẩn-thành-phần-giao-diện-solid-component-specs)
6. [Hệ Thống Animation Xuyên Suốt: 3D Wireframe Server Node HUD](#6-hệ-thống-animation-xuyên-suốt-3d-wireframe-server-node-hud)
7. [Nguyên Tắc Code & Chuẩn Hóa Thymeleaf](#7-nguyên-tắc-code--chuẩn-hóa-thymeleaf)
8. [Hồ Sơ Cá Nhân & Bảng Ánh Xạ Kỹ Năng](#8-hồ-sơ-cá-nhân--bảng-ánh-xạ-kỹ-năng)
9. [Mẫu Khởi Tạo Mã Nguồn Chuẩn (Starter Boilerplate)](#9-mẫu-khởi-tạo-mã-nguồn-chuẩn-starter-boilerplate)

---

## 1. Triết Lý Thiết Kế: Linear / Vercel Aesthetic

- **Tuyệt đối không dùng dải màu loang (No Fuzzy Gradients/Orbs)**: Loại bỏ toàn bộ các hiệu ứng glow mờ ảo, gradient đa sắc nhiều màu. Chuyển sang phong cách đồ họa kỹ thuật cao (Engineering-Grade UI) với màu sắc phẳng, sắc nét, tương phản cao.
- **Bento Grid Phẳng & Viền Cơ Khí (Mechanical 1px Borders)**: Sử dụng các khối hộp màu đen/xám than (`#0A0A0A`, `#121212`, `#18181B`) với đường viền 1px siêu mảnh (`border-zinc-800`), khi hover chuyển sang màu sáng rõ nét (`border-zinc-500` hoặc `border-white`).
- **Màu Sắc Rõ Ràng (Solid High-Contrast)**:
  - Nền đen tuyền (`#000000` / `#09090B`).
  - Chữ trắng tinh khiết (`#FFFFFF`) và xám kỹ thuật (`#A1A1AA`).
  - Nút bấm đơn sắc tương phản cao (Solid White Button với chữ đen `bg-white text-black`, hoặc Solid Dark Button viền trắng).
  - Chỉ sử dụng 1 màu Accent duy nhất cho các trạng thái kích hoạt / trực tuyến (Emerald `#10B981` hoặc Electric Cyan `#06B6D4`).
- **Hiệu Ứng Chuyển Động Xuyên Suốt (3D Wireframe Node HUD)**:
  - Một mô hình kiến trúc phân tán (System Node Graph) dạng 3D Wireframe cố định ở góc/sườn màn hình, kết nối giữa các tầng: **Client Gateway ➔ Auth Service ➔ Core Backend ➔ Database Cluster**.
  - Khi người dùng cuộn chuột qua từng phần của trang, mô hình sẽ tự động xoay góc nhìn 3D (GSAP Scrub), thắp sáng Node tương ứng và truyền các gói dữ liệu (Data Packets) theo thời gian thực.

---

## 2. Hệ Thống Màu Sắc Đơn Sắc (Solid Color Tokens)

Tất cả màu sắc đều là màu phẳng (Solid), không pha trộn dải gradient, tạo cảm giác chính xác và hiện đại chuẩn các sản phẩm công nghệ hàng đầu thế giới (Vercel, Linear, Supabase, Cloudflare).

```
┌────────────────────────────────────────────────────────────────────────┐
│ SOLID HIGH-CONTRAST MONOCHROME PALETTE                                 │
├───────────────────┬────────────────────┬───────────────────────────────┤
│ BACKGROUND BASE   │ SURFACE / CARD     │ TEXT & CONTRAST ACCENT        │
│ #000000 / #09090B │ #121214 / #18181B  │ Pure White #FFFFFF            │
│ (Đen tuyền)       │ (Xám than phẳng)   │ Zinc 400   #A1A1AA            │
└───────────────────┴────────────────────┴───────────────────────────────┘
```

### 2.1. Bảng màu chi tiết

| Vai trò | Mã Hex | Tailwind Class đại diện | Quy tắc áp dụng |
| :--- | :--- | :--- | :--- |
| **Nền chính (Canvas Base)** | `#000000` / `#09090B` | `bg-black` / `bg-[#09090B]` | Toàn bộ nền trang (Body), đen sâu không đục |
| **Nền Card Bento (Surface)** | `#121214` | `bg-[#121214]` / `bg-zinc-900/90` | Mặt phẳng các khối hộp, card dự án, terminal |
| **Nền Card cấp 2 (Sub-Surface)**| `#18181B` (Zinc 900) | `bg-zinc-900` | Nền con bên trong card, ô input, sub-panels |
| **Đường viền mặc định (Border)**| `#27272A` (Zinc 800) | `border-zinc-800` | Viền 1px sắc nét ngăn cách các khối hộp |
| **Đường viền Hover (Border Focus)**| `#52525B` (Zinc 600) | `hover:border-zinc-500` / `hover:border-white/50` | Khi rê chuột vào card/button |
| **Chữ chính (Text Pure White)** | `#FFFFFF` | `text-white` | Tiêu đề H1–H3, tên dự án, số liệu quan trọng |
| **Chữ phụ (Text Technical Zinc)**| `#A1A1AA` (Zinc 400) | `text-zinc-400` | Đoạn văn mô tả, nhãn phụ, ngày tháng |
| **Chữ mờ (Text Muted)** | `#71717A` (Zinc 500) | `text-zinc-500` | Mã số metadata, index `#01`, code comments |
| **Màu nhấn trạng thái (Accent)** | `#10B981` (Emerald 500) | `text-emerald-400`, `bg-emerald-500` | Trạng thái Online, ping server, node kích hoạt |
| **Nút bấm chính (Solid Button)**| `#FFFFFF` nền, `#000000` chữ | `bg-white text-black hover:bg-zinc-200` | Nút hành động chính độ tương phản tối đa |

---

## 3. Typography & Phông Chữ Sắc Nét

1. **Font giao diện chính (Sans-serif):** `Plus Jakarta Sans` hoặc `Inter` (Font hình học, nét chữ sắc cạnh, sạch sẽ).
2. **Font kỹ thuật (Monospace):** `JetBrains Mono` (Dành cho mã số, tech tags, chỉ số phần trăm, log máy chủ).

### 3.1. Google Fonts CDN
```html
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;500;600;700&family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
```

### 3.2. Quy chuẩn kích thước (Type Scale)

- **Display Hero:** `text-4xl sm:text-6xl lg:text-7xl font-extrabold tracking-tight text-white`
- **Heading 1 (H1):** `text-3xl sm:text-4xl font-bold tracking-tight text-white`
- **Heading 2 (H2):** `text-xl sm:text-2xl font-semibold text-white`
- **Body Text:** `text-sm sm:text-base text-zinc-400 leading-relaxed`
- **Monospace Meta / Tag:** `font-mono text-xs font-medium tracking-wide text-zinc-300`

---

## 4. Bố Cục, Spacing & Bento Grid Phẳng

- **Container:** `max-w-6xl` (1152px) căn giữa (`mx-auto`).
- **Khoảng cách khối (Section Spacing):** `py-20 md:py-28`.
- **Lưới Bento Grid:** Chia 12 cột chuẩn, khoảng cách đều `gap-4 md:gap-6`.
- **Bo góc (Border Radius):** Bo góc vừa phải, sắc cạnh kiểu Linear (`rounded-xl` = 12px, `rounded-2xl` = 16px). Tránh bo tròn quá đà làm mất chất cơ khí.

---

## 5. Quy Chuẩn Thành Phần Giao Diện (Solid Component Specs)

### 5.1. Nút Bấm Đơn Sắc (Solid High-Contrast Buttons)

#### A. Nút Primary Solid (Trắng trên Đen)
```html
<a href="projects.html" class="inline-flex items-center justify-center gap-2 px-6 py-3.5 rounded-xl font-semibold text-sm bg-white text-black hover:bg-zinc-200 transition-all duration-200 active:scale-95 shadow-sm">
  <span>Xem dự án</span>
  <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3"/>
  </svg>
</a>
```

#### B. Nút Secondary Solid (Đen viền Xám)
```html
<a href="contact.html" class="inline-flex items-center justify-center gap-2 px-6 py-3.5 rounded-xl font-medium text-sm text-zinc-200 bg-[#121214] hover:bg-zinc-800 border border-zinc-800 hover:border-zinc-500 transition-all duration-200 active:scale-95">
  <span>Liên hệ</span>
</a>
```

---

### 5.2. Bento Card Đơn Sắc (Solid Bento Card)

Card sử dụng màu nền than `#121214`, viền cơ khí `border-zinc-800`, hover chuyển sang `border-zinc-500` rõ ràng:

```html
<div class="bento-card bg-[#121214] border border-zinc-800 hover:border-zinc-500 rounded-2xl p-6 sm:p-8 transition-colors duration-300 flex flex-col justify-between">
  <div class="flex items-center justify-between mb-4">
    <span class="font-mono text-xs text-zinc-400 bg-zinc-900 px-3 py-1 rounded-lg border border-zinc-800">// BACKEND CORE</span>
    <span class="font-mono text-xs text-zinc-500">01</span>
  </div>
  <div>
    <h3 class="text-xl font-bold text-white mb-2">Kiến Trúc Microservices</h3>
    <p class="text-sm text-zinc-400 leading-relaxed">Phát triển hệ thống phân tán với Spring Boot, bảo mật JWT và cơ chế cache Redis.</p>
  </div>
</div>
```

---

### 5.3. Tech Badges Đơn Sắc (Solid Tech Chips)

```html
<div class="inline-flex items-center gap-2 px-3 py-1.5 rounded-lg bg-zinc-900 border border-zinc-800 hover:border-zinc-600 transition-colors">
  <img src="https://files.svgcdn.io/devicon/java.svg" alt="Java" class="w-4 h-4">
  <span class="font-mono text-xs text-zinc-300 font-medium">Java</span>
</div>
```

---

## 6. Hệ Thống Animation Xuyên Suốt: 3D Wireframe Server Node HUD

Đây là điểm nhấn độc đáo nhất của website: **Một bảng điều khiển kiến trúc hệ thống dạng 3D Node (System Architecture HUD)** được ghim cố định ở góc phải màn hình (trên Desktop) hoặc thanh trạng thái (trên Mobile), kết nối trực tiếp với thanh cuộn:

```
┌────────────────────────────────────────────────────────────────────────┐
│ 3D WIREFRAME SYSTEM NODE ARCHITECTURE (HUD)                            │
├────────────────────────────────────────────────────────────────────────┤
│                                                                        │
│       [ NODE 1: GATEWAY ]      ← Thắp sáng khi ở HERO SECTION         │
│               │ (Data Packet)                                          │
│       [ NODE 2: CORE LOGIC ]   ← Thắp sáng khi ở ABOUT ME SECTION      │
│               │                                                        │
│       [ NODE 3: DATABASE ]     ← Thắp sáng khi ở PROJECTS SECTION      │
│               │                                                        │
│       [ NODE 4: EVENT STREAM ] ← Thắp sáng khi ở CONTACT / FOOTER      │
│                                                                        │
│ * Xoay 3D theo góc nhìn khi cuộn chuột (GSAP ScrollTrigger Scrub)      │
│ * Hạt xung năng lượng truyền giữa các Node khi lướt tới từng section   │
└────────────────────────────────────────────────────────────────────────┘
```

### 6.1. Nguyên lý vận hành bằng GSAP ScrollTrigger
- **Toàn bộ trang được chia thành 4 mốc kiến trúc (Nodes)**:
  1. `Section #hero` ➔ Node: **API Gateway & Routing**
  2. `Section #about` ➔ Node: **Core Backend (Spring Boot Engine)**
  3. `Section #projects` ➔ Node: **Database & Microservices**
  4. `Section #contact / footer` ➔ Node: **Event Stream & Notification**
- **Cơ chế Scrub**: Khi cuộn trang, thanh tiến trình tỷ lệ thuận và camera góc nhìn 3D của cụm Node sẽ xoay một góc tinh tế, sáng đèn Node đang active, và phát xung nhịp dữ liệu (pulse data packets) xuống các node phía dưới.

---

## 7. Nguyên Tắc Code & Chuẩn Hóa Thymeleaf

1. **100% Hardcoded Semantic HTML**: Tuyệt đối không dùng JavaScript để render HTML dữ liệu (`innerHTML`, `forEach`). Toàn bộ text được viết thẳng vào HTML.
2. **Dễ dàng chuyển đổi sang Thymeleaf (`th:*`)**:
   - `th:text="${profile.name}"`
   - `th:each="project : ${projects}"`
   - `th:src="${skill.logoUrl}"`
   - `th:fragment="navbar"` và `th:fragment="footer"`

---

## 8. Hồ Sơ Cá Nhân & Bảng Ánh Xạ Kỹ Năng

- **Họ và tên:** Nguyễn Quốc Trí (Ternal)
- **Vị trí:** Backend Developer (Java / Spring Boot)
- **Học vấn:** Sinh viên chuyên ngành Phát triển phần mềm, trường Cao đẳng FPT Polytechnic.
- **Định hướng:** Chuyên sâu về Backend, hệ sinh thái Java, Spring Boot, thiết kế kiến trúc hệ thống đồ sộ, tối ưu hóa cơ sở dữ liệu và bảo mật.
- **Liên hệ:** `ternal.qtri@gmail.com` | `github.com/ternal-qtri` | `0973 346 041`.

### Bảng Logo Devicon SVG chuẩn:
- Java: `https://files.svgcdn.io/devicon/java.svg`
- Spring: `https://files.svgcdn.io/devicon/spring.svg`
- PostgreSQL: `https://files.svgcdn.io/devicon/postgresql.svg`
- MySQL: `https://files.svgcdn.io/devicon/mysql.svg`
- SQL Server: `https://files.svgcdn.io/devicon/microsoftsqlserver.svg`
- MongoDB: `https://files.svgcdn.io/devicon/mongodb.svg`
- Docker: `https://files.svgcdn.io/devicon/docker.svg`
- Git: `https://files.svgcdn.io/devicon/git.svg`
- Postman: `https://files.svgcdn.io/devicon/postman.svg`
- Swagger: `https://files.svgcdn.io/devicon/swagger.svg`
- DBeaver: `https://files.svgcdn.io/devicon/dbeaver.svg`
- Thymeleaf: `https://files.svgcdn.io/devicon/thymeleaf.svg`

---

## 9. Mẫu Khởi Tạo Mã Nguồn Chuẩn (Starter Boilerplate)

```html
<!DOCTYPE html>
<html lang="vi" class="scroll-smooth">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Nguyễn Quốc Trí (Ternal) | Backend Developer</title>

  <!-- Google Fonts: Plus Jakarta Sans & JetBrains Mono -->
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;500;600;700&family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">

  <!-- Tailwind CSS CDN (Solid Theme Config) -->
  <script src="https://cdn.tailwindcss.com"></script>
  <script>
    tailwind.config = {
      darkMode: 'class',
      theme: {
        extend: {
          fontFamily: {
            sans: ['"Plus Jakarta Sans"', 'sans-serif'],
            mono: ['"JetBrains Mono"', 'monospace'],
          },
          colors: {
            brand: {
              black: '#000000',
              canvas: '#09090B',
              surface: '#121214',
              sub: '#18181B',
              border: '#27272A',
              borderHover: '#52525B',
              accent: '#10B981',
            }
          }
        }
      }
    }
  </script>

  <!-- GSAP & Anime.js CDN -->
  <script src="https://cdnjs.cloudflare.com/ajax/libs/gsap/3.12.5/gsap.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/gsap/3.12.5/ScrollTrigger.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/animejs/3.2.2/anime.min.js"></script>
</head>
<body class="bg-[#09090B] text-zinc-100 font-sans antialiased selection:bg-white selection:text-black">
  <!-- Giao diện HTML tĩnh -->
  <script src="../assets/js/main.js"></script>
</body>
</html>
```
