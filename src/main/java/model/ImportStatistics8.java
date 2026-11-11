package model;

import java.io.Serializable;

/**
 * Lớp thực thể ImportStatistics8 theo sơ đồ lớp thực thể pha thiết kế.
 */
public class ImportStatistics8 extends Product8 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int totalImportQuantity;
    private double totalImportValue;

    public ImportStatistics8() {
        super();
    }

    public ImportStatistics8(int totalImportQuantity, double totalImportValue) {
        super();
        this.totalImportQuantity = totalImportQuantity;
        this.totalImportValue = totalImportValue;
    }

    public ImportStatistics8(int id, String name, String image, String type, String unit,
                             double price, int stockQuantity, String description,
                             int totalImportQuantity, double totalImportValue) {
        super(id, name, image, type, unit, price, stockQuantity, description);
        this.totalImportQuantity = totalImportQuantity;
        this.totalImportValue = totalImportValue;
    }

    public int getTotalImportQuantity() {
        return totalImportQuantity;
    }

    public void setTotalImportQuantity(int totalImportQuantity) {
        this.totalImportQuantity = totalImportQuantity;
    }

    public double getTotalImportValue() {
        return totalImportValue;
    }

    public void setTotalImportValue(double totalImportValue) {
        this.totalImportValue = totalImportValue;
    }
}

