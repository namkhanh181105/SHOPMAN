# ShopMan

## 1. Hướng dẫn chạy dự án

### Yêu cầu môi trường
- **JDK 17 trở lên** (theo cấu hình `maven.compiler.release=17` trong `pom.xml`; thiết lập `JAVA_HOME` trỏ đúng thư mục JDK nếu cần).
- **XAMPP** dùng dịch vụ **MySQL/MariaDB** (cổng `3306`). Không bật Tomcat; không dừng hoặc thay đổi cấu hình Apache XAMPP đang chạy trên cổng `8080`.
- **Kết nối Internet** trong lần chạy đầu tiên để Maven Wrapper tải Apache Maven (`3.9.11`) và các thư viện phụ thuộc.

### Các bước cài đặt và chạy (PowerShell)

1. **Lấy mã nguồn từ GitHub và di chuyển vào thư mục chứa `pom.xml`** (thay `<URL_REPO_GITHUB>` và `<thu-muc-du-an>` theo repository của bạn):
   ```powershell
   git clone <URL_REPO_GITHUB>
   cd <thu-muc-du-an>
   ```

2. **Khởi động MySQL trong XAMPP và import cơ sở dữ liệu**:
   - Mở **XAMPP Control Panel**, khởi động **MySQL** (cổng `3306`). Có thể giữ **Apache** (cổng `8080`) để mở phpMyAdmin.
   - Mở `http://localhost:8080/phpmyadmin/`, chọn **Import** và nạp tệp `sql/shopman_db.sql` để tạo CSDL `shopman`, 12 bảng và dữ liệu mẫu.

3. **Tạo và cấu hình tệp kết nối cơ sở dữ liệu**:
   - Sao chép tệp cấu hình mẫu thành tệp cấu hình cục bộ (tệp `db.properties` được `.gitignore` bỏ qua để không đẩy mật khẩu cá nhân lên GitHub):
     ```powershell
     Copy-Item src\main\resources\db.properties.example src\main\resources\db.properties
     ```
   - Mở `src\main\resources\db.properties` và cập nhật `db.url`, `db.username`, `db.password` theo MySQL trên máy (XAMPP mặc định dùng `db.username=root` và `db.password=` để trống; nếu chưa tạo `db.properties`, `DBUtil8` tạm nạp từ `db.properties.example`).

5. **Build và chạy kiểm thử bằng Maven Wrapper**:
   ```powershell
   .\mvnw.cmd clean verify
   ```

6. **Khởi chạy máy chủ web Jetty**:
   ```powershell
   .\mvnw.cmd jetty:run
   ```
   - Ứng dụng chạy trên cổng mặc định `9090`, context path `/ShopMan`.
   - Địa chỉ truy cập: `http://localhost:9090/ShopMan/`
   - Nếu cổng `9090` đang bận, đổi sang cổng `9091` (không tắt tiến trình đang chiếm cổng):
     ```powershell
     .\mvnw.cmd "-Djetty.http.port=9091" jetty:run
     ```
   - Nhấn **`Ctrl + C`** trong terminal để dừng Jetty.

## 2. Cấu trúc thư mục dự án

```text
ShopMan/
├── .mvn/
│   └── wrapper/
│       └── maven-wrapper.properties
├── sql/
│   └── shopman_db.sql
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── dao/
│   │   │   ├── filter/
│   │   │   ├── model/
│   │   │   └── util/
│   │   ├── resources/
│   │   │   └── db.properties.example
│   │   └── webapp/
│   │       ├── assets/
│   │       │   ├── css/
│   │       │   │   └── style.css
│   │       │   └── js/
│   │       │       └── script.js
│   │       ├── customer/
│   │       │   ├── CustomerMainMenu8.jsp
│   │       │   ├── InvoiceDetailView8.jsp
│   │       │   └── InvoiceTrackingMenu8.jsp
│   │       ├── manager/
│   │       │   ├── ImportHistoryView8.jsp
│   │       │   ├── ImportInvoiceDetailView8.jsp
│   │       │   ├── ManagerMainMenu8.jsp
│   │       │   └── SupplierImportStatisticsView8.jsp
│   │       ├── member/
│   │       │   ├── LoginView8.jsp
│   │       │   └── Logout8.jsp
│   │       ├── WEB-INF/
│   │       │   └── web.xml
│   │       ├── error.jsp
│   │       └── index.jsp
│   └── test/
│       └── java/
│           ├── dao/
│           ├── model/
│           ├── server/
│           └── util/
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
