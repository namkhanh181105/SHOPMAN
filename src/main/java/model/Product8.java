package model;

import java.io.Serializable;

/**
 * Lớp thực thể Product8 đại diện cho sản phẩm/hàng hóa trong hệ thống ShopMan.
 */
public class Product8 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String image;
    private String type;
    private String unit;
    private double price;
    private int stockQuantity;
    private String description;

    public Product8() {
    }

    public Product8(int id, String name, String image, String type, String unit, double price, int stockQuantity, String description) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.type = type;
        this.unit = unit;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

