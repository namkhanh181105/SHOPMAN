package model;

import java.util.Date;

/**
 * Lớp thực thể SupplierImportStatistics8 kế thừa từ Supplier8,
 * bổ sung các thuộc tính phục vụ thống kê theo lượng hàng nhập.
 */
public class SupplierImportStatistics8 extends Supplier8 {
    private static final long serialVersionUID = 1L;

    private int totalQuantity;
    private Date lastImportDate;

    public SupplierImportStatistics8() {
        super();
    }

    public SupplierImportStatistics8(int id, String name, String address, String phoneNumber, String email,
                                    int totalQuantity, Date lastImportDate) {
        super(id, name, address, phoneNumber, email);
        this.totalQuantity = totalQuantity;
        this.lastImportDate = lastImportDate;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Date getLastImportDate() {
        return lastImportDate;
    }

    public void setLastImportDate(Date lastImportDate) {
        this.lastImportDate = lastImportDate;
    }

    // Alias cho tương thích phương thức
    public Date getLatestImportDate() {
        return lastImportDate;
    }

    public void setLatestImportDate(Date latestImportDate) {
        this.lastImportDate = latestImportDate;
    }
}

