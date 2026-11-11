package model;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Kiểm thử tính hướng đối tượng (OOP) của các lớp trong gói model.
 */
public class ModelOOPTest {

    @Test
    public void testCustomerInheritance() {
        Customer8 cust = new Customer8(1, "Nguyễn Văn An", "khachhang1", "pass123", "an@example.com", "0912345678", "Hà Nội");
        assertTrue("Customer8 phải kế thừa từ Member8", cust instanceof Member8);
        assertEquals(1, cust.getId());
        assertEquals("Nguyễn Văn An", cust.getName());
        assertEquals("khachhang1", cust.getUsername());
        assertEquals("Hà Nội", cust.getAddress());
    }

    @Test
    public void testManagerInheritance() {
        Manager8 mgr = new Manager8(2, "Trần Văn Bình", "quanly1", "pass123", "binh@example.com", "0987654321", "Quản lý");
        assertTrue("Manager8 phải kế thừa từ Employee8", mgr instanceof Employee8);
        assertTrue("Employee8 phải kế thừa từ Member8", mgr instanceof Member8);
        assertEquals(2, mgr.getId());
        assertEquals("Trần Văn Bình", mgr.getName());
        assertEquals("Quản lý", mgr.getPosition());
    }

    @Test
    public void testSupplierImportStatisticsInheritance() {
        Date now = new Date();
        SupplierImportStatistics8 stat = new SupplierImportStatistics8(1, "NCC-Khánh", "Hà Nội", "0912111222", "khanh@example.com", 600, now);
        assertTrue("SupplierImportStatistics8 phải kế thừa từ Supplier8", stat instanceof Supplier8);
        assertEquals(1, stat.getId());
        assertEquals("NCC-Khánh", stat.getName());
        assertEquals(600, stat.getTotalQuantity());
        assertEquals(now, stat.getLastImportDate());
        assertEquals(now, stat.getLatestImportDate());
    }

    @Test
    public void testInvoiceAndDetailsCalculations() {
        Product8 p1 = new Product8(1, "Sữa Vinamilk", "milk.png", "Đồ uống", "Hộp", 500.0, 100, "Mô tả 1");
        Product8 p2 = new Product8(2, "Mì chính", "michinh.png", "Gia vị", "Gói", 300.0, 80, "Mô tả 2");

        InvoiceDetail8 d1 = new InvoiceDetail8(1, 500.0, 2, p1);
        InvoiceDetail8 d2 = new InvoiceDetail8(2, 300.0, 1, p2);

        assertEquals(1000.0, d1.getAmount(), 0.001);
        assertEquals(300.0, d2.getAmount(), 0.001);

        Invoice8 inv = new Invoice8();
        inv.setId(1);
        inv.setDetails(new InvoiceDetail8[]{d1, d2});

        assertEquals(1300.0, inv.getTotalAmount(), 0.001);
    }

    @Test
    public void testImportInvoiceCalculations() {
        Product8 p1 = new Product8(1, "Sữa Vinamilk", "milk.png", "Đồ uống", "Hộp", 500.0, 100, "Mô tả 1");
        ImportInvoiceDetail8 d1 = new ImportInvoiceDetail8(1, 500.0, 40, p1);
        ImportInvoiceDetail8 d2 = new ImportInvoiceDetail8(2, 300.0, 20, p1);

        ImportInvoice8 importInv = new ImportInvoice8();
        importInv.setId(1);
        importInv.setDetails(new ImportInvoiceDetail8[]{d1, d2});

        assertEquals(60, importInv.getTotalQuantity());
        assertEquals(26000.0, importInv.getTotalAmount(), 0.001);
    }

    @Test
    public void testStatisticsExtendProduct8WithoutRedeclaringFields() {
        ImportStatistics8 imp = new ImportStatistics8(
                10, "Bim bim Oishi", "snack.png", "Đồ ăn", "Gói", 8000.0, 50, "Mô tả",
                120, 960000.0);
        ExportStatistics8 exp = new ExportStatistics8(
                11, "Trà xanh C2", "tea.png", "Đồ uống", "Chai", 10000.0, 40, "Mô tả 2",
                80, 800000.0);

        assertTrue(imp instanceof Product8);
        assertTrue(exp instanceof Product8);
        assertEquals(Product8.class, ImportStatistics8.class.getSuperclass());
        assertEquals(Product8.class, ExportStatistics8.class.getSuperclass());

        assertEquals(10, imp.getId());
        assertEquals("Bim bim Oishi", imp.getName());
        assertEquals(120, imp.getTotalImportQuantity());
        assertEquals(960000.0, imp.getTotalImportValue(), 0.001);

        assertEquals(11, exp.getId());
        assertEquals("Trà xanh C2", exp.getName());
        assertEquals(80, exp.getTotalExportQuantity());
        assertEquals(800000.0, exp.getTotalExportValue(), 0.001);

        java.util.Set<String> productFields = new java.util.HashSet<>();
        for (java.lang.reflect.Field f : Product8.class.getDeclaredFields()) {
            if (!"serialVersionUID".equals(f.getName())) {
                productFields.add(f.getName());
            }
        }
        for (java.lang.reflect.Field f : ImportStatistics8.class.getDeclaredFields()) {
            assertFalse("ImportStatistics8 không được khai báo lại thuộc tính của Product8: " + f.getName(),
                    productFields.contains(f.getName()));
        }
        for (java.lang.reflect.Field f : ExportStatistics8.class.getDeclaredFields()) {
            assertFalse("ExportStatistics8 không được khai báo lại thuộc tính của Product8: " + f.getName(),
                    productFields.contains(f.getName()));
        }
    }
}

