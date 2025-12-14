package com.example.studentscore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.studentscore.dto.LoginDTO;
import com.example.studentscore.entity.User;
import com.example.studentscore.vo.LoginVO;
import com.example.studentscore.vo.UserInfoVO;

/**
 * 认证服务接口
 */
public interface AuthService extends IService<User> {

    /**
     * 用户登录
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 用户登出
     */
    void logout(String token);

    /**
     * 获取当前用户信息
     */
    UserInfoVO getCurrentUser(String token);

    /**
     * 修改密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 初始化默认用户
     */
    void initDefaultUsers();
}
