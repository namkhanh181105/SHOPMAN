package util;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Tiện ích kiểm tra tính khả dụng của cổng mạng (Port Check).
 * Tuân thủ quy định môi trường: Không tự ý ngắt tiến trình đang chiếm cổng,
 * thông báo rõ ràng và hướng dẫn người dùng chọn cổng khác.
 */
public class PortCheckUtil8 {

    /**
     * Kiểm tra xem cổng TCP có đang được sử dụng hay không.
     * @param port số cổng TCP cần kiểm tra (ví dụ 9090)
     * @return true nếu cổng đang rảnh (có thể lắng nghe), false nếu đang bận
     */
    public static boolean isPortAvailable(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Kiểm tra cổng và in thông báo hướng dẫn ra console.
     */
    public static void checkPortStatus(int port) {
        if (isPortAvailable(port)) {
            System.out.println("[PortCheckUtil8] Cổng " + port + " đang khả dụng và sẵn sàng phục vụ Jetty.");
        } else {
            System.err.println("===============================================================================");
            System.err.println("[CẢNH BÁO MÔI TRƯỜNG] Cổng " + port + " hiện đang được sử dụng bởi một tiến trình khác!");
            System.err.println("- Hệ thống tuân thủ nguyên tắc an toàn: KHÔNG tự động tắt tiến trình đang chiếm cổng.");
            System.err.println("- Apache của XAMPP đang chạy trên cổng 8080 (giữ nguyên không can thiệp).");
            System.err.println("- Để khởi động Jetty trên cổng khác khả dụng (ví dụ 9091), hãy chạy lệnh:");
            System.err.println("    mvn -Djetty.http.port=9091 jetty:run");
            System.err.println("  hoặc với file wrapper:");
            System.err.println("    .\\mvnw.bat -Djetty.http.port=9091 jetty:run");
            System.err.println("===============================================================================");
        }
    }
}

