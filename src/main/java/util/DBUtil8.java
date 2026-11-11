package util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Tiện ích quản lý kết nối cơ sở dữ liệu JDBC dùng chung cho các lớp DAO8.
 */
public class DBUtil8 {
    private static final String DEFAULT_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/shopman?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DEFAULT_USERNAME = "root";
    private static final String DEFAULT_PASSWORD = "";

    private static String driver = DEFAULT_DRIVER;
    private static String url = DEFAULT_URL;
    private static String username = DEFAULT_USERNAME;
    private static String password = DEFAULT_PASSWORD;

    // Dùng để tiêm kết nối kiểm thử (ví dụ: H2 Database in-memory trong Unit Test)
    private static ConnectionSupplier testConnectionSupplier = null;

    @FunctionalInterface
    public interface ConnectionSupplier {
        Connection getConnection() throws SQLException;
    }

    static {
        loadProperties();
    }

    /**
     * Nạp cấu hình kết nối DB: ưu tiên db.properties cục bộ, dự phòng db.properties.example rồi mới tới mặc định.
     */
    public static synchronized void loadProperties() {
        driver = DEFAULT_DRIVER;
        url = DEFAULT_URL;
        username = DEFAULT_USERNAME;
        password = DEFAULT_PASSWORD;

        ClassLoader classLoader = DBUtil8.class.getClassLoader();
        String activeResource = "db.properties";
        try {
            if (!loadFromResource(classLoader, "db.properties")) {
                activeResource = "db.properties.example";
                if (loadFromResource(classLoader, "db.properties.example")) {
                    System.err.println("[DBUtil8] Chưa tìm thấy db.properties, đang tạm dùng cấu hình mẫu db.properties.example.");
                } else {
                    System.err.println("[DBUtil8] Không tìm thấy db.properties hoặc db.properties.example, dùng cấu hình mặc định.");
                }
            }
        } catch (Exception e) {
            System.err.println("[DBUtil8] Không thể nạp " + activeResource + ", sử dụng cấu hình mặc định: " + e.getMessage());
        }

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            System.err.println("[DBUtil8] Không tìm thấy JDBC Driver: " + driver);
        }
    }

    private static boolean loadFromResource(ClassLoader classLoader, String resourceName) throws IOException {
        try (InputStream is = classLoader.getResourceAsStream(resourceName)) {
            if (is == null) {
                return false;
            }
            Properties props = new Properties();
            try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                props.load(reader);
            }
            driver = props.getProperty("db.driver", driver).trim();
            url = props.getProperty("db.url", url).trim();
            username = props.getProperty("db.username", username).trim();
            password = props.getProperty("db.password", password);
            return true;
        }
    }

    /**
     * Lấy kết nối mới tới cơ sở dữ liệu.
     */
    public static Connection getConnection() throws SQLException {
        if (testConnectionSupplier != null) {
            return testConnectionSupplier.getConnection();
        }
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Thiết lập kết nối dùng riêng cho kiểm thử tự động.
     */
    public static synchronized void setTestConnectionSupplier(ConnectionSupplier supplier) {
        testConnectionSupplier = supplier;
    }

    public static synchronized void resetConnectionSupplier() {
        testConnectionSupplier = null;
        loadProperties();
    }

    public static String getUrl() {
        return url;
    }
}

