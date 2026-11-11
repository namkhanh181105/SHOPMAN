package model;

/**
 * Lớp thực thể DeliveryStaff8 kế thừa từ Employee8.
 */
public class DeliveryStaff8 extends Employee8 {
    private static final long serialVersionUID = 1L;

    public DeliveryStaff8() {
        super();
    }

    public DeliveryStaff8(int id, String name, String username, String password, String email, String phoneNumber, String position) {
        super(id, name, username, password, email, phoneNumber, position);
    }
}

