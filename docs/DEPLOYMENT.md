# Triển khai lên Linux (lab)

Workflow CD có sẵn nhưng chưa triển khai trên tài khoản hay máy chủ nào.

## Chuẩn bị

1. Tạo repository riêng với nội dung project ở root, push main và đợi CI publish image thành công.
2. Chuẩn bị Linux có Docker Engine, Compose v2 và curl. User triển khai cần quyền chạy Docker. Dùng tài khoản SSH riêng và key Ed25519 riêng cho lab.
3. Trên server tạo `~/task-manager/.env` từ `.env.example`, đổi mật khẩu, giữ `HTTP_PORT=8080`. Đặt quyền `chmod 600 ~/task-manager/.env`.
4. Nếu GHCR package private, đăng nhập GHCR trên server bằng token có `read:packages` qua `docker login ghcr.io --password-stdin`, hoặc đặt package public cho portfolio. Workflow không chuyển registry token lên server.
5. Trong GitHub Settings → Environments tạo `lab` với các secrets sau. Có thể thêm required reviewers tùy tài khoản.

| Secret | Nội dung |
|---|---|
| DEPLOY_HOST | IP hoặc hostname server, SSH cổng 22 |
| DEPLOY_USER | Username SSH |
| DEPLOY_SSH_KEY | Private key Ed25519; public key tương ứng nằm trong authorized_keys trên server |
| DEPLOY_KNOWN_HOSTS | Dòng known_hosts cho server; lấy và đối chiếu fingerprint qua kênh tin cậy |

Giữ kiểm tra host key. GitHub-hosted runner cần kết nối được tới cổng SSH của server theo cấu hình mạng của bạn.

## Deploy

Actions → Deploy to Linux server → Run workflow trên main → nhập SHA 40 ký tự của commit có image đã được CI publish. Workflow upload cấu hình, pull image, khởi động services, restart Nginx để cập nhật địa chỉ API, rồi kiểm tra health. `.env` được giữ trên server.

Cổng ứng dụng chỉ bind localhost. Để xem từ laptop:

```bash
ssh -L 8080:127.0.0.1:8080 user@server
```

Gọi `http://localhost:8080/health` hoặc API. Đây là lab chưa có xác thực/TLS; cần bổ sung các phần này trước khi public.

## Rollback và dữ liệu

Chạy lại workflow với SHA phiên bản tốt trước đó. Thao tác này đổi image, không rollback dữ liệu hay migration. Backup database trước khi thay schema; migration mới cần tương thích với ứng dụng cũ nếu muốn rollback image.

Deployment một máy chủ có thể gián đoạn ngắn, chưa có zero downtime. Trên server, đặt APP_IMAGE trước khi chạy lệnh Compose:

```bash
cd ~/task-manager
export APP_IMAGE=ghcr.io/owner/repo:FULL_SHA
docker compose -f compose.deploy.yaml ps
docker compose -f compose.deploy.yaml logs
```

Không xóa volume để sửa lỗi deploy. Không in `.env` hay private key ra logs.
