package model;

import java.io.Serializable;
import java.util.Date;

/**
 * Lớp thực thể Invoice8 đại diện cho hóa đơn bán hàng trong hệ thống ShopMan.
 */
public class Invoice8 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private Date createdDate;
    private String status;
    private double totalAmount;
    private Customer8 customer;
    private SalesStaff8 salesStaff;
    private DeliveryStaff8 deliveryStaff;
    private InvoiceDetail8[] details;

    public Invoice8() {
    }

    public Invoice8(int id, Date createdDate, String status, double totalAmount,
                    Customer8 customer, SalesStaff8 salesStaff, DeliveryStaff8 deliveryStaff,
                    InvoiceDetail8[] details) {
        this.id = id;
        this.createdDate = createdDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.customer = customer;
        this.salesStaff = salesStaff;
        this.deliveryStaff = deliveryStaff;
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

    public double getTotalAmount() {
        if (totalAmount == 0.0 && details != null && details.length > 0) {
            double sum = 0.0;
            for (InvoiceDetail8 d : details) {
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

    public Customer8 getCustomer() {
        return customer;
    }

    public void setCustomer(Customer8 customer) {
        this.customer = customer;
    }

    public SalesStaff8 getSalesStaff() {
        return salesStaff;
    }

    public void setSalesStaff(SalesStaff8 salesStaff) {
        this.salesStaff = salesStaff;
    }

    public DeliveryStaff8 getDeliveryStaff() {
        return deliveryStaff;
    }

    public void setDeliveryStaff(DeliveryStaff8 deliveryStaff) {
        this.deliveryStaff = deliveryStaff;
    }

    public InvoiceDetail8[] getDetails() {
        return details;
    }

    public void setDetails(InvoiceDetail8[] details) {
        this.details = details;
    }
}

