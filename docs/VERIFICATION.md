# Kết quả kiểm tra tại máy tạo project

Ngày 02/11/2026:

- `mvn -B -ntp verify`: BUILD SUCCESS; 5 tests, 0 failures, 0 errors. Chạy trên JDK 19, biên dịch target Java 17.
- Docker multi-stage build thành công với JDK 17. Maven dùng BuildKit cache; bỏ bước dependency:go-offline tải nhiều plugin không cần thiết.
- Docker Compose chạy MySQL 8.4, Spring Boot API và Nginx. Health qua Nginx: `UP`.
- `python scripts/smoke-test.py`: PASS cho health, tạo, đọc, sửa, lọc và xóa task qua Nginx/MySQL thật. Có kiểm tra createdAt giữ nguyên khi đọc lại sau khi tạo.
- Đã tạo lại toàn bộ container bằng down/up, giữ volume MySQL; task mẫu id=2 vẫn còn. Dữ liệu mẫu vẫn được giữ để thực hành.
- Phát hiện độ chính xác timestamp khác nhau giữa Instant và TIMESTAMP(6); đã chuẩn hóa về microsecond và thêm kiểm thử hồi quy.
- MySQL khởi tạo lần đầu mất hơn thời gian chờ cũ trên máy này. Đã thêm start_period 120s, 60 lần retry và tăng thời gian chờ Compose/CI/deploy lên 600 giây.
- Compose config đã kiểm tra cho cả bản local và bản deploy. GitHub Actions, GHCR publishing và SSH deployment chưa được chạy trong phiên này.

Ứng dụng đang mở ở `http://localhost:8080/api/tasks`; health ở `http://localhost:8080/health`. Không có giao diện web ở đường dẫn `/`.
