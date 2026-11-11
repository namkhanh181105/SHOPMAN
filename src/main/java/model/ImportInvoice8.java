package model;

import java.io.Serializable;
import java.util.Date;

/**
 * Lớp thực thể ImportInvoice8 đại diện cho hóa đơn nhập hàng từ nhà cung cấp.
 */
public class ImportInvoice8 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private Date createdDate;
    private String status;
    private int totalQuantity;
    private double totalAmount;
    private Supplier8 supplier;
    private Manager8 manager;
    private ImportInvoiceDetail8[] details;

    public ImportInvoice8() {
    }

    public ImportInvoice8(int id, Date createdDate, String status, int totalQuantity, double totalAmount,
                          Supplier8 supplier, Manager8 manager, ImportInvoiceDetail8[] details) {
        this.id = id;
        this.createdDate = createdDate;
        this.status = status;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
        this.supplier = supplier;
        this.manager = manager;
        this.details = details;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalQuantity() {
        if (totalQuantity == 0 && details != null && details.length > 0) {
            int sum = 0;
            for (ImportInvoiceDetail8 d : details) {
                if (d != null) {
                    sum += d.getQuantity();
                }
            }
            return sum;
        }
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public double getTotalAmount() {
        if (totalAmount == 0.0 && details != null && details.length > 0) {
            double sum = 0.0;
            for (ImportInvoiceDetail8 d : details) {
                if (d != null) {
                    sum += d.getAmount();
                }
            }
            return sum;
        }
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Supplier8 getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier8 supplier) {
        this.supplier = supplier;
    }

    public Manager8 getManager() {
        return manager;
    }

    public void setManager(Manager8 manager) {
        this.manager = manager;
    }

    public ImportInvoiceDetail8[] getDetails() {
        return details;
    }

    public void setDetails(ImportInvoiceDetail8[] details) {
        this.details = details;
    }
}

