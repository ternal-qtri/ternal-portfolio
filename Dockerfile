# ==============================================================================
# Multi-stage Dockerfile for Spring Boot Application (Ternal Portfolio)
# Stage 1: Build & Package with Maven
# Stage 2: Minimal, Secure, Container-Optimized Alpine JRE Runtime
# ==============================================================================

# ------------------------------------------------------------------------------
# STAGE 1: Builder
# ------------------------------------------------------------------------------
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Tận dụng triệt để cơ chế Layer Caching của Docker:
# Chỉ copy pom.xml trước để tải và cache toàn bộ dependencies
COPY pom.xml .

# Tải trước dependencies (nếu pom.xml không đổi thì bước này được cache 100%)
RUN mvn dependency:go-offline -B

# Copy toàn bộ mã nguồn vào image
COPY src ./src

# Đóng gói file JAR thực thi (bỏ qua chạy test trong lúc build image)
RUN mvn clean package -DskipTests -B

# ------------------------------------------------------------------------------
# STAGE 2: Lightweight & Secure Runtime
# ------------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine AS runtime

# Cài đặt tzdata để đồng bộ múi giờ Việt Nam và wget phục vụ Healthcheck
RUN apk add --no-cache tzdata wget && \
    cp /usr/share/zoneinfo/Asia/Ho_Chi_Minh /etc/localtime && \
    echo "Asia/Ho_Chi_Minh" > /etc/timezone

WORKDIR /app

# Tuân thủ tiêu chuẩn bảo mật container (CIS Benchmark):
# Tạo user và group riêng biệt 'spring', không chạy ứng dụng dưới quyền root
RUN addgroup -S spring && adduser -S spring -G spring

# Copy file JAR được đóng gói từ Stage 1 và phân quyền cho user spring
COPY --from=builder --chown=spring:spring /build/target/*.jar app.jar

# Chuyển sang quyền user không đặc quyền (non-root)
USER spring:spring

# Khai báo cổng dịch vụ của ứng dụng
EXPOSE 8080

# Cấu hình tối ưu hóa JVM trong môi trường container:
# - UseContainerSupport: Nhận diện chính xác giới hạn CPU & RAM từ Docker cgroups
# - MaxRAMPercentage=75.0: Giới hạn heap size tối đa 75% RAM được cấp phát cho container
# - InitialRAMPercentage=50.0: Cấp phát trước 50% RAM để tối ưu tốc độ khởi động
# - java.security.egd: Sử dụng nguồn sinh số ngẫu nhiên non-blocking urandom
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:InitialRAMPercentage=50.0 \
               -Djava.security.egd=file:/dev/./urandom \
               -Duser.timezone=Asia/Ho_Chi_Minh \
               -Dfile.encoding=UTF-8"

# Kiểm tra tình trạng hoạt động của ứng dụng (Healthcheck)
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/ || exit 1

# Lệnh khởi động Spring Boot
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
