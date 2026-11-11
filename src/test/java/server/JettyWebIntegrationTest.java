package server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.Configurations;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import util.DBUtil8;
import util.PasswordUtil8;

import java.io.File;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.SocketException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Kiểm thử tích hợp End-to-End chạy ứng dụng JSP/JSTL/Filter thật trên Jetty nhúng cổng ngẫu nhiên:
 * - Cổng ngẫu nhiên (0), không đụng cổng 8080 (XAMPP) hay 9090 (Jetty chính), tự dừng sau khi chạy xong.
 * - Đổi Session ID sau khi đăng nhập (chống Session Fixation).
 * - Phân quyền Customer8 / Manager8 / từ chối vai trò không hỗ trợ.
 * - Trang SupplierImportStatisticsView8.jsp không gọi DAO khi mới mở lần đầu, giữ bộ lọc khi xem lịch sử nhập.
 * - Thoát HTML (XSS) trên JSP, xử lý lỗi CSDL thân thiện không lộ stack trace, và đăng xuất hủy session.
 */
public class JettyWebIntegrationTest {

    private static final String H2_URL =
            "jdbc:h2:mem:testshopman_web;MODE=MySQL;DATABASE_TO_UPPER=FALSE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1";
    private static Connection masterConnection;
    private static Server server;
    private static int port;
    private static Path tempDir;
    private static Throwable startupSkipCause;
    private static final AtomicInteger QUERY_CONNECTIONS = new AtomicInteger(0);

    @BeforeClass
    public static void startServerAndDatabase() throws Exception {
        masterConnection = DriverManager.getConnection(H2_URL, "sa", "");
        seedDatabase();
        resetCountingSupplier();

        tempDir = Files.createTempDirectory("shopman-jetty-integration");
        server = new Server(0);
        Configurations.setServerDefault(server);

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/ShopMan");
        webapp.setResourceBase("src/main/webapp");
        webapp.setTempDirectory(tempDir.toFile());
        webapp.setParentLoaderPriority(true);
        webapp.setThrowUnavailableOnStartupException(true);
        webapp.setAttribute(
                "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                ".*/.*taglibs[^/]*\\.jar$|.*/.*jsp-[^/]*\\.jar$|.*/.*jstl[^/]*\\.jar$|.*/classes/.*");

        server.setHandler(webapp);
        try {
            server.start();
            port = ((ServerConnector) server.getConnectors()[0]).getLocalPort();
            assertTrue(port > 0);
            assertNotEquals("Không được chiếm cổng 8080", 8080, port);
            assertNotEquals("Không được chiếm cổng 9090", 9090, port);
        } catch (IOException e) {
            if ((e.getMessage() != null && e.getMessage().contains("loopback"))
                    || (e.getCause() instanceof SocketException)) {
                startupSkipCause = e;
            } else {
                throw e;
            }
        }
    }

    @AfterClass
    public static void stopServerAndCleanUp() throws Exception {
        DBUtil8.resetConnectionSupplier();
        if (server != null) {
            server.stop();
            assertTrue("Máy chủ Jetty nhúng phải dừng hoàn toàn sau khi kiểm thử", server.isStopped());
        }
        if (masterConnection != null && !masterConnection.isClosed()) {
            masterConnection.close();
        }
        if (tempDir != null && Files.exists(tempDir)) {
            Files.walk(tempDir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @Before
    public void ensureServerAvailable() {
        Assume.assumeNoException("Môi trường sandbox không cho phép mở socket nội bộ", startupSkipCause);
        resetCountingSupplier();
        QUERY_CONNECTIONS.set(0);
    }

    private static void resetCountingSupplier() {
        DBUtil8.setTestConnectionSupplier(() -> {
            QUERY_CONNECTIONS.incrementAndGet();
            return DriverManager.getConnection(H2_URL, "sa", "");
        });
    }

    private static void seedDatabase() throws SQLException {
        try (Statement stmt = masterConnection.createStatement()) {
            stmt.execute("CREATE TABLE tblMember8 (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "username VARCHAR(255) NOT NULL UNIQUE, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "email VARCHAR(255), " +
                    "phoneNumber VARCHAR(20))");
            stmt.execute("CREATE TABLE tblCustomer8 (" +
                    "tblMember8id INT PRIMARY KEY, address VARCHAR(255), " +
                    "FOREIGN KEY (tblMember8id) REFERENCES tblMember8(id) ON DELETE CASCADE)");
            stmt.execute("CREATE TABLE tblEmployee8 (" +
                    "tblMember8id INT PRIMARY KEY, position VARCHAR(255), " +
                    "FOREIGN KEY (tblMember8id) REFERENCES tblMember8(id) ON DELETE CASCADE)");
            stmt.execute("CREATE TABLE tblManager8 (tblEmployee8id INT PRIMARY KEY, " +
                    "FOREIGN KEY (tblEmployee8id) REFERENCES tblEmployee8(tblMember8id) ON DELETE CASCADE)");
            stmt.execute("CREATE TABLE tblSalesStaff8 (tblEmployee8id INT PRIMARY KEY, " +
                    "FOREIGN KEY (tblEmployee8id) REFERENCES tblEmployee8(tblMember8id) ON DELETE CASCADE)");
            stmt.execute("CREATE TABLE tblDeliveryStaff8 (tblEmployee8id INT PRIMARY KEY, " +
                    "FOREIGN KEY (tblEmployee8id) REFERENCES tblEmployee8(tblMember8id) ON DELETE CASCADE)");
            stmt.execute("CREATE TABLE tblSupplier8 (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) NOT NULL, " +
                    "phoneNumber VARCHAR(255), email VARCHAR(255), address VARCHAR(255))");
            stmt.execute("CREATE TABLE tblProduct8 (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) NOT NULL, " +
                    "type VARCHAR(255), image VARCHAR(255), unit VARCHAR(255), " +
                    "price DOUBLE NOT NULL DEFAULT 0.0, stockQuantity INT NOT NULL DEFAULT 0, description VARCHAR(255))");
            stmt.execute("CREATE TABLE tblInvoice8 (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, createdDate DATE NOT NULL, status VARCHAR(255) NOT NULL, " +
                    "tblCustomer8id INT NOT NULL, tblSalesStaff8id INT NULL, tblDeliveryStaff8id INT NULL, " +
                    "FOREIGN KEY (tblCustomer8id) REFERENCES tblCustomer8(tblMember8id))");
            stmt.execute("CREATE TABLE tblInvoiceDetail8 (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, unitPrice DOUBLE NOT NULL, quantity INT NOT NULL, " +
                    "tblProduct8id INT NOT NULL, tblInvoice8id INT NOT NULL, " +
                    "FOREIGN KEY (tblProduct8id) REFERENCES tblProduct8(id), " +
                    "FOREIGN KEY (tblInvoice8id) REFERENCES tblInvoice8(id))");
            stmt.execute("CREATE TABLE tblImportInvoice8 (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, createdDate DATE NOT NULL, status VARCHAR(255) NOT NULL, " +
                    "tblSupplier8id INT NOT NULL, tblManager8id INT NOT NULL, " +
                    "FOREIGN KEY (tblSupplier8id) REFERENCES tblSupplier8(id))");
            stmt.execute("CREATE TABLE tblImportInvoiceDetail8 (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, unitPrice DOUBLE NOT NULL, quantity INT NOT NULL, " +
                    "tblProduct8id INT NOT NULL, tblImportInvoice8id INT NOT NULL, " +
                    "FOREIGN KEY (tblProduct8id) REFERENCES tblProduct8(id), " +
                    "FOREIGN KEY (tblImportInvoice8id) REFERENCES tblImportInvoice8(id))");

            String pCust1 = PasswordUtil8.hashPassword("123456");
            String pMgr = PasswordUtil8.hashPassword("123456");
            String pSales = PasswordUtil8.hashPassword("123456");
            String pCust2 = PasswordUtil8.hashPassword("123456");

            stmt.execute("INSERT INTO tblMember8 (id, name, username, password, email, phoneNumber) VALUES " +
                    "(1, 'Nguyễn <script>alert(1)</script> An', 'khachhang1', '" + pCust1 + "', 'an@example.com', '0912345678'), " +
                    "(2, 'Trần Văn Bình', 'quanly1', '" + pMgr + "', 'binh@example.com', '0987654321'), " +
                    "(3, 'Lê Thị Cúc', 'banhang1', '" + pSales + "', 'cuc@example.com', '0901122334'), " +
                    "(4, 'Phạm Văn Dũng', 'giaohang1', '" + pSales + "', 'dung@example.com', '0933445566'), " +
                    "(5, 'Hoàng Minh Đức', 'khachhang2', '" + pCust2 + "', 'duc@example.com', '0944556677')");
            stmt.execute("INSERT INTO tblCustomer8 VALUES (1, 'Hà Nội'), (5, 'Hà Nội')");
            stmt.execute("INSERT INTO tblEmployee8 VALUES (2, 'Quản lý'), (3, 'Bán hàng'), (4, 'Giao hàng')");
            stmt.execute("INSERT INTO tblManager8 VALUES (2)");
            stmt.execute("INSERT INTO tblSalesStaff8 VALUES (3)");
            stmt.execute("INSERT INTO tblDeliveryStaff8 VALUES (4)");

            stmt.execute("INSERT INTO tblProduct8 (id, name, type, image, unit, price, stockQuantity, description) VALUES " +
                    "(1, 'Sữa Vinamilk', 'Đồ uống', 'milk.png', 'Hộp', 500.0, 100, 'Sữa tươi'), " +
                    "(2, 'Mì chính <b>Test</b>', 'Gia vị', 'mc.png', 'Gói', 300.0, 80, 'Gia vị')");

            stmt.execute("INSERT INTO tblInvoice8 (id, createdDate, status, tblCustomer8id, tblSalesStaff8id, tblDeliveryStaff8id) VALUES " +
                    "(1, '2026-07-22', 'Đã thanh toán', 1, 3, 4), " +
                    "(2, '2026-09-20', 'Đang giao', 1, 3, NULL), " +
                    "(3, '2026-06-10', 'Đã giao', 1, 3, 4), " +
                    "(4, '2026-09-25', 'Đang giao', 5, 3, NULL)");
            stmt.execute("INSERT INTO tblInvoiceDetail8 (id, unitPrice, quantity, tblProduct8id, tblInvoice8id) VALUES " +
                    "(1, 500.0, 2, 1, 1), " +
                    "(2, 300.0, 1, 2, 2), " +
                    "(3, 500.0, 1, 1, 3), " +
                    "(4, 300.0, 5, 2, 4)");

            stmt.execute("INSERT INTO tblSupplier8 (id, name, phoneNumber, email, address) VALUES " +
                    "(1, 'NCC <script>alert(\"xss\")</script> Khánh', '0912111222', 'khanh@example.com', 'Hà Nội')");
            stmt.execute("INSERT INTO tblImportInvoice8 (id, createdDate, status, tblSupplier8id, tblManager8id) VALUES " +
                    "(1, '2026-05-04', 'Đã thanh toán', 1, 2)");
            stmt.execute("INSERT INTO tblImportInvoiceDetail8 (id, unitPrice, quantity, tblProduct8id, tblImportInvoice8id) VALUES " +
                    "(1, 500.0, 40, 1, 1), " +
                    "(2, 300.0, 10, 2, 1)");
        }
    }

    private String baseUrl() {
        return "http://localhost:" + port + "/ShopMan";
    }

    private HttpClient newClient(CookieManager cm) {
        return HttpClient.newBuilder()
                .cookieHandler(cm)
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
    }

    private String sessionCookieId(CookieManager cm) {
        for (HttpCookie cookie : cm.getCookieStore().getCookies()) {
            if ("JSESSIONID".equalsIgnoreCase(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private HttpResponse<String> get(HttpClient client, String pathAndQuery) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + pathAndQuery))
                .GET()
                .build();
        return client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private HttpResponse<String> postForm(HttpClient client, String path, String username, String password) throws Exception {
        String form = "username=" + URLEncoder.encode(username, StandardCharsets.UTF_8)
                + "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + path))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(form, StandardCharsets.UTF_8))
                .build();
        return client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    @Test
    public void testManagerSessionRotationStatisticsFilterXssEscapingAndLogout() throws Exception {
        CookieManager cm = new CookieManager();
        cm.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        HttpClient client = newClient(cm);

        // 1. Lấy trang login để có JSESSIONID ban đầu
        HttpResponse<String> loginPage = get(client, "/member/LoginView8.jsp");
        assertEquals(200, loginPage.statusCode());
        String initialSessionId = sessionCookieId(cm);
        assertNotNull("Truy cập JSP đầu tiên phải sinh JSESSIONID", initialSessionId);

        // 2. Đăng nhập quản lý => phải đổi JSESSIONID
        HttpResponse<String> loginPost = postForm(client, "/member/LoginView8.jsp", "quanly1", "123456");
        assertEquals(302, loginPost.statusCode());
        assertTrue(loginPost.headers().firstValue("Location").orElse("").contains("/manager/ManagerMainMenu8.jsp"));
        String rotatedSessionId = sessionCookieId(cm);
        assertNotNull(rotatedSessionId);
        assertNotEquals("Session ID phải được đổi mới sau khi đăng nhập thành công", initialSessionId, rotatedSessionId);

        // 3. Mở trang SupplierImportStatisticsView8.jsp lần đầu: KHÔNG được gọi DAO
        QUERY_CONNECTIONS.set(0);
        HttpResponse<String> initialStats = get(client, "/manager/SupplierImportStatisticsView8.jsp");
        assertEquals(200, initialStats.statusCode());
        assertEquals("Mở trang thống kê lần đầu KHÔNG được gọi SupplierDAO8", 0, QUERY_CONNECTIONS.get());
        assertTrue(initialStats.body().contains("Chưa thực hiện truy vấn"));
        assertFalse(initialStats.body().contains("NCC01"));

        // 4. Gửi ngày sai định dạng / ngược ngày => KHÔNG gọi DAO
        HttpResponse<String> invalidDateRes = get(client, "/manager/SupplierImportStatisticsView8.jsp?submitted=true&startDate=2026-05-01XYZ&endDate=2026-07-31");
        assertEquals(200, invalidDateRes.statusCode());
        assertEquals(0, QUERY_CONNECTIONS.get());
        assertTrue(invalidDateRes.body().contains("Định dạng ngày không hợp lệ"));

        HttpResponse<String> reversedRes = get(client, "/manager/SupplierImportStatisticsView8.jsp?submitted=true&startDate=2026-08-01&endDate=2026-05-01");
        assertEquals(200, reversedRes.statusCode());
        assertEquals(0, QUERY_CONNECTIONS.get());
        assertTrue(reversedRes.body().contains("Ngày bắt đầu không được lớn hơn ngày kết thúc"));

        // 5. Gửi bộ lọc hợp lệ => Gọi DAO và thoát XSS trên tên NCC
        HttpResponse<String> validStats = get(client, "/manager/SupplierImportStatisticsView8.jsp?submitted=true&startDate=2026-05-01&endDate=2026-07-31");
        assertEquals(200, validStats.statusCode());
        assertTrue("Phải truy vấn DAO sau khi người dùng nhấn Xem thống kê", QUERY_CONNECTIONS.get() > 0);
        assertTrue(validStats.body().contains("NCC01"));
        assertFalse("Không được xuất thẻ <script> thô từ CSDL", validStats.body().contains("<script>alert"));
        assertTrue("Nội dung HTML nguy hiểm phải được escape", validStats.body().contains("&lt;script&gt;"));

        // 6. Xem lịch sử nhập hàng + giữ khoảng ngày khi Quay lại + chặn URL sửa tay ngày ngược
        HttpResponse<String> historyRes = get(client, "/manager/ImportHistoryView8.jsp?supplierId=1&startDate=2026-05-01&endDate=2026-07-31");
        assertEquals(200, historyRes.statusCode());
        assertTrue(historyRes.body().contains("NH01"));
        assertTrue(historyRes.body().contains("submitted=true&amp;startDate=2026-05-01&amp;endDate=2026-07-31"));
        assertFalse(historyRes.body().contains("<script>alert"));

        HttpResponse<String> tamperedHistory = get(client, "/manager/ImportHistoryView8.jsp?supplierId=1&startDate=2026-08-01&endDate=2026-05-01");
        assertEquals(200, tamperedHistory.statusCode());
        assertTrue(tamperedHistory.body().contains("Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc"));

        // Chặn XSS qua tham số URL trên ImportHistoryView8.jsp & ImportInvoiceDetailView8.jsp
        HttpResponse<String> xssParamRes = get(client,
                "/manager/ImportInvoiceDetailView8.jsp?importInvoiceId=1&supplierId=1&startDate="
                        + URLEncoder.encode("<script>alert(9)</script>", StandardCharsets.UTF_8) + "&endDate=2026-07-31");
        assertEquals(200, xssParamRes.statusCode());
        assertFalse(xssParamRes.body().contains("<script>alert(9)</script>"));
        assertFalse(xssParamRes.body().contains("<b>Test</b>"));
        assertTrue(xssParamRes.body().contains("&lt;b&gt;Test&lt;/b&gt;"));

        // 7. Đăng xuất phải hủy session
        HttpResponse<String> logoutRes = get(client, "/member/Logout8.jsp");
        assertEquals(302, logoutRes.statusCode());
        assertTrue(logoutRes.headers().firstValue("Location").orElse("").contains("logout=true"));

        HttpResponse<String> afterLogout = get(client, "/manager/ManagerMainMenu8.jsp");
        assertEquals(302, afterLogout.statusCode());
        assertTrue(afterLogout.headers().firstValue("Location").orElse("").contains("login_required"));
    }

    @Test
    public void testUnsupportedRoleDoesNotAuthenticateAndCustomerFlowWithDbOutageHandling() throws Exception {
        CookieManager cm = new CookieManager();
        cm.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        HttpClient client = newClient(cm);

        // 1. Tài khoản bán hàng không được phép thiết lập trạng thái đăng nhập hợp lệ
        HttpResponse<String> salesLogin = postForm(client, "/member/LoginView8.jsp", "banhang1", "123456");
        assertEquals(200, salesLogin.statusCode());
        assertTrue(salesLogin.body().contains("Tài khoản không thuộc vai trò Khách hàng hoặc Quản lý"));

        HttpResponse<String> deniedMgr = get(client, "/manager/ManagerMainMenu8.jsp");
        assertEquals(302, deniedMgr.statusCode());
        assertTrue(deniedMgr.headers().firstValue("Location").orElse("").contains("login_required"));

        // 2. Đăng nhập khách hàng thành công
        HttpResponse<String> custLogin = postForm(client, "/member/LoginView8.jsp", "khachhang1", "123456");
        assertEquals(302, custLogin.statusCode());
        assertTrue(custLogin.headers().firstValue("Location").orElse("").contains("/customer/CustomerMainMenu8.jsp"));

        // Không được vào khu vực quản lý
        HttpResponse<String> crossRole = get(client, "/manager/ManagerMainMenu8.jsp");
        assertEquals(302, crossRole.statusCode());
        assertTrue(crossRole.headers().firstValue("Location").orElse("").contains("manager_access_denied"));

        // Trang khách hàng escape HTML tên khách hàng
        HttpResponse<String> custMenu = get(client, "/customer/CustomerMainMenu8.jsp");
        assertEquals(200, custMenu.statusCode());
        assertFalse(custMenu.body().contains("<script>alert(1)</script>"));
        assertTrue(custMenu.body().contains("&lt;script&gt;alert(1)&lt;/script&gt;"));

        // Theo dõi hóa đơn: chỉ thấy HD01 (Đã thanh toán) và HD02 (Đang giao), không thấy HD03 (Đã giao) hay HD04 (khách khác)
        HttpResponse<String> invoiceListPage = get(client, "/customer/InvoiceTrackingMenu8.jsp");
        assertEquals(200, invoiceListPage.statusCode());
        assertTrue(invoiceListPage.body().contains("HD01"));
        assertTrue(invoiceListPage.body().contains("HD02"));
        assertFalse(invoiceListPage.body().contains("HD03"));
        assertFalse(invoiceListPage.body().contains("HD04"));

        // Chi tiết HD01 nạp SalesStaff8 + DeliveryStaff8, HD02 có SalesStaff8 và DeliveryStaff8 "Chưa phân công"
        HttpResponse<String> hd01Page = get(client, "/customer/InvoiceDetailView8.jsp?invoiceId=1");
        assertEquals(200, hd01Page.statusCode());
        assertTrue(hd01Page.body().contains("Lê Thị Cúc"));
        assertTrue(hd01Page.body().contains("Phạm Văn Dũng"));

        HttpResponse<String> hd02Page = get(client, "/customer/InvoiceDetailView8.jsp?invoiceId=2");
        assertEquals(200, hd02Page.statusCode());
        assertTrue(hd02Page.body().contains("Lê Thị Cúc"));
        assertTrue(hd02Page.body().contains("Chưa phân công"));

        // 3. Giả lập lỗi kết nối CSDL: JSP hiển thị thông báo thân thiện, không lộ SQL hoặc stack trace
        DBUtil8.setTestConnectionSupplier(() -> {
            throw new SQLException("SELECT secret_password FROM tblMember8 WHERE url='jdbc:mysql://localhost:3306'");
        });
        try {
            HttpResponse<String> outagePage = get(client, "/customer/InvoiceTrackingMenu8.jsp");
            assertEquals(200, outagePage.statusCode());
            assertTrue(outagePage.body().contains("Hệ thống đang gặp sự cố khi tải danh sách hóa đơn"));
            assertFalse(outagePage.body().contains("SQLException"));
            assertFalse(outagePage.body().contains("jdbc:mysql"));
            assertFalse(outagePage.body().contains("secret_password"));
        } finally {
            resetCountingSupplier();
        }
    }
}
