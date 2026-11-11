package dao;

import model.InvoiceDetail8;
import model.Product8;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lớp InvoiceDetailDAO8 xử lý truy xuất các dòng hàng chi tiết của một hóa đơn bán hàng.
 */
public class InvoiceDetailDAO8 extends DAO8 {
    private static final Logger LOGGER = Logger.getLogger(InvoiceDetailDAO8.class.getName());

    public InvoiceDetailDAO8() {
        super();
    }

    /**
     * Lấy danh sách các dòng chi tiết của hóa đơn, xác thực quyền sở hữu của khách hàng.
     * @param invoiceId mã hóa đơn
     * @param customerId mã khách hàng đăng nhập
     * @return mảng InvoiceDetail8[] (mảng rỗng nếu không có chi tiết hoặc không thuộc khách hàng)
     */
    public InvoiceDetail8[] getInvoiceDetail(int invoiceId, int customerId) {
        String sql = "SELECT d.id AS detailId, d.unitPrice, d.quantity, (d.unitPrice * d.quantity) AS amount, " +
                     "       p.id AS productId, p.name AS productName, p.image, p.type, p.unit, p.price, p.description " +
                     "FROM tblInvoiceDetail8 d " +
                     "JOIN tblInvoice8 i ON d.tblInvoice8id = i.id " +
                     "JOIN tblProduct8 p ON d.tblProduct8id = p.id " +
                     "WHERE i.id = ? AND i.tblCustomer8id = ? " +
                     "ORDER BY d.id ASC";

        List<InvoiceDetail8> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ps.setInt(2, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product8 product = new Product8();
                    product.setId(rs.getInt("productId"));
                    product.setName(rs.getString("productName"));
                    product.setImage(rs.getString("image"));
                    product.setType(rs.getString("type"));
                    product.setUnit(rs.getString("unit"));
                    product.setPrice(rs.getDouble("price"));
                    product.setDescription(rs.getString("description"));

                    InvoiceDetail8 detail = new InvoiceDetail8();
                    detail.setId(rs.getInt("detailId"));
                    detail.setUnitPrice(rs.getDouble("unitPrice"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setAmount(rs.getDouble("amount"));
                    detail.setProduct(product);

                    list.add(detail);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    "Lỗi truy vấn chi tiết hóa đơn invoiceId=" + invoiceId + ", customerId=" + customerId, e);
            throw new DataAccessException8("Không thể tải chi tiết hóa đơn từ cơ sở dữ liệu.", e);
        }
        return list.toArray(new InvoiceDetail8[0]);
    }
}

