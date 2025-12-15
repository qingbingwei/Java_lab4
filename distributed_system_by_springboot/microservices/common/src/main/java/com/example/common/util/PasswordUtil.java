package com.example.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码工具类
 * 使用SHA-256算法和盐值进行密码加密
 */
public class PasswordUtil {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;

    /**
     * 生成随机盐值
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 使用SHA-256加密密码
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt.getBytes());
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }

    /**
     * 加密密码（自动生成盐值）
     * 返回格式: salt$hashedPassword
     */
    public static String encryptPassword(String password) {
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);
        return salt + "$" + hashedPassword;
    }

    /**
     * 验证密码
     */
    public static boolean verifyPassword(String password, String encryptedPassword) {
        if (encryptedPassword == null || !encryptedPassword.contains("$")) {
            return false;
        }

        String[] parts = encryptedPassword.split("\\$", 2);
        if (parts.length != 2) {
            return false;
        }

        String salt = parts[0];
        String storedHash = parts[1];
        String computedHash = hashPassword(password, salt);

        return storedHash.equals(computedHash);
    }
}
