package model;

import java.io.Serializable;

/**
 * Lớp thực thể ExportStatistics8 theo sơ đồ lớp thực thể pha thiết kế.
 */
public class ExportStatistics8 extends Product8 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int totalExportQuantity;
    private double totalExportValue;

    public ExportStatistics8() {
        super();
    }

    public ExportStatistics8(int totalExportQuantity, double totalExportValue) {
        super();
        this.totalExportQuantity = totalExportQuantity;
        this.totalExportValue = totalExportValue;
    }

    public ExportStatistics8(int id, String name, String image, String type, String unit,
                             double price, int stockQuantity, String description,
                             int totalExportQuantity, double totalExportValue) {
        super(id, name, image, type, unit, price, stockQuantity, description);
        this.totalExportQuantity = totalExportQuantity;
        this.totalExportValue = totalExportValue;
    }

    public int getTotalExportQuantity() {
        return totalExportQuantity;
    }

    public void setTotalExportQuantity(int totalExportQuantity) {
        this.totalExportQuantity = totalExportQuantity;
    }

    public double getTotalExportValue() {
        return totalExportValue;
    }

    public void setTotalExportValue(double totalExportValue) {
        this.totalExportValue = totalExportValue;
    }
}

