# Kết quả kiểm tra tại máy tạo project

Ngày 02/11/2026:

- `mvn -B verify`: BUILD SUCCESS; 5 tests, 0 failures, 0 errors, 0 skipped.
- Chạy bằng JDK 19 có sẵn trên máy, biên dịch target Java 17. Docker và CI cấu hình JDK 17; chưa thực thi trên JDK 17 tại máy này.
- Đã parse các file YAML của ứng dụng, Compose và workflows; kiểm tra cú pháp Python và encoding UTF-8 của tài liệu.
- Chưa chạy Docker build/Compose/MySQL/Nginx tại máy này vì không có lệnh Docker.
- GitHub Actions, GHCR publishing và SSH deployment chưa được chạy. Cần repository/server và cấu hình trong DEPLOYMENT.md.

YAML parse chỉ kiểm tra cú pháp, không chứng minh workflow hoặc cấu hình container chạy thành công. Job integration được chuẩn bị để kiểm tra stack thật trên GitHub Actions.
