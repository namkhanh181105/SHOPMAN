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
   - Mở **XAMPP Control Panel**, khởi động **MySQL** (cổng `3306`). Có thể giữ **Apache** (cổng `8080`) để mở phpMyAdmin; tuyệt đối không bật Tomcat.
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
```

- `src/main/java/model/`: Các lớp thực thể OOP của hệ thống (`Member8`, `Customer8`, `Employee8`, `Manager8`, `SalesStaff8`, `DeliveryStaff8`, `Supplier8`, `SupplierImportStatistics8`, `Product8`, `ImportStatistics8`, `ExportStatistics8`, `CustomerStatistic8`, `Invoice8`, `InvoiceDetail8`, `ImportInvoice8`, `ImportInvoiceDetail8`).
- `src/main/java/dao/`: Lớp nền `DAO8`, ngoại lệ `DataAccessException8` và các DAO JDBC (`MemberDAO8`, `InvoiceDAO8`, `InvoiceDetailDAO8`, `SupplierDAO8`, `ImportInvoiceDAO8`, `ImportInvoiceDetailDAO8`).
- `src/main/java/filter/`: Bộ lọc `CharacterEncodingFilter8` (UTF-8) và `AuthenticationFilter8` (kiểm tra session, phân quyền `/customer/*`, `/manager/*`).
- `src/main/java/util/`: Tiện ích dùng chung (`DBUtil8`, `PasswordUtil8`, `DateUtil8`, `PortCheckUtil8`).
- `src/main/resources/`: Chứa `db.properties.example` làm mẫu cấu hình kết nối DB; tệp `db.properties` thực tế do người dùng tạo cục bộ và được `.gitignore` bỏ qua.
- `src/main/webapp/`: Mã nguồn giao diện JSP/JSTL theo vai trò (`member/`, `customer/`, `manager/`), trang gốc `index.jsp` và trang lỗi `error.jsp`.
- `src/main/webapp/assets/`: Tài nguyên tĩnh dùng chung gồm `css/style.css` và `js/script.js`.
- `src/main/webapp/WEB-INF/`: Chứa `web.xml` khai báo welcome file, filter, timeout session và trang xử lý lỗi.
- `src/test/java/`: Bộ kiểm thử JUnit trong `dao/`, `model/`, `server/` và `util/`.
- `sql/`: Chứa `shopman_db.sql` tạo database `shopman`, bảng và dữ liệu mẫu.
- `.mvn/`, `mvnw`, `mvnw.cmd`: Maven Wrapper giúp build và chạy dự án mà không cần cài sẵn Maven toàn cục.
- `pom.xml`, `.gitignore`, `README.md`: Khai báo thư viện/plugin Maven + Jetty, quy tắc loại trừ Git và tài liệu hướng dẫn.
- `target/`: Thư mục sinh tự động khi biên dịch/đóng gói Maven (`classes/`, `ShopMan.war`...), đã được `.gitignore` loại trừ và không đưa lên GitHub.
