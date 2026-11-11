-- =====================================================================
-- Cơ sở dữ liệu hệ thống ShopMan - Theo đúng sơ đồ CSDL trong báo cáo
-- =====================================================================

CREATE DATABASE IF NOT EXISTS shopman CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shopman;

-- 1. Bảng tblMember8 (Thành viên)
CREATE TABLE IF NOT EXISTS tblMember8 (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phoneNumber VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Bảng tblCustomer8 (Khách hàng - kế thừa tblMember8)
CREATE TABLE IF NOT EXISTS tblCustomer8 (
    tblMember8id INT(10) PRIMARY KEY,
    address VARCHAR(255),
    CONSTRAINT fk_customer_member FOREIGN KEY (tblMember8id)
        REFERENCES tblMember8(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Bảng tblEmployee8 (Nhân viên - kế thừa tblMember8)
CREATE TABLE IF NOT EXISTS tblEmployee8 (
    tblMember8id INT(10) PRIMARY KEY,
    position VARCHAR(255),
    CONSTRAINT fk_employee_member FOREIGN KEY (tblMember8id)
        REFERENCES tblMember8(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Bảng tblManager8 (Nhân viên quản lý - kế thừa tblEmployee8)
CREATE TABLE IF NOT EXISTS tblManager8 (
    tblEmployee8id INT(10) PRIMARY KEY,
    CONSTRAINT fk_manager_employee FOREIGN KEY (tblEmployee8id)
        REFERENCES tblEmployee8(tblMember8id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Bảng tblSalesStaff8 (Nhân viên bán hàng - kế thừa tblEmployee8)
CREATE TABLE IF NOT EXISTS tblSalesStaff8 (
    tblEmployee8id INT(10) PRIMARY KEY,
    CONSTRAINT fk_salesstaff_employee FOREIGN KEY (tblEmployee8id)
        REFERENCES tblEmployee8(tblMember8id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Bảng tblDeliveryStaff8 (Nhân viên giao hàng - kế thừa tblEmployee8)
CREATE TABLE IF NOT EXISTS tblDeliveryStaff8 (
    tblEmployee8id INT(10) PRIMARY KEY,
    CONSTRAINT fk_deliverystaff_employee FOREIGN KEY (tblEmployee8id)
        REFERENCES tblEmployee8(tblMember8id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. Bảng tblSupplier8 (Nhà cung cấp)
CREATE TABLE IF NOT EXISTS tblSupplier8 (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phoneNumber VARCHAR(255),
    email VARCHAR(255),
    address VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. Bảng tblProduct8 (Hàng hóa/Sản phẩm)
CREATE TABLE IF NOT EXISTS tblProduct8 (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255),
    image VARCHAR(255),
    unit VARCHAR(255),
    price DOUBLE(10,2) NOT NULL DEFAULT 0.0,
    stockQuantity INT(10) NOT NULL DEFAULT 0,
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. Bảng tblInvoice8 (Hóa đơn bán hàng)
CREATE TABLE IF NOT EXISTS tblInvoice8 (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    createdDate DATE NOT NULL,
    status VARCHAR(255) NOT NULL,
    tblCustomer8id INT(10) NOT NULL,
    tblSalesStaff8id INT(10) NULL,
    tblDeliveryStaff8id INT(10) NULL,
    CONSTRAINT fk_invoice_customer FOREIGN KEY (tblCustomer8id)
        REFERENCES tblCustomer8(tblMember8id) ON DELETE CASCADE,
    CONSTRAINT fk_invoice_salesstaff FOREIGN KEY (tblSalesStaff8id)
        REFERENCES tblSalesStaff8(tblEmployee8id) ON DELETE SET NULL,
    CONSTRAINT fk_invoice_deliverystaff FOREIGN KEY (tblDeliveryStaff8id)
        REFERENCES tblDeliveryStaff8(tblEmployee8id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. Bảng tblInvoiceDetail8 (Chi tiết hóa đơn bán hàng)
CREATE TABLE IF NOT EXISTS tblInvoiceDetail8 (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    unitPrice DOUBLE(10,2) NOT NULL,
    quantity INT(10) NOT NULL,
    tblProduct8id INT(10) NOT NULL,
    tblInvoice8id INT(10) NOT NULL,
    CONSTRAINT fk_invoicedetail_product FOREIGN KEY (tblProduct8id)
        REFERENCES tblProduct8(id) ON DELETE CASCADE,
    CONSTRAINT fk_invoicedetail_invoice FOREIGN KEY (tblInvoice8id)
        REFERENCES tblInvoice8(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 11. Bảng tblImportInvoice8 (Hóa đơn nhập hàng)
CREATE TABLE IF NOT EXISTS tblImportInvoice8 (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    createdDate DATE NOT NULL,
    status VARCHAR(255) NOT NULL,
    tblSupplier8id INT(10) NOT NULL,
    tblManager8id INT(10) NOT NULL,
    CONSTRAINT fk_importinvoice_supplier FOREIGN KEY (tblSupplier8id)
        REFERENCES tblSupplier8(id) ON DELETE CASCADE,
    CONSTRAINT fk_importinvoice_manager FOREIGN KEY (tblManager8id)
        REFERENCES tblManager8(tblEmployee8id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 12. Bảng tblImportInvoiceDetail8 (Chi tiết hóa đơn nhập hàng)
CREATE TABLE IF NOT EXISTS tblImportInvoiceDetail8 (
    id INT(10) AUTO_INCREMENT PRIMARY KEY,
    unitPrice DOUBLE(10,2) NOT NULL,
    quantity INT(10) NOT NULL,
    tblProduct8id INT(10) NOT NULL,
    tblImportInvoice8id INT(10) NOT NULL,
    CONSTRAINT fk_importdetail_product FOREIGN KEY (tblProduct8id)
        REFERENCES tblProduct8(id) ON DELETE CASCADE,
    CONSTRAINT fk_importdetail_importinvoice FOREIGN KEY (tblImportInvoice8id)
        REFERENCES tblImportInvoice8(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================================
-- DỮ LIỆU MẪU THEO ĐÚNG CÁC HÌNH VẼ TRONG BÁO CÁO (KHÁCH HÀNG & QUẢN LÝ)
-- Mật khẩu mặc định của tất cả tài khoản mới: 123456
-- Cơ chế băm: PBKDF2WithHmacSHA256 kèm salt ngẫu nhiên 16 byte (65536 vòng lặp)
-- Lưu ý: ON DUPLICATE KEY UPDATE chỉ cập nhật thông tin hồ sơ, KHÔNG ghi đè mật khẩu tài khoản đã tồn tại.
-- =====================================================================

-- Thêm thành viên (gồm 2 khách hàng và các vai trò nhân viên)
INSERT INTO tblMember8 (id, name, username, password, email, phoneNumber) VALUES
(1, 'Nguyễn Văn An', 'khachhang1', 'pbkdf2_sha256$65536$IHFngwMiA47rQJ/WtN0p8Q==$Lz7oajf73NXakhF6DFeCwoO1YVISQABb8OMKPPHiN/I=', 'an.nguyen@example.com', '0912345678'),
(2, 'Trần Văn Bình', 'quanly1', 'pbkdf2_sha256$65536$ocT2rHzzqJ9765a6dHcoJw==$Ut6rCxdbIuWvHO5edtjb2crdymtK/kzdr7PvtI0oZy8=', 'binh.tran@example.com', '0987654321'),
(3, 'Lê Thị Cúc', 'banhang1', 'pbkdf2_sha256$65536$STqu40YsUqX9qvLSdZ78Iw==$DHankwbIcDqKo0YTpiDSaomJy1UobfmJrbinjfjgBIg=', 'cuc.le@example.com', '0901122334'),
(4, 'Phạm Văn Dũng', 'giaohang1', 'pbkdf2_sha256$65536$dGElZrF3xGqG2OF/NNvCxg==$71BCkRG0ufwJJ5N6HrLS2i6Wirc0nN8xWoRR9XWVpx8=', 'dung.pham@example.com', '0933445566'),
(5, 'Hoàng Minh Đức', 'khachhang2', 'pbkdf2_sha256$65536$iBc21eclTZGfwfzLYLWrxA==$hOWEAWsdYt/3d+BUAdmFkWBPkFAcW59/SJKHAieCWbo=', 'duc.hoang@example.com', '0944556677')
ON DUPLICATE KEY UPDATE name=VALUES(name), email=VALUES(email), phoneNumber=VALUES(phoneNumber);

-- Phân quyền
INSERT INTO tblCustomer8 (tblMember8id, address) VALUES
(1, 'Số 12 Chùa Bộc, Đống Đa, Hà Nội'),
(5, 'Số 96 Nguyễn Trãi, Thanh Xuân, Hà Nội')
ON DUPLICATE KEY UPDATE address=VALUES(address);

INSERT INTO tblEmployee8 (tblMember8id, position) VALUES
(2, 'Quản lý cửa hàng'),
(3, 'Nhân viên bán hàng'),
(4, 'Nhân viên giao hàng')
ON DUPLICATE KEY UPDATE position=VALUES(position);

INSERT INTO tblManager8 (tblEmployee8id) VALUES (2) ON DUPLICATE KEY UPDATE tblEmployee8id=VALUES(tblEmployee8id);
INSERT INTO tblSalesStaff8 (tblEmployee8id) VALUES (3) ON DUPLICATE KEY UPDATE tblEmployee8id=VALUES(tblEmployee8id);
INSERT INTO tblDeliveryStaff8 (tblEmployee8id) VALUES (4) ON DUPLICATE KEY UPDATE tblEmployee8id=VALUES(tblEmployee8id);

-- Thêm Sản phẩm (MH01, MH02, MH03)
INSERT INTO tblProduct8 (id, name, type, image, unit, price, stockQuantity, description) VALUES
(1, 'Sữa Vinamilk', 'Đồ uống', 'milk.png', 'Hộp', 500.0, 100, 'Sữa tươi tiệt trùng Vinamilk 180ml'),
(2, 'Mì chính', 'Gia vị', 'michinh.png', 'Gói', 300.0, 80, 'Mì chính Ajinomoto 454g'),
(3, 'Bột canh', 'Gia vị', 'botcanh.png', 'Gói', 200.0, 120, 'Bột canh Hải Châu 190g')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Thêm Hóa đơn bán hàng:
-- Hóa đơn đang đặt của khachhang1 (id=1):
--   HD01: 22/07/2026, 1000, Đã thanh toán (vẫn là hóa đơn đang đặt chờ giao theo Hình 17)
--   HD02: 20/09/2026, 20000, Đang giao
--   HD03: 22/09/2026, 30000, Chờ duyệt (chưa phân công NV giao hàng => tblDeliveryStaff8id NULL)
-- Hóa đơn kết thúc của khachhang1 (không hiển thị trong "hóa đơn đang đặt"):
--   HD04: 15/06/2026, 1000, Đã giao
--   HD05: 18/06/2026, 1500, Đã hủy (chưa phân công cả NV bán hàng và NV giao hàng)
-- Hóa đơn của khách hàng khác (khachhang2, id=5):
--   HD06: 25/09/2026, 2000, Đang giao
INSERT INTO tblInvoice8 (id, createdDate, status, tblCustomer8id, tblSalesStaff8id, tblDeliveryStaff8id) VALUES
(1, '2026-07-22', 'Đã thanh toán', 1, 3, 4),
(2, '2026-09-20', 'Đang giao', 1, 3, 4),
(3, '2026-09-22', 'Chờ duyệt', 1, 3, NULL),
(4, '2026-06-15', 'Đã giao', 1, 3, 4),
(5, '2026-06-18', 'Đã hủy', 1, NULL, NULL),
(6, '2026-09-25', 'Đang giao', 5, 3, NULL)
ON DUPLICATE KEY UPDATE status=VALUES(status), tblCustomer8id=VALUES(tblCustomer8id),
    tblSalesStaff8id=VALUES(tblSalesStaff8id), tblDeliveryStaff8id=VALUES(tblDeliveryStaff8id);

-- Chi tiết hóa đơn HD01 (MH01: 500*1=500, MH02: 300*1=300, MH03: 200*1=200 => Tổng 1000)
INSERT INTO tblInvoiceDetail8 (id, unitPrice, quantity, tblProduct8id, tblInvoice8id) VALUES
(1, 500.0, 1, 1, 1),
(2, 300.0, 1, 2, 1),
(3, 200.0, 1, 3, 1),
-- Chi tiết HD02 (Tổng 20000)
(4, 500.0, 40, 1, 2),
-- Chi tiết HD03 (Tổng 30000)
(5, 500.0, 60, 1, 3),
-- Chi tiết HD04 (Đã giao, Tổng 1000)
(6, 500.0, 2, 1, 4),
-- Chi tiết HD05 (Đã hủy, Tổng 1500)
(7, 300.0, 5, 2, 5),
-- Chi tiết HD06 (khachhang2, Tổng 2000)
(8, 200.0, 10, 3, 6)
ON DUPLICATE KEY UPDATE quantity=VALUES(quantity);

-- Thêm Nhà cung cấp theo Hình 20
-- NCC01: NCC-Khánh
-- NCC02: NCC-Huy
-- NCC03: NCC-Nam
INSERT INTO tblSupplier8 (id, name, phoneNumber, email, address) VALUES
(1, 'NCC-Khánh', '0912111222', 'khanh.supplier@example.com', 'Hà Nội'),
(2, 'NCC-Huy', '0913222333', 'huy.supplier@example.com', 'Hải Phòng'),
(3, 'NCC-Nam', '0914333444', 'nam.supplier@example.com', 'Đà Nẵng')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Thêm Hóa đơn nhập hàng theo Hình 20, 21, 22
-- Khoảng thời gian thống kê: 01/05/2026 đến 31/07/2026
-- NCC-Khánh:
--   NH01: 04/05/2026, Đã thanh toán, số lượng 100, tổng tiền 34000
--   NH02: 02/06/2026, Đã thanh toán, số lượng 200, tổng tiền 40000
--   NH03: 01/07/2026, Đã thanh toán, số lượng 300, tổng tiền 60000
--   => Tổng lượng nhập: 100 + 200 + 300 = 600, ngày gần nhất: 01/07/2026
INSERT INTO tblImportInvoice8 (id, createdDate, status, tblSupplier8id, tblManager8id) VALUES
(1, '2026-05-04', 'Đã thanh toán', 1, 2),
(2, '2026-06-02', 'Đã thanh toán', 1, 2),
(3, '2026-07-01', 'Đã thanh toán', 1, 2),
-- NCC-Huy: NH04 ngày 01/07/2026, số lượng 200
(4, '2026-07-01', 'Đã thanh toán', 2, 2),
-- NCC-Nam: NH05 ngày 09/07/2026, số lượng 300
(5, '2026-07-09', 'Đã thanh toán', 3, 2)
ON DUPLICATE KEY UPDATE status=VALUES(status);

-- Chi tiết hóa đơn nhập NH01 (Hình 22: MH01: 500*40=20000, MH02: 300*20=6000, MH03: 200*40=8000 => Qty 100, Total 34000)
INSERT INTO tblImportInvoiceDetail8 (id, unitPrice, quantity, tblProduct8id, tblImportInvoice8id) VALUES
(1, 500.0, 40, 1, 1),
(2, 300.0, 20, 2, 1),
(3, 200.0, 40, 3, 1),
-- Chi tiết NH02 (200 units, 40000)
(4, 200.0, 200, 3, 2),
-- Chi tiết NH03 (300 units, 60000)
(5, 200.0, 300, 3, 3),
-- Chi tiết NH04 (NCC-Huy: 200 units)
(6, 200.0, 200, 3, 4),
-- Chi tiết NH05 (NCC-Nam: 300 units)
(7, 200.0, 300, 3, 5)
ON DUPLICATE KEY UPDATE quantity=VALUES(quantity);

-- =====================================================================
-- HƯỚNG DẪN CHUYỂN ĐỔI / ĐẶT LẠI MẬT KHẨU CHO TÀI KHOẢN CŨ DÙNG SHA-256
-- PasswordUtil8 không còn chấp nhận SHA-256 cũ hoặc mật khẩu rõ.
-- Nếu CSDL cũ đã tồn tại mật khẩu SHA-256 (64 ký tự hex), hãy tạo hash PBKDF2 mới bằng:
--   java -cp target/classes util.PasswordUtil8 <mat-khau-moi>
-- rồi chạy lệnh SQL (thay thế username và chuỗi hash tương ứng):
--   UPDATE tblMember8 SET password = 'pbkdf2_sha256$65536$<salt>$<hash>' WHERE username = 'khachhang1';
-- =====================================================================

