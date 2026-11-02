# Lộ trình thực hành

## 1. Hiểu API

Đọc Task → TaskRequest → TaskRepository → TaskController. Tự gọi CRUD, gửi title rỗng, status sai và id không tồn tại. Giải thích vì sao trả 201, 204, 400 hoặc 404. Đọc test completeLifecycle trước khi sửa code.

## 2. Hiểu database

Đọc migration V1 và cấu hình ddl-auto validate. Tạo task, down/up stack, chứng minh task còn. Phân biệt container với volume. Không sửa migration V1 sau khi đã chạy trên database dùng lâu dài; thêm V2 cho thay đổi tiếp theo.

## 3. Hiểu Docker và networking

Giải thích hai stage trong Dockerfile, vì sao runtime chỉ cần JRE, vì sao không chạy root. Vì sao DB_URL dùng hostname mysql thay vì localhost trong container? Phân biệt cổng Nginx trên host với cổng API nội bộ. Healthcheck và depends_on giúp chờ database sẵn sàng thế nào?

## 4. Hiểu CI/CD

Tạo branch, sửa một test cho fail và quan sát PR checks; sửa lại để pass. Merge main, xem image trên GHCR. Phân biệt test/build/push (CI) với deploy lên server (CD). Giải thích vì sao tag SHA giúp truy vết và vì sao không đưa secrets vào repository.

## 5. Bằng chứng portfolio

Tự chụp sơ đồ kiến trúc, docker compose ps, smoke test, Actions pass và GHCR package. Quay demo: tạo task → sửa DONE → down/up → dữ liệu còn. Không đưa mật khẩu vào ảnh/video. Ghi lỗi thực tế và cách tự xử lý. Chỉ ghi kết quả bạn đã thực hành trong CV.

## Bài tập mở rộng

- Thêm deadline bằng migration V2, cập nhật API và test.
- Thêm MySQL integration tests bằng Testcontainers.
- Thêm authentication, TLS, backup/restore trước khi public.
- Triển khai lên EC2 sau khi hiểu chi phí và cách dọn tài nguyên.
