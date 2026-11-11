package model;

/**
 * Lớp thực thể Manager8 kế thừa từ Employee8.
 */
public class Manager8 extends Employee8 {
    private static final long serialVersionUID = 1L;

    public Manager8() {
        super();
    }

    public Manager8(int id, String name, String username, String password, String email, String phoneNumber, String position) {
        super(id, name, username, password, email, phoneNumber, position);
    }
}

