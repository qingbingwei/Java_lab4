package com.example.studentscore.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("sys_user")
@Schema(description = "用户实体")
public class User {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码(加密)")
    private String password;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "角色: ADMIN-管理员, TEACHER-教师, STUDENT-学生")
    private String role;

    @Schema(description = "关联ID(学生ID或教师ID)")
    private Long refId;

    @Schema(description = "状态: 0-禁用, 1-启用")
    private Integer status;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @TableLogic
    @Schema(description = "删除标志")
    private Integer deleted;
}
