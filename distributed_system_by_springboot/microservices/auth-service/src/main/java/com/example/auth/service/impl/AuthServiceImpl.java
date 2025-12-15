package com.example.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.auth.feign.StudentFeignClient;
import com.example.auth.feign.TeacherFeignClient;
import com.example.auth.mapper.UserMapper;
import com.example.auth.service.AuthService;
import com.example.common.dto.LoginDTO;
import com.example.common.entity.User;
import com.example.common.exception.BusinessException;
import com.example.common.result.Result;
import com.example.common.util.PasswordUtil;
import com.example.common.util.TokenUtil;
import com.example.common.entity.Student;
import com.example.common.entity.Teacher;
import com.example.common.vo.LoginVO;
import com.example.common.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl extends ServiceImpl<UserMapper, User> implements AuthService {

    private final StudentFeignClient studentFeignClient;
    private final TeacherFeignClient teacherFeignClient;

    // 角色对应的权限
    private static final List<String> ADMIN_PERMISSIONS = Arrays.asList(
            "dashboard", "students", "teachers", "courses", "classes",
            "scores", "enrollment", "statistics", "system"
    );

    private static final List<String> TEACHER_PERMISSIONS = Arrays.asList(
            "dashboard", "students:view", "courses:view", "classes",
            "scores", "statistics"
    );

    private static final List<String> STUDENT_PERMISSIONS = Arrays.asList(
            "dashboard", "scores:view", "enrollment"
    );

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 查找用户
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, loginDTO.getUsername()));

        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (user.getStatus() != 1) {
            throw new BusinessException("账户已被禁用");
        }

        // 验证密码
        if (!PasswordUtil.verifyPassword(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        updateById(user);

        // 生成Token
        String token = TokenUtil.generateToken(user.getId());

        // 构建返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setRealName(user.getRealName());
        loginVO.setRole(user.getRole());
        loginVO.setRefId(user.getRefId());
        
        // 获取学号/工号
        String businessId = getBusinessId(user.getRole(), user.getRefId());
        loginVO.setBusinessId(businessId);
        
        loginVO.setPermissions(getPermissionsByRole(user.getRole()));

        log.info("用户登录成功: {}", user.getUsername());
        return loginVO;
    }

    @Override
    public void logout(String token) {
        TokenUtil.removeToken(token);
        log.info("用户登出成功");
    }

    @Override
    public UserInfoVO getCurrentUser(String token) {
        Long userId = TokenUtil.getUserIdByToken(token);
        if (userId == null) {
            throw new BusinessException("未登录或登录已过期");
        }

        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setUserId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setRole(user.getRole());
        userInfo.setRoleName(getRoleName(user.getRole()));
        userInfo.setRefId(user.getRefId());
        
        // 获取学号/工号
        String businessId = getBusinessId(user.getRole(), user.getRefId());
        userInfo.setBusinessId(businessId);
        
        userInfo.setPermissions(getPermissionsByRole(user.getRole()));

        return userInfo;
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (!PasswordUtil.verifyPassword(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        user.setPassword(PasswordUtil.encryptPassword(newPassword));
        updateById(user);
        log.info("用户修改密码成功: {}", user.getUsername());
    }

    @Override
    public void initDefaultUsers() {
        // 检查是否已初始化
        if (count() > 0) {
            log.info("用户已存在，执行同步...");
            syncUsers();
            return;
        }

        log.info("初始化默认用户...");
        
        List<User> userList = new ArrayList<>();

        // 创建管理员
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(PasswordUtil.encryptPassword("123456"));
        admin.setRealName("系统管理员");
        admin.setRole("ADMIN");
        admin.setStatus(1);
        admin.setCreateTime(LocalDateTime.now());
        userList.add(admin);
        
        // 为已有的教师创建用户
        try {
            Result<List<Teacher>> teacherResult = teacherFeignClient.getList();
            if (teacherResult != null && teacherResult.getData() != null) {
                for (Teacher teacher : teacherResult.getData()) {
                    User teacherUser = new User();
                    teacherUser.setUsername(teacher.getTeacherId());
                    teacherUser.setPassword(PasswordUtil.encryptPassword("123456"));
                    teacherUser.setRealName(teacher.getName());
                    teacherUser.setRole("TEACHER");
                    teacherUser.setRefId(teacher.getId());
                    teacherUser.setStatus(1);
                    teacherUser.setCreateTime(LocalDateTime.now());
                    userList.add(teacherUser);
                }
            }
        } catch (Exception e) {
            log.warn("获取教师列表失败，跳过教师用户初始化: {}", e.getMessage());
        }
        
        // 为已有的学生创建用户
        try {
            Result<List<Student>> studentResult = studentFeignClient.getList();
            if (studentResult != null && studentResult.getData() != null) {
                for (Student student : studentResult.getData()) {
                    User studentUser = new User();
                    studentUser.setUsername(student.getStudentId());
                    studentUser.setPassword(PasswordUtil.encryptPassword("123456"));
                    studentUser.setRealName(student.getName());
                    studentUser.setRole("STUDENT");
                    studentUser.setRefId(student.getId());
                    studentUser.setStatus(1);
                    studentUser.setCreateTime(LocalDateTime.now());
                    userList.add(studentUser);
                }
            }
        } catch (Exception e) {
            log.warn("获取学生列表失败，跳过学生用户初始化: {}", e.getMessage());
        }
        
        // 批量保存
        saveBatch(userList);
        log.info("默认用户初始化完成，共创建 {} 个用户", userList.size());
    }
    
    /**
     * 同步用户数据：为缺失的教师和学生创建用户账号，并更新已存在用户的 refId
     */
    private void syncUsers() {
        List<User> newUsers = new ArrayList<>();
        List<User> updateUsers = new ArrayList<>();
        
        // 同步教师用户
        try {
            Result<List<Teacher>> teacherResult = teacherFeignClient.getList();
            if (teacherResult != null && teacherResult.getData() != null) {
                for (Teacher teacher : teacherResult.getData()) {
                    // 检查用户是否已存在
                    LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(User::getUsername, teacher.getTeacherId());
                    User existingUser = getOne(wrapper);
                    
                    if (existingUser == null) {
                        // 创建新用户
                        User teacherUser = new User();
                        teacherUser.setUsername(teacher.getTeacherId());
                        teacherUser.setPassword(PasswordUtil.encryptPassword("123456"));
                        teacherUser.setRealName(teacher.getName());
                        teacherUser.setRole("TEACHER");
                        teacherUser.setRefId(teacher.getId());
                        teacherUser.setStatus(1);
                        teacherUser.setCreateTime(LocalDateTime.now());
                        newUsers.add(teacherUser);
                        log.info("创建教师用户: {}", teacher.getTeacherId());
                    } else if (existingUser.getRefId() == null) {
                        // 更新已存在用户的 refId
                        existingUser.setRefId(teacher.getId());
                        updateUsers.add(existingUser);
                        log.info("更新教师用户 refId: {} -> {}", teacher.getTeacherId(), teacher.getId());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("同步教师用户失败: {}", e.getMessage());
        }
        
        // 同步学生用户
        try {
            Result<List<Student>> studentResult = studentFeignClient.getList();
            if (studentResult != null && studentResult.getData() != null) {
                for (Student student : studentResult.getData()) {
                    // 检查用户是否已存在
                    LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(User::getUsername, student.getStudentId());
                    User existingUser = getOne(wrapper);
                    
                    if (existingUser == null) {
                        // 创建新用户
                        User studentUser = new User();
                        studentUser.setUsername(student.getStudentId());
                        studentUser.setPassword(PasswordUtil.encryptPassword("123456"));
                        studentUser.setRealName(student.getName());
                        studentUser.setRole("STUDENT");
                        studentUser.setRefId(student.getId());
                        studentUser.setStatus(1);
                        studentUser.setCreateTime(LocalDateTime.now());
                        newUsers.add(studentUser);
                        log.info("创建学生用户: {}", student.getStudentId());
                    } else if (existingUser.getRefId() == null) {
                        // 更新已存在用户的 refId
                        existingUser.setRefId(student.getId());
                        updateUsers.add(existingUser);
                        log.info("更新学生用户 refId: {} -> {}", student.getStudentId(), student.getId());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("同步学生用户失败: {}", e.getMessage());
        }
        
        // 批量保存新用户
        if (!newUsers.isEmpty()) {
            saveBatch(newUsers);
            log.info("用户同步完成，共创建 {} 个新用户", newUsers.size());
        }
        
        // 批量更新已存在用户
        if (!updateUsers.isEmpty()) {
            updateBatchById(updateUsers);
            log.info("用户同步完成，共更新 {} 个用户的 refId", updateUsers.size());
        }
        
        if (newUsers.isEmpty() && updateUsers.isEmpty()) {
            log.info("用户同步完成，无需创建或更新用户");
        }
    }

    private List<String> getPermissionsByRole(String role) {
        return switch (role) {
            case "ADMIN" -> new ArrayList<>(ADMIN_PERMISSIONS);
            case "TEACHER" -> new ArrayList<>(TEACHER_PERMISSIONS);
            case "STUDENT" -> new ArrayList<>(STUDENT_PERMISSIONS);
            default -> new ArrayList<>();
        };
    }

    private String getRoleName(String role) {
        return switch (role) {
            case "ADMIN" -> "系统管理员";
            case "TEACHER" -> "教师";
            case "STUDENT" -> "学生";
            default -> "未知";
        };
    }

    /**
     * 获取业务ID(学号/工号)
     */
    private String getBusinessId(String role, Long refId) {
        if (refId == null) {
            return null;
        }
        
        try {
            if ("STUDENT".equals(role)) {
                Result<Student> result = studentFeignClient.getById(refId);
                if (result != null && result.getData() != null) {
                    return result.getData().getStudentId();
                }
            } else if ("TEACHER".equals(role)) {
                Result<Teacher> result = teacherFeignClient.getById(refId);
                if (result != null && result.getData() != null) {
                    return result.getData().getTeacherId();
                }
            }
        } catch (Exception e) {
            log.warn("获取业务ID失败: role={}, refId={}", role, refId, e);
        }
        
        return null;
    }
}
