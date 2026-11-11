package util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * Tiện ích băm và kiểm tra mật khẩu an toàn bằng PBKDF2WithHmacSHA256 kèm salt ngẫu nhiên.
 */
public class PasswordUtil8 {
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String HASH_PREFIX = "pbkdf2_sha256";
    private static final int SALT_BYTES = 16;
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH_BITS = 256;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Băm mật khẩu bằng PBKDF2WithHmacSHA256 kèm salt ngẫu nhiên 16 byte.
     * Định dạng lưu: pbkdf2_sha256$<iterations>$<base64Salt>$<base64Hash>.
     */
    public static String hashPassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Mật khẩu không được null.");
        }
        byte[] salt = new byte[SALT_BYTES];
        SECURE_RANDOM.nextBytes(salt);
        byte[] derived = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS);
        Base64.Encoder encoder = Base64.getEncoder();
        return HASH_PREFIX + "$" + ITERATIONS + "$"
                + encoder.encodeToString(salt) + "$"
                + encoder.encodeToString(derived);
    }

    /**
     * Kiểm tra tính hợp lệ của mật khẩu bằng so sánh thời gian hằng (constant-time).
     * Chỉ chấp nhận chuỗi hash PBKDF2 hợp lệ; không trim() mật khẩu và không chấp nhận mật khẩu rõ/hash cũ.
     */
    public static boolean checkPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null || storedHash.isEmpty()) {
            return false;
        }
        String[] parts = storedHash.split("\\$");
        if (parts.length != 4 || !HASH_PREFIX.equals(parts[0])) {
            return false;
        }
        int iterations;
        try {
            iterations = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return false;
        }
        if (iterations < 10000) {
            return false;
        }
        byte[] salt;
        byte[] expectedHash;
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            salt = decoder.decode(parts[2]);
            expectedHash = decoder.decode(parts[3]);
        } catch (IllegalArgumentException e) {
            return false;
        }
        if (salt.length < 8 || expectedHash.length == 0) {
            return false;
        }
        byte[] actualHash = pbkdf2(plainPassword.toCharArray(), salt, iterations, expectedHash.length * 8);
        return MessageDigest.isEqual(actualHash, expectedHash);
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLengthBits) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLengthBits);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Thuật toán băm mật khẩu PBKDF2WithHmacSHA256 không khả dụng", e);
        } finally {
            spec.clearPassword();
        }
    }

    /**
     * Tiện ích dòng lệnh hỗ trợ tạo chuỗi hash PBKDF2 mới khi chuyển đổi hoặc đặt lại mật khẩu tài khoản cũ.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Cách dùng: PasswordUtil8 <mat-khau-moi>");
            System.exit(1);
        }
        System.out.println(hashPassword(args[0]));
    }
}

