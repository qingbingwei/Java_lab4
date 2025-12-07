package com.example.studentscore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.studentscore.dto.LoginDTO;
import com.example.studentscore.entity.Student;
import com.example.studentscore.entity.Teacher;
import com.example.studentscore.entity.User;
import com.example.studentscore.exception.BusinessException;
import com.example.studentscore.mapper.StudentMapper;
import com.example.studentscore.mapper.TeacherMapper;
import com.example.studentscore.mapper.UserMapper;
import com.example.studentscore.service.AuthService;
import com.example.studentscore.util.PasswordUtil;
import com.example.studentscore.util.TokenUtil;
import com.example.studentscore.vo.LoginVO;
import com.example.studentscore.vo.UserInfoVO;
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

    private final StudentMapper studentMapper;
    private final TeacherMapper teacherMapper;

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
            log.info("用户已初始化，跳过");
            return;
        }
        
        log.info("初始化默认用户...");
        
        List<User> userList = new ArrayList<>();
        
        // 创建管理员
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(PasswordUtil.encryptPassword("admin123"));
        admin.setRealName("系统管理员");
        admin.setRole("ADMIN");
        admin.setStatus(1);
        admin.setCreateTime(LocalDateTime.now());
        userList.add(admin);
        
        // 为已有的教师创建用户
        List<Teacher> teachers = teacherMapper.selectList(null);
        for (Teacher teacher : teachers) {
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
        
        // 为已有的学生创建用户
        List<Student> students = studentMapper.selectList(null);
        for (Student student : students) {
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
        
        // 批量保存
        saveBatch(userList);
        
        log.info("默认用户初始化完成，共创建 {} 个用户", userList.size());
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
}
