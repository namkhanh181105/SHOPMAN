package model;

import java.io.Serializable;

/**
 * Lớp thực thể Supplier8 đại diện cho nhà cung cấp trong hệ thống ShopMan.
 */
public class Supplier8 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;

    public Supplier8() {
    }

    public Supplier8(int id, String name, String address, String phoneNumber, String email) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

