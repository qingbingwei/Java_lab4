package com.example.auth.controller;

import com.example.auth.service.AuthService;
import com.example.common.dto.LoginDTO;
import com.example.common.result.Result;
import com.example.common.util.TokenUtil;
import com.example.common.vo.LoginVO;
import com.example.common.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Tag(name = "认证管理", description = "用户登录、登出、获取用户信息等")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = authService.login(loginDTO);
        return Result.success("登录成功", loginVO);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Void> logout(
            @Parameter(description = "访问令牌") @RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        authService.logout(token);
        return Result.success("登出成功", null);
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/user-info")
    public Result<UserInfoVO> getCurrentUser(
            @Parameter(description = "访问令牌") @RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        UserInfoVO userInfo = authService.getCurrentUser(token);
        return Result.success(userInfo);
    }

    @Operation(summary = "修改密码")
    @PostMapping("/change-password")
    public Result<Void> changePassword(
            @Parameter(description = "访问令牌") @RequestHeader("Authorization") String token,
            @Parameter(description = "原密码") @RequestParam("oldPassword") String oldPassword,
            @Parameter(description = "新密码") @RequestParam("newPassword") String newPassword) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = TokenUtil.getUserIdByToken(token);
        if (userId == null) {
            return Result.error("未登录或登录已过期");
        }
        authService.changePassword(userId, oldPassword, newPassword);
        return Result.success("密码修改成功", null);
    }

    @Operation(summary = "初始化默认用户")
    @PostMapping("/init-users")
    public Result<Void> initDefaultUsers() {
        authService.initDefaultUsers();
        return Result.success("用户初始化成功", null);
    }
}
