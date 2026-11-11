package model;

/**
 * Lớp thực thể Employee8 kế thừa từ Member8.
 */
public class Employee8 extends Member8 {
    private static final long serialVersionUID = 1L;

    private String position;

    public Employee8() {
        super();
    }

    public Employee8(int id, String name, String username, String password, String email, String phoneNumber, String position) {
        super(id, name, username, password, email, phoneNumber);
        this.position = position;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}

