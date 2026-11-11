package model;

/**
 * Lớp CustomerStatistic8 kế thừa từ Customer8.
 */
public class CustomerStatistic8 extends Customer8 {
    private static final long serialVersionUID = 1L;

    private int orderCount;
    private double totalPurchaseAmount;

    public CustomerStatistic8() {
        super();
    }

    public CustomerStatistic8(int id, String name, String username, String password, String email, String phoneNumber, String address, int orderCount, double totalPurchaseAmount) {
        super(id, name, username, password, email, phoneNumber, address);
        this.orderCount = orderCount;
        this.totalPurchaseAmount = totalPurchaseAmount;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public double getTotalPurchaseAmount() {
        return totalPurchaseAmount;
    }

    public void setTotalPurchaseAmount(double totalPurchaseAmount) {
        this.totalPurchaseAmount = totalPurchaseAmount;
    }
}

