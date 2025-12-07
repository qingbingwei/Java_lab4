package util;

import model.User;

/**
 * 会话管理器 - 单例模式
 * 管理当前登录用户的会话信息
 */
public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    private long loginTime;
    private String sessionId;

    private SessionManager() {
    }

    /**
     * 获取SessionManager单例实例
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }

    /**
     * 登录用户
     */
    public void login(User user) {
        this.currentUser = user;
        this.loginTime = System.currentTimeMillis();
        this.sessionId = generateSessionId();
    }

    /**
     * 登出用户
     */
    public void logout() {
        this.currentUser = null;
        this.sessionId = null;
        this.loginTime = 0;
    }

    /**
     * 获取当前用户
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * 检查是否已登录
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * 获取会话ID
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * 获取登录时长（毫秒）
     */
    public long getSessionDuration() {
        if (!isLoggedIn()) {
            return 0;
        }
        return System.currentTimeMillis() - loginTime;
    }

    /**
     * 生成会话ID
     */
    private String generateSessionId() {
        return "SESSION_" + System.currentTimeMillis() + "_" + 
               (currentUser != null ? currentUser.getId() : "0");
    }

    /**
     * 检查会话是否超时（默认30分钟）
     */
    public boolean isSessionExpired() {
        if (!isLoggedIn()) {
            return true;
        }
        long timeout = 30 * 60 * 1000; // 30分钟
        return getSessionDuration() > timeout;
    }

    /**
     * 刷新会话
     */
    public void refreshSession() {
        this.loginTime = System.currentTimeMillis();
    }
}
