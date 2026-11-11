package dao;

import model.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import util.DBUtil8;
import util.DateUtil8;
import util.PasswordUtil8;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Kiểm thử tự động toàn diện các phương thức của DAO trong môi trường H2 In-Memory độc lập.
 */
public class DAOTest {

    private static final String H2_URL = "jdbc:h2:mem:testshopman;MODE=MySQL;DATABASE_TO_UPPER=FALSE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1";
    private static Connection masterConnection;

    @BeforeClass
    public static void setUp() throws Exception {
        // masterConnection giữ in-memory database sống cho toàn bộ test suite
        masterConnection = DriverManager.getConnection(H2_URL, "sa", "");

        // Tiêm ConnectionSupplier trả về kết nối độc lập cho mỗi lời gọi JDBC
        DBUtil8.setTestConnectionSupplier(() -> DriverManager.getConnection(H2_URL, "sa", ""));

        // Tạo cấu trúc bảng
        try (Statement stmt = masterConnection.createStatement()) {
            stmt.execute("CREATE TABLE tblMember8 (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "username VARCHAR(255) NOT NULL UNIQUE, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "email VARCHAR(255), " +
                    "phoneNumber VARCHAR(20)" +
                    ")");

            stmt.execute("CREATE TABLE tblCustomer8 (" +
                    "tblMember8id INT PRIMARY KEY, " +
                    "address VARCHAR(255), " +
                    "FOREIGN KEY (tblMember8id) REFERENCES tblMember8(id) ON DELETE CASCADE" +
                    ")");

            stmt.execute("CREATE TABLE tblEmployee8 (" +
                    "tblMember8id INT PRIMARY KEY, " +
                    "position VARCHAR(255), " +
                    "FOREIGN KEY (tblMember8id) REFERENCES tblMember8(id) ON DELETE CASCADE" +
                    ")");

            stmt.execute("CREATE TABLE tblManager8 (" +
                    "tblEmployee8id INT PRIMARY KEY, " +
                    "FOREIGN KEY (tblEmployee8id) REFERENCES tblEmployee8(tblMember8id) ON DELETE CASCADE" +
                    ")");

            stmt.execute("CREATE TABLE tblSalesStaff8 (" +
                    "tblEmployee8id INT PRIMARY KEY, " +
                    "FOREIGN KEY (tblEmployee8id) REFERENCES tblEmployee8(tblMember8id) ON DELETE CASCADE" +
                    ")");

            stmt.execute("CREATE TABLE tblDeliveryStaff8 (" +
                    "tblEmployee8id INT PRIMARY KEY, " +
                    "FOREIGN KEY (tblEmployee8id) REFERENCES tblEmployee8(tblMember8id) ON DELETE CASCADE" +
                    ")");

            stmt.execute("CREATE TABLE tblSupplier8 (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "phoneNumber VARCHAR(255), " +
                    "email VARCHAR(255), " +
                    "address VARCHAR(255)" +
                    ")");

            stmt.execute("CREATE TABLE tblProduct8 (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "type VARCHAR(255), " +
                    "image VARCHAR(255), " +
                    "unit VARCHAR(255), " +
                    "price DOUBLE NOT NULL DEFAULT 0.0, " +
                    "stockQuantity INT NOT NULL DEFAULT 0, " +
                    "description VARCHAR(255)" +
                    ")");

            stmt.execute("CREATE TABLE tblInvoice8 (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "createdDate DATE NOT NULL, " +
                    "status VARCHAR(255) NOT NULL, " +
                    "tblCustomer8id INT NOT NULL, " +
                    "tblSalesStaff8id INT NULL, " +
                    "tblDeliveryStaff8id INT NULL, " +
                    "FOREIGN KEY (tblCustomer8id) REFERENCES tblCustomer8(tblMember8id)" +
                    ")");

            stmt.execute("CREATE TABLE tblInvoiceDetail8 (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "unitPrice DOUBLE NOT NULL, " +
                    "quantity INT NOT NULL, " +
                    "tblProduct8id INT NOT NULL, " +
                    "tblInvoice8id INT NOT NULL, " +
                    "FOREIGN KEY (tblProduct8id) REFERENCES tblProduct8(id), " +
                    "FOREIGN KEY (tblInvoice8id) REFERENCES tblInvoice8(id)" +
                    ")");

            stmt.execute("CREATE TABLE tblImportInvoice8 (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "createdDate DATE NOT NULL, " +
                    "status VARCHAR(255) NOT NULL, " +
                    "tblSupplier8id INT NOT NULL, " +
                    "tblManager8id INT NOT NULL, " +
                    "FOREIGN KEY (tblSupplier8id) REFERENCES tblSupplier8(id)" +
                    ")");

            stmt.execute("CREATE TABLE tblImportInvoiceDetail8 (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "unitPrice DOUBLE NOT NULL, " +
                    "quantity INT NOT NULL, " +
                    "tblProduct8id INT NOT NULL, " +
                    "tblImportInvoice8id INT NOT NULL, " +
                    "FOREIGN KEY (tblProduct8id) REFERENCES tblProduct8(id), " +
                    "FOREIGN KEY (tblImportInvoice8id) REFERENCES tblImportInvoice8(id)" +
                    ")");

            // Nạp dữ liệu mẫu
            String hashed1 = PasswordUtil8.hashPassword("123456");
            String hashed2 = PasswordUtil8.hashPassword("123456");
            String hashed3 = PasswordUtil8.hashPassword("123456");
            String hashed4 = PasswordUtil8.hashPassword("123456");
            String hashed5 = PasswordUtil8.hashPassword("123456");
            String hashedSpace = PasswordUtil8.hashPassword(" 123456 ");
            String legacySha256 = "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92";

            stmt.execute("INSERT INTO tblMember8 (id, name, username, password, email, phoneNumber) VALUES " +
                    "(1, 'Nguyễn Văn An', 'khachhang1', '" + hashed1 + "', 'an@example.com', '0912345678'), " +
                    "(2, 'Trần Văn Bình', 'quanly1', '" + hashed2 + "', 'binh@example.com', '0987654321'), " +
                    "(3, 'Lê Thị Cúc', 'banhang1', '" + hashed3 + "', 'cuc@example.com', '0901122334'), " +
                    "(4, 'Phạm Văn Dũng', 'giaohang1', '" + hashed4 + "', 'dung@example.com', '0933445566'), " +
                    "(5, 'Hoàng Minh Đức', 'khachhang2', '" + hashed5 + "', 'duc@example.com', '0944556677'), " +
                    "(6, 'Người dùng khoảng trắng', 'spaceuser', '" + hashedSpace + "', 'space@example.com', '0955667788'), " +
                    "(7, 'Người dùng SHA256 cũ', 'legacysha', '" + legacySha256 + "', 'legacy@example.com', '0966778899')");

            stmt.execute("INSERT INTO tblCustomer8 (tblMember8id, address) VALUES " +
                    "(1, 'Số 12 Chùa Bộc, Hà Nội'), " +
                    "(5, 'Số 96 Nguyễn Trãi, Hà Nội'), " +
                    "(6, 'Địa chỉ 6'), " +
                    "(7, 'Địa chỉ 7')");
            stmt.execute("INSERT INTO tblEmployee8 (tblMember8id, position) VALUES " +
                    "(2, 'Quản lý cửa hàng'), " +
                    "(3, 'Nhân viên bán hàng'), " +
                    "(4, 'Nhân viên giao hàng')");
            stmt.execute("INSERT INTO tblManager8 (tblEmployee8id) VALUES (2)");
            stmt.execute("INSERT INTO tblSalesStaff8 (tblEmployee8id) VALUES (3)");
            stmt.execute("INSERT INTO tblDeliveryStaff8 (tblEmployee8id) VALUES (4)");

            stmt.execute("INSERT INTO tblProduct8 (id, name, type, image, unit, price, stockQuantity, description) VALUES " +
                    "(1, 'Sữa Vinamilk', 'Đồ uống', 'milk.png', 'Hộp', 500.0, 100, 'Sữa tươi 180ml'), " +
                    "(2, 'Mì chính', 'Gia vị', 'michinh.png', 'Gói', 300.0, 80, 'Mì chính 454g'), " +
                    "(3, 'Bột canh', 'Gia vị', 'botcanh.png', 'Gói', 200.0, 120, 'Bột canh 190g')");

            // Hóa đơn khachhang1: HD01 (Đã thanh toán), HD02 (Đang giao), HD03 (Chờ duyệt, thiếu NV giao hàng),
            // HD04 (Đã giao - bị loại), HD05 (Đã hủy & không có NV nào - bị loại), HD06 (Hoàn tất - bị loại)
            // Hóa đơn khachhang2: HD07 (Đang giao - thuộc khách hàng khác)
            stmt.execute("INSERT INTO tblInvoice8 (id, createdDate, status, tblCustomer8id, tblSalesStaff8id, tblDeliveryStaff8id) VALUES " +
                    "(1, '2026-07-22', 'Đã thanh toán', 1, 3, 4), " +
                    "(2, '2026-09-20', 'Đang giao', 1, 3, 4), " +
                    "(3, '2026-09-22', 'Chờ duyệt', 1, 3, NULL), " +
                    "(4, '2026-06-15', 'Đã giao', 1, 3, 4), " +
                    "(5, '2026-06-18', 'Đã hủy', 1, NULL, NULL), " +
                    "(6, '2026-06-20', 'Hoàn tất', 1, 3, 4), " +
                    "(7, '2026-09-25', 'Đang giao', 5, 3, NULL)");

            stmt.execute("INSERT INTO tblInvoiceDetail8 (id, unitPrice, quantity, tblProduct8id, tblInvoice8id) VALUES " +
                    "(1, 500.0, 1, 1, 1), " +
                    "(2, 300.0, 1, 2, 1), " +
                    "(3, 200.0, 1, 3, 1), " +
                    "(4, 500.0, 40, 1, 2), " +
                    "(5, 500.0, 60, 1, 3), " +
                    "(6, 500.0, 2, 1, 4), " +
                    "(7, 300.0, 5, 2, 5), " +
                    "(8, 200.0, 3, 3, 6), " +
                    "(9, 200.0, 10, 3, 7)");

            stmt.execute("INSERT INTO tblSupplier8 (id, name, phoneNumber, email, address) VALUES " +
                    "(1, 'NCC-Khánh', '0912111222', 'khanh@example.com', 'Hà Nội'), " +
                    "(2, 'NCC-Huy', '0913222333', 'huy@example.com', 'Hải Phòng')");

            stmt.execute("INSERT INTO tblImportInvoice8 (id, createdDate, status, tblSupplier8id, tblManager8id) VALUES " +
                    "(1, '2026-05-04', 'Đã thanh toán', 1, 2), " +
                    "(2, '2026-06-02', 'Đã thanh toán', 1, 2), " +
                    "(3, '2026-07-01', 'Đã thanh toán', 1, 2), " +
                    "(4, '2026-07-01', 'Đã thanh toán', 2, 2)");

            stmt.execute("INSERT INTO tblImportInvoiceDetail8 (id, unitPrice, quantity, tblProduct8id, tblImportInvoice8id) VALUES " +
                    "(1, 500.0, 40, 1, 1), " +
                    "(2, 300.0, 20, 2, 1), " +
                    "(3, 200.0, 40, 3, 1), " +
                    "(4, 200.0, 200, 3, 2), " +
                    "(5, 200.0, 300, 3, 3), " +
                    "(6, 200.0, 200, 3, 4)");
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        DBUtil8.resetConnectionSupplier();
        if (masterConnection != null && !masterConnection.isClosed()) {
            masterConnection.close();
        }
    }

    @Test
    public void testCheckLogin() {
        MemberDAO8 dao = new MemberDAO8();
        Member8 customer = dao.checkLogin("khachhang1", "123456");
        assertNotNull("Đăng nhập tài khoản khách hàng thành công", customer);
        assertTrue(customer instanceof Customer8);
        assertEquals("Nguyễn Văn An", customer.getName());
        assertNull("Không được giữ password/hash trong đối tượng Member8 trả về", customer.getPassword());

        Member8 manager = dao.checkLogin("quanly1", "123456");
        assertNotNull("Đăng nhập tài khoản quản lý thành công", manager);
        assertTrue(manager instanceof Manager8);
        assertEquals("Trần Văn Bình", manager.getName());
        assertNull("Không được giữ password/hash trong đối tượng Manager8 trả về", manager.getPassword());

        Member8 salesStaff = dao.checkLogin("banhang1", "123456");
        assertTrue(salesStaff instanceof SalesStaff8);
        assertNull(salesStaff.getPassword());

        Member8 deliveryStaff = dao.checkLogin("giaohang1", "123456");
        assertTrue(deliveryStaff instanceof DeliveryStaff8);
        assertNull(deliveryStaff.getPassword());

        // Kiểm tra không trim() mật khẩu
        assertNotNull("Mật khẩu giữ nguyên khoảng trắng phải đăng nhập được", dao.checkLogin("spaceuser", " 123456 "));
        assertNull("Mật khẩu bị trim khoảng trắng phải bị từ chối", dao.checkLogin("spaceuser", "123456"));

        // Kiểm tra từ chối hash SHA-256 cũ hoặc mật khẩu thô
        assertNull("Hash SHA-256 cũ không được chấp nhận", dao.checkLogin("legacysha", "123456"));
        assertNull("Không chấp nhận đăng nhập bằng chính chuỗi hash cũ",
                dao.checkLogin("legacysha", "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92"));

        assertNull(dao.checkLogin("khachhang1", "wrongpassword"));
        assertNull(dao.checkLogin("nonexistent", "123456"));
    }

    @Test
    public void testGetInvoiceList() {
        InvoiceDAO8 dao = new InvoiceDAO8();
        Invoice8[] list = dao.getInvoiceList(1);
        assertNotNull(list);
        // HD01 (Đã thanh toán), HD02 (Đang giao), HD03 (Chờ duyệt) được giữ;
        // HD04 (Đã giao), HD05 (Đã hủy), HD06 (Hoàn tất) và HD07 (của khách khác) bị loại
        assertEquals(3, list.length);

        // Kiểm tra sắp xếp theo ngày tạo từ sớm nhất đến muộn nhất (HD01 -> HD02 -> HD03)
        assertEquals(1, list[0].getId());
        assertEquals(2, list[1].getId());
        assertEquals(3, list[2].getId());

        // HD01 tổng tiền = 500*1 + 300*1 + 200*1 = 1000, trạng thái Đã thanh toán vẫn là hóa đơn đang đặt
        Invoice8 hd01 = list[0];
        assertEquals(1000.0, hd01.getTotalAmount(), 0.001);
        assertEquals("Đã thanh toán", hd01.getStatus());

        assertEquals(20000.0, list[1].getTotalAmount(), 0.001);
        assertEquals("Đang giao", list[1].getStatus());

        assertEquals(30000.0, list[2].getTotalAmount(), 0.001);
        assertEquals("Chờ duyệt", list[2].getStatus());

        // Khách hàng chưa có hóa đơn đang đặt phải trả về mảng rỗng (không ném lỗi)
        Invoice8[] emptyList = dao.getInvoiceList(999);
        assertNotNull(emptyList);
        assertEquals(0, emptyList.length);
    }

    @Test
    public void testGetInvoiceAndOwnershipAndStaffRelations() {
        InvoiceDAO8 dao = new InvoiceDAO8();

        // HD01: Có đầy đủ Customer8, SalesStaff8, DeliveryStaff8 và tổng tiền không bị nhân bản khi JOIN
        Invoice8 hd01 = dao.getInvoice(1, 1);
        assertNotNull(hd01);
        assertEquals(1, hd01.getId());
        assertEquals("Đã thanh toán", hd01.getStatus());
        assertEquals(1000.0, hd01.getTotalAmount(), 0.001);
        assertNotNull(hd01.getCustomer());
        assertEquals("Nguyễn Văn An", hd01.getCustomer().getName());
        assertNotNull("HD01 phải nạp SalesStaff8", hd01.getSalesStaff());
        assertEquals(3, hd01.getSalesStaff().getId());
        assertEquals("Lê Thị Cúc", hd01.getSalesStaff().getName());
        assertNotNull("HD01 phải nạp DeliveryStaff8", hd01.getDeliveryStaff());
        assertEquals(4, hd01.getDeliveryStaff().getId());
        assertEquals("Phạm Văn Dũng", hd01.getDeliveryStaff().getName());

        // HD03: Có SalesStaff8 (3) nhưng DeliveryStaff8 NULL => phải trả null, không tạo đối tượng id=0
        Invoice8 hd03 = dao.getInvoice(3, 1);
        assertNotNull(hd03);
        assertEquals(30000.0, hd03.getTotalAmount(), 0.001);
        assertNotNull(hd03.getSalesStaff());
        assertEquals(3, hd03.getSalesStaff().getId());
        assertNull("FK NULL cho nhân viên giao hàng phải trả về null", hd03.getDeliveryStaff());

        // HD05: Cả SalesStaff8 và DeliveryStaff8 đều NULL
        Invoice8 hd05 = dao.getInvoice(5, 1);
        assertNotNull(hd05);
        assertNull("FK NULL cho nhân viên bán hàng phải trả về null", hd05.getSalesStaff());
        assertNull("FK NULL cho nhân viên giao hàng phải trả về null", hd05.getDeliveryStaff());

        // Kiểm tra bảo mật: Khách hàng ID 1 không được xem HD07 của khách hàng ID 5 (khachhang2)
        assertNull("Khách hàng khác không được truy cập hóa đơn không thuộc sở hữu", dao.getInvoice(7, 1));
        assertNotNull("Chủ sở hữu đúng (khachhang2) xem được HD07", dao.getInvoice(7, 5));
        assertNull("Khách hàng ID 999 không được truy cập HD01", dao.getInvoice(1, 999));
    }

    @Test
    public void testGetInvoiceDetail() {
        InvoiceDetailDAO8 dao = new InvoiceDetailDAO8();
        InvoiceDetail8[] details = dao.getInvoiceDetail(1, 1);
        assertNotNull(details);
        assertEquals(3, details.length);

        assertEquals("Sữa Vinamilk", details[0].getProduct().getName());
        assertEquals(500.0, details[0].getUnitPrice(), 0.001);
        assertEquals(1, details[0].getQuantity());
        assertEquals(500.0, details[0].getAmount(), 0.001);

        // Khách hàng không sở hữu hóa đơn không lấy được chi tiết
        InvoiceDetail8[] emptyDetails = dao.getInvoiceDetail(1, 999);
        assertEquals(0, emptyDetails.length);
    }

    @Test
    public void testGetSupplierImportStatistics() {
        SupplierDAO8 dao = new SupplierDAO8();
        Date start = DateUtil8.parseDate("2026-05-01");
        Date end = DateUtil8.parseDate("2026-07-31");

        SupplierImportStatistics8[] stats = dao.getSupplierImportStatistics(start, end);
        assertNotNull(stats);
        assertEquals(2, stats.length);

        // NCC-Khánh có 100 + 200 + 300 = 600 sản phẩm nhập
        SupplierImportStatistics8 khanh = stats[0];
        assertEquals("NCC-Khánh", khanh.getName());
        assertEquals(600, khanh.getTotalQuantity());
        assertNotNull(khanh.getLastImportDate());

        // NCC-Huy có 200 sản phẩm nhập
        SupplierImportStatistics8 huy = stats[1];
        assertEquals("NCC-Huy", huy.getName());
        assertEquals(200, huy.getTotalQuantity());
    }

    @Test
    public void testGetImportHistory() {
        ImportInvoiceDAO8 dao = new ImportInvoiceDAO8();
        Date start = DateUtil8.parseDate("2026-05-01");
        Date end = DateUtil8.parseDate("2026-07-31");

        ImportInvoice8[] history = dao.getImportHistory(1, start, end);
        assertNotNull(history);
        assertEquals(3, history.length);

        // NH01: 40 + 20 + 40 = 100 sản phẩm, tổng tiền = 20000 + 6000 + 8000 = 34000
        ImportInvoice8 nh01 = null;
        for (ImportInvoice8 inv : history) {
            if (inv.getId() == 1) {
                nh01 = inv;
                break;
            }
        }
        assertNotNull(nh01);
        assertEquals(100, nh01.getTotalQuantity());
        assertEquals(34000.0, nh01.getTotalAmount(), 0.001);
    }

    @Test
    public void testGetImportInvoiceAndDetail() {
        ImportInvoiceDAO8 importDAO = new ImportInvoiceDAO8();
        ImportInvoice8 inv = importDAO.getImportInvoice(1);
        assertNotNull(inv);
        assertEquals(1, inv.getId());
        assertEquals("NCC-Khánh", inv.getSupplier().getName());
        assertEquals(100, inv.getTotalQuantity());
        assertEquals(34000.0, inv.getTotalAmount(), 0.001);

        ImportInvoiceDetailDAO8 detailDAO = new ImportInvoiceDetailDAO8();
        ImportInvoiceDetail8[] details = detailDAO.getImportInvoiceDetail(1);
        assertNotNull(details);
        assertEquals(3, details.length);
        assertEquals("Sữa Vinamilk", details[0].getProduct().getName());
        assertEquals(40, details[0].getQuantity());
        assertEquals(500.0, details[0].getUnitPrice(), 0.001);
        assertEquals(20000.0, details[0].getAmount(), 0.001);
    }

    @Test
    public void testDateRangeValidationInDateDAOs() {
        SupplierDAO8 supplierDAO = new SupplierDAO8();
        ImportInvoiceDAO8 importDAO = new ImportInvoiceDAO8();
        Date start = DateUtil8.parseDate("2026-08-01");
        Date end = DateUtil8.parseDate("2026-05-01");

        try {
            supplierDAO.getSupplierImportStatistics(start, end);
            fail("SupplierDAO8 phải từ chối khi startDate > endDate");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Ngày bắt đầu"));
        }

        try {
            supplierDAO.getSupplierImportStatistics(null, end);
            fail("SupplierDAO8 phải từ chối khi ngày null");
        } catch (IllegalArgumentException expected) {
            // hợp lệ
        }

        try {
            importDAO.getImportHistory(1, start, end);
            fail("ImportInvoiceDAO8 phải từ chối khi startDate > endDate");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Ngày bắt đầu"));
        }

        try {
            importDAO.getImportHistory(0, end, start);
            fail("ImportInvoiceDAO8 phải từ chối supplierId <= 0");
        } catch (IllegalArgumentException expected) {
            // hợp lệ
        }
    }

    @Test
    public void testDAO8LazyConnectionAndDatabaseFailureThrowsDataAccessException() throws Exception {
        AtomicInteger openedConnections = new AtomicInteger(0);
        DBUtil8.setTestConnectionSupplier(() -> {
            openedConnections.incrementAndGet();
            return DriverManager.getConnection(H2_URL, "sa", "");
        });

        try {
            InvoiceDAO8 dao = new InvoiceDAO8();
            assertEquals("Khởi tạo DAO8 không được mở sẵn kết nối gây rò rỉ", 0, openedConnections.get());
            Invoice8[] list = dao.getInvoiceList(1);
            assertEquals(3, list.length);
            assertEquals("Chỉ mở đúng 1 kết nối cho 1 truy vấn", 1, openedConnections.get());

            // Giả lập lỗi kết nối CSDL để kiểm chứng không nuốt lỗi thành mảng rỗng hay null
            DBUtil8.setTestConnectionSupplier(() -> {
                throw new SQLException("Simulated DB outage");
            });

            Date d1 = DateUtil8.parseDate("2026-05-01");
            Date d2 = DateUtil8.parseDate("2026-07-31");

            assertThrowsDataAccessException(() -> new MemberDAO8().checkLogin("khachhang1", "123456"));
            assertThrowsDataAccessException(() -> new InvoiceDAO8().getInvoiceList(1));
            assertThrowsDataAccessException(() -> new InvoiceDAO8().getInvoice(1, 1));
            assertThrowsDataAccessException(() -> new InvoiceDetailDAO8().getInvoiceDetail(1, 1));
            assertThrowsDataAccessException(() -> new SupplierDAO8().getSupplierImportStatistics(d1, d2));
            assertThrowsDataAccessException(() -> new ImportInvoiceDAO8().getImportHistory(1, d1, d2));
            assertThrowsDataAccessException(() -> new ImportInvoiceDAO8().getImportInvoice(1));
            assertThrowsDataAccessException(() -> new ImportInvoiceDetailDAO8().getImportInvoiceDetail(1));
        } finally {
            DBUtil8.setTestConnectionSupplier(() -> DriverManager.getConnection(H2_URL, "sa", ""));
        }
    }

    private void assertThrowsDataAccessException(Runnable action) {
        try {
            action.run();
            fail("DAO phải ném DataAccessException8 khi có SQLException thay vì trả về rỗng/null");
        } catch (DataAccessException8 ex) {
            assertTrue(ex.getCause() instanceof SQLException);
        }
    }
}
