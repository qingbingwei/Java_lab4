package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 密码加密工具类
 * 使用SHA-256算法和盐值进行密码加密
 */
public class PasswordUtil {
    private static final Logger LOGGER = Logger.getLogger(PasswordUtil.class.getName());
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
     * @param password 原始密码
     * @param salt 盐值
     * @return 加密后的密码
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt.getBytes());
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "密码加密失败", e);
            return null;
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
     * @param password 输入的密码
     * @param encryptedPassword 存储的加密密码（包含盐值）
     * @return 是否匹配
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

    /**
     * 检查密码强度
     * @param password 密码
     * @return 强度级别 (0-4)
     */
    public static int checkPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return 0;
        }

        int strength = 0;
        if (password.length() >= 8) strength++;
        if (password.matches(".*[a-z].*")) strength++;
        if (password.matches(".*[A-Z].*")) strength++;
        if (password.matches(".*[0-9].*")) strength++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) strength++;

        return Math.min(strength, 4);
    }

    /**
     * 验证密码是否符合策略
     * 至少8个字符，包含大小写字母和数字
     */
    public static boolean validatePasswordPolicy(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        
        return hasUpper && hasLower && hasDigit;
    }
}
