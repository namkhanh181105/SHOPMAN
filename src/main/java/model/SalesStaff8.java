package model;

/**
 * Lớp thực thể SalesStaff8 kế thừa từ Employee8.
 */
public class SalesStaff8 extends Employee8 {
    private static final long serialVersionUID = 1L;

    public SalesStaff8() {
        super();
    }

    public SalesStaff8(int id, String name, String username, String password, String email, String phoneNumber, String position) {
        super(id, name, username, password, email, phoneNumber, position);
    }
}

