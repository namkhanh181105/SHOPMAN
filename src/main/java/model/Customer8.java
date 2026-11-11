package model;

/**
 * Lớp thực thể Customer8 kế thừa từ Member8.
 */
public class Customer8 extends Member8 {
    private static final long serialVersionUID = 1L;

    private String address;

    public Customer8() {
        super();
    }

    public Customer8(int id, String name, String username, String password, String email, String phoneNumber, String address) {
        super(id, name, username, password, email, phoneNumber);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

