# Stage 1: Build ứng dụng
FROM maven:3.9.8-amazoncorretto-21 AS build

WORKDIR /app

# Copy file cấu hình trước để tận dụng cache
COPY pom.xml .
COPY src ./src

# Build project, skip test để tăng tốc
RUN mvn clean package -DskipTests

# Stage 2: Image chạy ứng dụng
FROM amazoncorretto:21.0.4

WORKDIR /app

# Copy JAR từ giai đoạn build. Dùng wildcard để tránh lỗi khi đổi version
COPY --from=build /app/target/*.jar app.jar

## (Tuỳ chọn) Mở cổng nếu app chạy trên port 8080
#EXPOSE 8080

# Lệnh khởi chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
