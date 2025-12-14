package com.example.studentscore.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 登录响应VO
 */
@Data
@Schema(description = "登录响应")
public class LoginVO {

    @Schema(description = "访问令牌")
    private String token;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "角色")
    private String role;

    @Schema(description = "关联ID")
    private Long refId;

    @Schema(description = "菜单权限列表")
    private List<String> permissions;
}
