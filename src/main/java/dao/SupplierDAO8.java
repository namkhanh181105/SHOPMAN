package dao;

import model.SupplierImportStatistics8;
import util.DateUtil8;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lớp SupplierDAO8 xử lý truy vấn dữ liệu nhà cung cấp và thống kê lượng hàng nhập.
 */
public class SupplierDAO8 extends DAO8 {
    private static final Logger LOGGER = Logger.getLogger(SupplierDAO8.class.getName());

    public SupplierDAO8() {
        super();
    }

    /**
     * Lấy danh sách thống kê nhà cung cấp theo tổng lượng hàng nhập trong khoảng thời gian.
     * @param startDate ngày bắt đầu thống kê (bao gồm ngày này)
     * @param endDate ngày kết thúc thống kê (bao gồm ngày này)
     * @return mảng SupplierImportStatistics8[] sắp xếp theo tổng lượng nhập giảm dần
     */
    public SupplierImportStatistics8[] getSupplierImportStatistics(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Ngày bắt đầu và ngày kết thúc không được để trống.");
        }

        java.sql.Date sqlStartDate = java.sql.Date.valueOf(DateUtil8.toIsoString(startDate));
        java.sql.Date sqlEndDate = java.sql.Date.valueOf(DateUtil8.toIsoString(endDate));
        if (sqlStartDate.after(sqlEndDate)) {
            throw new IllegalArgumentException("Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc.");
        }

        // JOIN 1:N:N từ nhà cung cấp -> hóa đơn nhập -> chi tiết nhập nên SUM(quantity) không bị nhân bản
        String sql = "SELECT s.id, s.name, s.address, s.phoneNumber, s.email, " +
                     "       SUM(d.quantity) AS totalQuantity, " +
                     "       MAX(ii.createdDate) AS lastImportDate " +
                     "FROM tblSupplier8 s " +
                     "JOIN tblImportInvoice8 ii ON s.id = ii.tblSupplier8id " +
                     "JOIN tblImportInvoiceDetail8 d ON ii.id = d.tblImportInvoice8id " +
                     "WHERE ii.createdDate >= ? AND ii.createdDate <= ? " +
                     "GROUP BY s.id, s.name, s.address, s.phoneNumber, s.email " +
                     "ORDER BY totalQuantity DESC, s.id ASC";

        List<SupplierImportStatistics8> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, sqlStartDate);
            ps.setDate(2, sqlEndDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SupplierImportStatistics8 stat = new SupplierImportStatistics8();
                    stat.setId(rs.getInt("id"));
                    stat.setName(rs.getString("name"));
                    stat.setAddress(rs.getString("address"));
                    stat.setPhoneNumber(rs.getString("phoneNumber"));
                    stat.setEmail(rs.getString("email"));
                    stat.setTotalQuantity(rs.getInt("totalQuantity"));
                    stat.setLastImportDate(rs.getDate("lastImportDate"));
                    list.add(stat);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    "Lỗi truy vấn thống kê nhà cung cấp từ " + sqlStartDate + " đến " + sqlEndDate, e);
            throw new DataAccessException8("Không thể truy vấn thống kê nhà cung cấp từ cơ sở dữ liệu.", e);
        }
        return list.toArray(new SupplierImportStatistics8[0]);
    }
}

