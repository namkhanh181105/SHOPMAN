package dao;

import model.Customer8;
import model.DeliveryStaff8;
import model.Invoice8;
import model.SalesStaff8;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lớp InvoiceDAO8 xử lý truy xuất dữ liệu hóa đơn bán hàng của khách hàng.
 */
public class InvoiceDAO8 extends DAO8 {
    private static final Logger LOGGER = Logger.getLogger(InvoiceDAO8.class.getName());

    public InvoiceDAO8() {
        super();
    }

    /**
     * Lấy danh sách các hóa đơn đang đặt của khách hàng theo mã khách hàng.
     * Loại trừ các hóa đơn đã giao/hoàn tất hoặc đã hủy; giữ lại "Đã thanh toán", "Đang giao", "Chờ duyệt"...
     * @param customerId mã thành viên của khách hàng
     * @return mảng các hóa đơn Invoice8[]
     */
    public Invoice8[] getInvoiceList(int customerId) {
        String sql = "SELECT i.id, i.createdDate, i.status, " +
                     "       COALESCE(SUM(d.unitPrice * d.quantity), 0.0) AS totalAmount " +
                     "FROM tblInvoice8 i " +
                     "LEFT JOIN tblInvoiceDetail8 d ON i.id = d.tblInvoice8id " +
                     "WHERE i.tblCustomer8id = ? " +
                     "  AND LOWER(TRIM(COALESCE(i.status, ''))) NOT IN (?, ?, ?, ?, ?, ?) " +
                     "GROUP BY i.id, i.createdDate, i.status " +
                     "ORDER BY i.createdDate ASC, i.id ASC";

        List<Invoice8> list = new ArrayList<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setString(2, "đã giao");
            ps.setString(3, "đã giao hàng");
            ps.setString(4, "hoàn tất");
            ps.setString(5, "hoàn thành");
            ps.setString(6, "đã hủy");
            ps.setString(7, "đã huỷ");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Invoice8 inv = new Invoice8();
                    inv.setId(rs.getInt("id"));
                    inv.setCreatedDate(rs.getDate("createdDate"));
                    inv.setStatus(rs.getString("status"));
                    inv.setTotalAmount(rs.getDouble("totalAmount"));
                    list.add(inv);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi truy vấn danh sách hóa đơn đang đặt cho customerId=" + customerId, e);
            throw new DataAccessException8("Không thể tải danh sách hóa đơn từ cơ sở dữ liệu.", e);
        }
        return list.toArray(new Invoice8[0]);
    }

    /**
     * Lấy thông tin chi tiết một hóa đơn, kiểm tra bảo mật thuộc quyền sở hữu của khách hàng,
     * đồng thời nạp thông tin khách hàng và nhân viên bán hàng/giao hàng (nếu đã phân công).
     * @param invoiceId mã hóa đơn
     * @param customerId mã khách hàng đăng nhập
     * @return đối tượng Invoice8 hoặc null nếu không tồn tại / không đúng chủ sở hữu
     */
    public Invoice8 getInvoice(int invoiceId, int customerId) {
        String sql = "SELECT i.id, i.createdDate, i.status, " +
                     "       COALESCE(agg.totalAmount, 0.0) AS totalAmount, " +
                     "       cm.id AS customerId, cm.name AS customerName, cm.email AS customerEmail, " +
                     "       cm.phoneNumber AS customerPhone, c.address AS customerAddress, " +
                     "       ss.tblEmployee8id AS salesStaffId, se.position AS salesStaffPosition, " +
                     "       sm.name AS salesStaffName, sm.email AS salesStaffEmail, sm.phoneNumber AS salesStaffPhone, " +
                     "       ds.tblEmployee8id AS deliveryStaffId, de.position AS deliveryStaffPosition, " +
                     "       dm.name AS deliveryStaffName, dm.email AS deliveryStaffEmail, dm.phoneNumber AS deliveryStaffPhone " +
                     "FROM tblInvoice8 i " +
                     "JOIN tblCustomer8 c ON i.tblCustomer8id = c.tblMember8id " +
                     "JOIN tblMember8 cm ON c.tblMember8id = cm.id " +
                     "LEFT JOIN (" +
                     "    SELECT tblInvoice8id, SUM(unitPrice * quantity) AS totalAmount " +
                     "    FROM tblInvoiceDetail8 GROUP BY tblInvoice8id" +
                     ") agg ON i.id = agg.tblInvoice8id " +
                     "LEFT JOIN tblSalesStaff8 ss ON i.tblSalesStaff8id = ss.tblEmployee8id " +
                     "LEFT JOIN tblEmployee8 se ON ss.tblEmployee8id = se.tblMember8id " +
                     "LEFT JOIN tblMember8 sm ON se.tblMember8id = sm.id " +
                     "LEFT JOIN tblDeliveryStaff8 ds ON i.tblDeliveryStaff8id = ds.tblEmployee8id " +
                     "LEFT JOIN tblEmployee8 de ON ds.tblEmployee8id = de.tblMember8id " +
                     "LEFT JOIN tblMember8 dm ON de.tblMember8id = dm.id " +
                     "WHERE i.id = ? AND i.tblCustomer8id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ps.setInt(2, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Invoice8 inv = new Invoice8();
                    inv.setId(rs.getInt("id"));
                    inv.setCreatedDate(rs.getDate("createdDate"));
                    inv.setStatus(rs.getString("status"));
                    inv.setTotalAmount(rs.getDouble("totalAmount"));

                    Customer8 cust = new Customer8();
                    cust.setId(rs.getInt("customerId"));
                    cust.setName(rs.getString("customerName"));
                    cust.setEmail(rs.getString("customerEmail"));
                    cust.setPhoneNumber(rs.getString("customerPhone"));
                    cust.setAddress(rs.getString("customerAddress"));
                    inv.setCustomer(cust);

                    int salesStaffId = rs.getInt("salesStaffId");
                    if (!rs.wasNull() && salesStaffId > 0) {
                        SalesStaff8 salesStaff = new SalesStaff8();
                        salesStaff.setId(salesStaffId);
                        salesStaff.setName(rs.getString("salesStaffName"));
                        salesStaff.setEmail(rs.getString("salesStaffEmail"));
                        salesStaff.setPhoneNumber(rs.getString("salesStaffPhone"));
                        salesStaff.setPosition(rs.getString("salesStaffPosition"));
                        inv.setSalesStaff(salesStaff);
                    }

                    int deliveryStaffId = rs.getInt("deliveryStaffId");
                    if (!rs.wasNull() && deliveryStaffId > 0) {
                        DeliveryStaff8 deliveryStaff = new DeliveryStaff8();
                        deliveryStaff.setId(deliveryStaffId);
                        deliveryStaff.setName(rs.getString("deliveryStaffName"));
                        deliveryStaff.setEmail(rs.getString("deliveryStaffEmail"));
                        deliveryStaff.setPhoneNumber(rs.getString("deliveryStaffPhone"));
                        deliveryStaff.setPosition(rs.getString("deliveryStaffPosition"));
                        inv.setDeliveryStaff(deliveryStaff);
                    }

                    return inv;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi truy vấn chi tiết hóa đơn invoiceId=" + invoiceId + ", customerId=" + customerId, e);
            throw new DataAccessException8("Không thể tải thông tin hóa đơn từ cơ sở dữ liệu.", e);
        }
        return null;
    }
}

