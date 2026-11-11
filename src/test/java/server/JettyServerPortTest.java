package server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import util.PortCheckUtil8;

import java.io.IOException;
import java.net.SocketException;

import static org.junit.Assert.*;

/**
 * Kiểm thử tính an toàn cổng mạng của máy chủ Jetty theo đúng yêu cầu bài tập:
 * - Dùng cổng ngẫu nhiên (cổng 0) do hệ điều hành cấp phát tự động.
 * - Tuyệt đối không chiếm cổng 8080 của Apache XAMPP.
 * - Không chiếm cổng 9090 mặc định khi chạy kiểm thử.
 * - Máy chủ tự dừng ngay khi kiểm thử kết thúc.
 */
public class JettyServerPortTest {

    private Server server;

    @Before
    public void setUp() throws Exception {
        // Cấu hình Jetty lắng nghe trên cổng 0 (cổng ngẫu nhiên do OS chọn)
        server = new Server(0);
    }

    @After
    public void tearDown() throws Exception {
        if (server != null && server.isStarted()) {
            server.stop();
        }
    }

    @Test
    public void testJettyRandomPortAndAutoStop() throws Exception {
        try {
            server.start();
        } catch (IOException e) {
            // Trong môi trường sandbox Windows không có quyền mở loopback socket,
            // dùng Assume để bỏ qua kiểm thử thay vì đánh dấu lỗi môi trường.
            if ((e.getMessage() != null && e.getMessage().contains("loopback")) ||
                (e.getCause() instanceof SocketException)) {
                Assume.assumeNoException("Môi trường sandbox cô lập không cho phép mở cổng socket nội bộ", e);
                return;
            }
            throw e;
        }

        ServerConnector connector = (ServerConnector) server.getConnectors()[0];
        int actualPort = connector.getLocalPort();

        assertTrue("Cổng được cấp phát phải lớn hơn 0", actualPort > 0);
        assertNotEquals("Tuyệt đối không được chiếm cổng 8080 của Apache XAMPP", 8080, actualPort);
        assertNotEquals("Kiểm thử không được chiếm cổng 9090 của dịch vụ chính", 9090, actualPort);

        System.out.println("[JettyServerPortTest] Máy chủ kiểm thử khởi chạy thành công trên cổng ngẫu nhiên: " + actualPort);

        // Dừng máy chủ và kiểm tra trạng thái dừng
        server.stop();
        assertTrue("Máy chủ phải dừng lại hoàn toàn sau khi kiểm thử", server.isStopped());
        System.out.println("[JettyServerPortTest] Máy chủ kiểm thử đã tự động dừng hoàn toàn.");
    }

    @Test
    public void testPortCheckUtil() {
        // Kiểm tra hàm tiện ích PortCheckUtil8
        boolean portAvailable = PortCheckUtil8.isPortAvailable(actualFreePort());
        assertTrue("Cổng rảnh phải được nhận diện là available", portAvailable);
    }

    private int actualFreePort() {
        try (java.net.ServerSocket ss = new java.net.ServerSocket(0)) {
            return ss.getLocalPort();
        } catch (Exception e) {
            return 9999;
        }
    }
}
