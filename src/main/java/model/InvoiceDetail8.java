package model;

import java.io.Serializable;

/**
 * Lớp thực thể InvoiceDetail8 đại diện cho một dòng chi tiết hóa đơn bán hàng.
 */
public class InvoiceDetail8 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private double unitPrice;
    private int quantity;
    private double amount;
    private Product8 product;

    public InvoiceDetail8() {
    }

    public InvoiceDetail8(int id, double unitPrice, int quantity, double amount, Product8 product) {
        this.id = id;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.amount = amount;
        this.product = product;
    }

    public InvoiceDetail8(int id, double unitPrice, int quantity, Product8 product) {
        this.id = id;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.amount = unitPrice * quantity;
        this.product = product;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        this.amount = this.unitPrice * this.quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.amount = this.unitPrice * this.quantity;
    }

    public double getAmount() {
        if (amount == 0.0 && unitPrice > 0 && quantity > 0) {
            return unitPrice * quantity;
        }
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Product8 getProduct() {
        return product;
    }

    public void setProduct(Product8 product) {
        this.product = product;
    }
}

