package dao;

import model.ImportInvoice8;
import model.Manager8;
import model.Supplier8;
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
 * Lớp ImportInvoiceDAO8 xử lý truy vấn lịch sử nhập hàng và thông tin hóa đơn nhập.
 */
public class ImportInvoiceDAO8 extends DAO8 {
    private static final Logger LOGGER = Logger.getLogger(ImportInvoiceDAO8.class.getName());

    public ImportInvoiceDAO8() {
        super();
    }

    /**
     * Lấy danh sách các hóa đơn nhập của nhà cung cấp trong khoảng thời gian xác định.
     * @param supplierId mã nhà cung cấp
     * @param startDate ngày bắt đầu
     * @param endDate ngày kết thúc
     * @return mảng ImportInvoice8[]
     */
    public ImportInvoice8[] getImportHistory(int supplierId, Date startDate, Date endDate) {
        if (supplierId <= 0) {
            throw new IllegalArgumentException("Mã nhà cung cấp không hợp lệ.");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Ngày bắt đầu và ngày kết thúc không được để trống.");
        }

        java.sql.Date sqlStartDate = java.sql.Date.valueOf(DateUtil8.toIsoString(startDate));
        java.sql.Date sqlEndDate = java.sql.Date.valueOf(DateUtil8.toIsoString(endDate));
        if (sqlStartDate.after(sqlEndDate)) {
            throw new IllegalArgumentException("Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc.");
        }

        String sql = "SELECT ii.id, ii.createdDate, ii.status, " +
                     "       COALESCE(SUM(d.quantity), 0) AS totalQuantity, " +
                     "       COALESCE(SUM(d.unitPrice * d.quantity), 0.0) AS totalAmount, " +
                     "       s.id AS supplierId, s.name AS supplierName, s.address, s.phoneNumber, s.email " +
                     "FROM tblImportInvoice8 ii " +
                     "JOIN tblSupplier8 s ON ii.tblSupplier8id = s.id " +
                     "LEFT JOIN tblImportInvoiceDetail8 d ON ii.id = d.tblImportInvoice8id " +
                     "WHERE ii.tblSupplier8id = ? AND ii.createdDate >= ? AND ii.createdDate <= ? " +
                     "GROUP BY ii.id, ii.createdDate, ii.status, s.id, s.name, s.address, s.phoneNumber, s.email " +
                     "ORDER BY ii.createdDate DESC, ii.id DESC";

        List<ImportInvoice8> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, supplierId);
            ps.setDate(2, sqlStartDate);
            ps.setDate(3, sqlEndDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Supplier8 supp = new Supplier8();
                    supp.setId(rs.getInt("supplierId"));
                    supp.setName(rs.getString("supplierName"));
                    supp.setAddress(rs.getString("address"));
                    supp.setPhoneNumber(rs.getString("phoneNumber"));
                    supp.setEmail(rs.getString("email"));

                    ImportInvoice8 inv = new ImportInvoice8();
                    inv.setId(rs.getInt("id"));
                    inv.setCreatedDate(rs.getDate("createdDate"));
                    inv.setStatus(rs.getString("status"));
                    inv.setTotalQuantity(rs.getInt("totalQuantity"));
                    inv.setTotalAmount(rs.getDouble("totalAmount"));
                    inv.setSupplier(supp);

                    list.add(inv);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    "Lỗi truy vấn lịch sử nhập hàng supplierId=" + supplierId + ", từ " + sqlStartDate + " đến " + sqlEndDate, e);
            throw new DataAccessException8("Không thể tải lịch sử nhập hàng từ cơ sở dữ liệu.", e);
        }
        return list.toArray(new ImportInvoice8[0]);
    }

    /**
     * Lấy thông tin chi tiết một hóa đơn nhập theo mã hóa đơn.
     * @param importInvoiceId mã hóa đơn nhập
     * @return đối tượng ImportInvoice8 hoặc null nếu không tồn tại
     */
    public ImportInvoice8 getImportInvoice(int importInvoiceId) {
        String sql = "SELECT ii.id, ii.createdDate, ii.status, " +
                     "       COALESCE(agg.totalQuantity, 0) AS totalQuantity, " +
                     "       COALESCE(agg.totalAmount, 0.0) AS totalAmount, " +
                     "       s.id AS supplierId, s.name AS supplierName, s.address AS supplierAddress, s.phoneNumber AS supplierPhone, s.email AS supplierEmail, " +
                     "       m.id AS managerId, m.name AS managerName " +
                     "FROM tblImportInvoice8 ii " +
                     "JOIN tblSupplier8 s ON ii.tblSupplier8id = s.id " +
                     "LEFT JOIN tblManager8 mgr ON ii.tblManager8id = mgr.tblEmployee8id " +
                     "LEFT JOIN tblEmployee8 emp ON mgr.tblEmployee8id = emp.tblMember8id " +
                     "LEFT JOIN tblMember8 m ON emp.tblMember8id = m.id " +
                     "LEFT JOIN (" +
                     "    SELECT tblImportInvoice8id, SUM(quantity) AS totalQuantity, SUM(unitPrice * quantity) AS totalAmount " +
                     "    FROM tblImportInvoiceDetail8 GROUP BY tblImportInvoice8id" +
                     ") agg ON ii.id = agg.tblImportInvoice8id " +
                     "WHERE ii.id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, importInvoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Supplier8 supp = new Supplier8();
                    supp.setId(rs.getInt("supplierId"));
                    supp.setName(rs.getString("supplierName"));
                    supp.setAddress(rs.getString("supplierAddress"));
                    supp.setPhoneNumber(rs.getString("supplierPhone"));
                    supp.setEmail(rs.getString("supplierEmail"));

                    Manager8 manager = null;
                    int mgrId = rs.getInt("managerId");
                    if (!rs.wasNull() && mgrId > 0) {
                        manager = new Manager8();
                        manager.setId(mgrId);
                        manager.setName(rs.getString("managerName"));
                    }

                    ImportInvoice8 inv = new ImportInvoice8();
                    inv.setId(rs.getInt("id"));
                    inv.setCreatedDate(rs.getDate("createdDate"));
                    inv.setStatus(rs.getString("status"));
                    inv.setTotalQuantity(rs.getInt("totalQuantity"));
                    inv.setTotalAmount(rs.getDouble("totalAmount"));
                    inv.setSupplier(supp);
                    inv.setManager(manager);

                    return inv;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi truy vấn hóa đơn nhập id=" + importInvoiceId, e);
            throw new DataAccessException8("Không thể tải thông tin hóa đơn nhập từ cơ sở dữ liệu.", e);
        }
        return null;
    }
}

