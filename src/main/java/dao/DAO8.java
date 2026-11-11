package dao;

import util.DBUtil8;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Lớp DAO8 cơ sở cung cấp kết nối JDBC cho các lớp DAO chuyên biệt.
 * Kết nối được mở theo từng thao tác trong khối try-with-resources để tránh rò rỉ kết nối.
 */
public class DAO8 {
    protected Connection con;

    public DAO8() {
    }

    /**
     * Mở kết nối JDBC cho từng thao tác truy xuất dữ liệu.
     */
    protected Connection getConnection() throws SQLException {
        return DBUtil8.getConnection();
    }

    public void closeConnection() {
        if (this.con != null) {
            try {
                if (!this.con.isClosed()) {
                    this.con.close();
                }
            } catch (SQLException ignored) {
            } finally {
                this.con = null;
            }
        }
    }
}

