package dao;

import model.Customer8;
import model.DeliveryStaff8;
import model.Employee8;
import model.Manager8;
import model.Member8;
import model.SalesStaff8;
import util.PasswordUtil8;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lớp MemberDAO8 xử lý xác thực đăng nhập và nạp thông tin thành viên theo vai trò.
 */
public class MemberDAO8 extends DAO8 {
    private static final Logger LOGGER = Logger.getLogger(MemberDAO8.class.getName());

    public MemberDAO8() {
        super();
    }

    /**
     * Xác thực thông tin đăng nhập của thành viên.
     * Đối tượng trả về không chứa mật khẩu/chuỗi băm để bảo đảm an toàn khi lưu trong session.
     * @param username tên đăng nhập
     * @param password mật khẩu người dùng nhập (không trim)
     * @return đối tượng Member8 theo vai trò nếu hợp lệ, ngược lại null
     */
    public Member8 checkLogin(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        String cleanUsername = username.trim();
        if (cleanUsername.isEmpty() || password.isEmpty()) {
            return null;
        }

        String sql = "SELECT m.id, m.name, m.username, m.password, m.email, m.phoneNumber, " +
                     "       c.tblMember8id AS customerId, c.address, " +
                     "       e.tblMember8id AS employeeId, e.position, " +
                     "       mgr.tblEmployee8id AS managerId, " +
                     "       ss.tblEmployee8id AS salesStaffId, " +
                     "       ds.tblEmployee8id AS deliveryStaffId " +
                     "FROM tblMember8 m " +
                     "LEFT JOIN tblCustomer8 c ON m.id = c.tblMember8id " +
                     "LEFT JOIN tblEmployee8 e ON m.id = e.tblMember8id " +
                     "LEFT JOIN tblManager8 mgr ON e.tblMember8id = mgr.tblEmployee8id " +
                     "LEFT JOIN tblSalesStaff8 ss ON e.tblMember8id = ss.tblEmployee8id " +
                     "LEFT JOIN tblDeliveryStaff8 ds ON e.tblMember8id = ds.tblEmployee8id " +
                     "WHERE m.username = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cleanUsername);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    if (!PasswordUtil8.checkPassword(password, storedPassword)) {
                        return null;
                    }

                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String user = rs.getString("username");
                    String email = rs.getString("email");
                    String phone = rs.getString("phoneNumber");

                    // Kiểm tra vai trò: Quản lý trước
                    int managerId = rs.getInt("managerId");
                    if (!rs.wasNull() && managerId > 0) {
                        String position = rs.getString("position");
                        return new Manager8(id, name, user, null, email, phone,
                                position != null ? position : "Quản lý");
                    }

                    // Nhân viên bán hàng
                    int salesStaffId = rs.getInt("salesStaffId");
                    if (!rs.wasNull() && salesStaffId > 0) {
                        String position = rs.getString("position");
                        return new SalesStaff8(id, name, user, null, email, phone,
                                position != null ? position : "Nhân viên bán hàng");
                    }

                    // Nhân viên giao hàng
                    int deliveryStaffId = rs.getInt("deliveryStaffId");
                    if (!rs.wasNull() && deliveryStaffId > 0) {
                        String position = rs.getString("position");
                        return new DeliveryStaff8(id, name, user, null, email, phone,
                                position != null ? position : "Nhân viên giao hàng");
                    }

                    // Khách hàng
                    int customerId = rs.getInt("customerId");
                    if (!rs.wasNull() && customerId > 0) {
                        String address = rs.getString("address");
                        return new Customer8(id, name, user, null, email, phone, address);
                    }

                    // Nhân viên chung khác (nếu có)
                    int employeeId = rs.getInt("employeeId");
                    if (!rs.wasNull() && employeeId > 0) {
                        String position = rs.getString("position");
                        return new Employee8(id, name, user, null, email, phone, position);
                    }

                    return new Member8(id, name, user, null, email, phone);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi truy vấn đăng nhập cho username=" + cleanUsername, e);
            throw new DataAccessException8("Không thể xác thực tài khoản do lỗi kết nối cơ sở dữ liệu.", e);
        }
        return null;
    }
}

