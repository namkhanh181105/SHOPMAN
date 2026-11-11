package dao;

import model.ImportInvoiceDetail8;
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
 * Lớp ImportInvoiceDetailDAO8 xử lý truy vấn các mặt hàng chi tiết của một hóa đơn nhập hàng.
 */
public class ImportInvoiceDetailDAO8 extends DAO8 {
    private static final Logger LOGGER = Logger.getLogger(ImportInvoiceDetailDAO8.class.getName());

    public ImportInvoiceDetailDAO8() {
        super();
    }

    /**
     * Lấy danh sách các dòng chi tiết của một hóa đơn nhập hàng.
     * @param importInvoiceId mã hóa đơn nhập
     * @return mảng ImportInvoiceDetail8[] (mảng rỗng nếu không có dòng hàng nào)
     */
    public ImportInvoiceDetail8[] getImportInvoiceDetail(int importInvoiceId) {
        String sql = "SELECT d.id AS detailId, d.unitPrice, d.quantity, (d.unitPrice * d.quantity) AS amount, " +
                     "       p.id AS productId, p.name AS productName, p.image, p.type, p.unit, p.price, p.description " +
                     "FROM tblImportInvoiceDetail8 d " +
                     "JOIN tblProduct8 p ON d.tblProduct8id = p.id " +
                     "WHERE d.tblImportInvoice8id = ? " +
                     "ORDER BY d.id ASC";

        List<ImportInvoiceDetail8> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, importInvoiceId);
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

                    ImportInvoiceDetail8 detail = new ImportInvoiceDetail8();
                    detail.setId(rs.getInt("detailId"));
                    detail.setUnitPrice(rs.getDouble("unitPrice"));
                    detail.setQuantity(rs.getInt("quantity"));
                    detail.setAmount(rs.getDouble("amount"));
                    detail.setProduct(product);

                    list.add(detail);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi truy vấn chi tiết hóa đơn nhập id=" + importInvoiceId, e);
            throw new DataAccessException8("Không thể tải chi tiết hóa đơn nhập từ cơ sở dữ liệu.", e);
        }
        return list.toArray(new ImportInvoiceDetail8[0]);
    }
}

