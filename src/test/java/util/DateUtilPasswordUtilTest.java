package util;

import org.junit.Test;

import java.sql.Date;

import static org.junit.Assert.*;

/**
 * Kiểm thử đơn vị cho xử lý ngày tháng chặt chẽ (DateUtil8)
 * và băm/kiểm tra mật khẩu PBKDF2 có salt (PasswordUtil8).
 */
public class DateUtilPasswordUtilTest {

    @Test
    public void testStrictDateParsingRejectsInvalidAndTrailingText() {
        assertEquals("2026-05-01", DateUtil8.toIsoString(DateUtil8.parseDate("2026-05-01")));
        assertEquals("01/05/2026", DateUtil8.formatDate(DateUtil8.parseDate("01/05/2026")));
        assertEquals("2024-02-29", DateUtil8.toIsoString(DateUtil8.parseDate("2024-02-29")));

        // Ký tự dư thừa phía sau phải bị từ chối
        assertNull(DateUtil8.parseDate("2026-05-01XYZ"));
        assertNull(DateUtil8.parseDate("01/05/2026ABC"));
        assertNull(DateUtil8.parseDate("2026-05-01; DROP TABLE tblInvoice8"));

        // Ngày không tồn tại trên lịch phải bị từ chối
        assertNull(DateUtil8.parseDate("2026-02-29"));
        assertNull(DateUtil8.parseDate("29/02/2026"));
        assertNull(DateUtil8.parseDate("2026-04-31"));
        assertNull(DateUtil8.parseDate("2026-13-01"));
        assertNull(DateUtil8.parseDate(""));
        assertNull(DateUtil8.parseDate(null));
    }

    @Test
    public void testDateFormattingHandlesBothSqlDateAndUtilDate() {
        Date sqlDate = Date.valueOf("2026-07-22");
        java.util.Date utilDate = new java.util.Date(sqlDate.getTime());

        assertEquals("22/07/2026", DateUtil8.formatDate(sqlDate));
        assertEquals("2026-07-22", DateUtil8.toIsoString(sqlDate));
        assertEquals("22/07/2026", DateUtil8.formatDate(utilDate));
        assertEquals("2026-07-22", DateUtil8.toIsoString(utilDate));
    }

    @Test
    public void testPasswordHashingIsSaltedRejectsPlaintextAndLegacySha256AndPreservesSpaces() {
        String h1 = PasswordUtil8.hashPassword("123456");
        String h2 = PasswordUtil8.hashPassword("123456");

        assertNotNull(h1);
        assertTrue(h1.startsWith("pbkdf2_sha256$65536$"));
        assertNotEquals("Hai lần băm cùng mật khẩu phải tạo salt và chuỗi khác nhau", h1, h2);
        assertTrue(PasswordUtil8.checkPassword("123456", h1));
        assertTrue(PasswordUtil8.checkPassword("123456", h2));

        // Tuyệt đối từ chối mật khẩu rõ và hash SHA-256 cũ không salt
        assertFalse(PasswordUtil8.checkPassword("123456", "123456"));
        assertFalse(PasswordUtil8.checkPassword(
                "123456",
                "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92"));
        assertFalse(PasswordUtil8.checkPassword(
                "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92",
                "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92"));

        // Tuyệt đối không trim mật khẩu
        String spacedHash = PasswordUtil8.hashPassword(" abc ");
        assertTrue(PasswordUtil8.checkPassword(" abc ", spacedHash));
        assertFalse(PasswordUtil8.checkPassword("abc", spacedHash));
        assertFalse(PasswordUtil8.checkPassword(" 123456 ", h1));

        // Chuỗi hash sai định dạng hoặc số vòng lặp quá thấp bị từ chối
        assertFalse(PasswordUtil8.checkPassword("123456", "pbkdf2_sha256$100$salt$hash"));
        assertFalse(PasswordUtil8.checkPassword("123456", "pbkdf2_sha256$invalid"));
    }
}

