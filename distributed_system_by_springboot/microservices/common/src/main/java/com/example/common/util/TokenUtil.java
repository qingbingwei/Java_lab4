package com.example.common.util;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单的Token管理器
 * 生产环境建议使用JWT或Redis
 */
public class TokenUtil {

    // 存储 token -> userId 的映射
    private static final Map<String, Long> TOKEN_MAP = new ConcurrentHashMap<>();
    // 存储 userId -> token 的映射
    private static final Map<Long, String> USER_TOKEN_MAP = new ConcurrentHashMap<>();

    /**
     * 生成Token
     */
    public static String generateToken(Long userId) {
        // 如果用户已有token，先移除旧token
        String oldToken = USER_TOKEN_MAP.get(userId);
        if (StrUtil.isNotBlank(oldToken)) {
            TOKEN_MAP.remove(oldToken);
        }

        String token = UUID.fastUUID().toString(true);
        TOKEN_MAP.put(token, userId);
        USER_TOKEN_MAP.put(userId, token);
        return token;
    }

    /**
     * 验证Token并返回用户ID
     */
    public static Long getUserIdByToken(String token) {
        if (StrUtil.isBlank(token)) {
            return null;
        }
        return TOKEN_MAP.get(token);
    }

    /**
     * 移除Token（登出）
     */
    public static void removeToken(String token) {
        Long userId = TOKEN_MAP.remove(token);
        if (userId != null) {
            USER_TOKEN_MAP.remove(userId);
        }
    }

    /**
     * 检查Token是否有效
     */
    public static boolean isValidToken(String token) {
        return TOKEN_MAP.containsKey(token);
    }
}
