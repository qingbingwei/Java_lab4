-- 学生成绩管理系统 SQLite 初始化数据

-- 插入测试学生数据
INSERT OR IGNORE INTO student (student_id, name, gender, class_name, grade, major, phone, email) VALUES
('2021001001', '张三', 'MALE', '计科2101', '2021', '计算机科学与技术', '13800138001', 'zhangsan@example.com'),
('2021001002', '李四', 'MALE', '计科2101', '2021', '计算机科学与技术', '13800138002', 'lisi@example.com'),
('2021001003', '王五', 'FEMALE', '计科2101', '2021', '计算机科学与技术', '13800138003', 'wangwu@example.com'),
('2021001004', '赵六', 'FEMALE', '计科2102', '2021', '计算机科学与技术', '13800138004', 'zhaoliu@example.com'),
('2021001005', '钱七', 'MALE', '计科2102', '2021', '计算机科学与技术', '13800138005', 'qianqi@example.com'),
('2022001001', '孙八', 'MALE', '软工2201', '2022', '软件工程', '13800138006', 'sunba@example.com'),
('2022001002', '周九', 'FEMALE', '软工2201', '2022', '软件工程', '13800138007', 'zhoujiu@example.com'),
('2022001003', '吴十', 'MALE', '软工2201', '2022', '软件工程', '13800138008', 'wushi@example.com');

-- 插入测试教师数据
INSERT OR IGNORE INTO teacher (teacher_id, name, title, department, phone, email) VALUES
('T001', '张教授', '教授', '计算机学院', '13900139001', 'zhangprof@example.com'),
('T002', '李副教授', '副教授', '计算机学院', '13900139002', 'liprof@example.com'),
('T003', '王讲师', '讲师', '计算机学院', '13900139003', 'wanglect@example.com'),
('T004', '陈教授', '教授', '软件学院', '13900139004', 'chenprof@example.com');

-- 插入测试课程数据
INSERT OR IGNORE INTO course (course_id, course_name, credits, course_type, hours, description) VALUES
('CS101', '高等数学', 4.0, '必修', 64, '高等数学基础课程'),
('CS102', 'Java程序设计', 3.0, '必修', 48, 'Java语言程序设计'),
('CS103', '数据结构', 4.0, '必修', 64, '数据结构与算法'),
('CS104', '数据库原理', 3.5, '必修', 56, '数据库系统原理'),
('CS105', '计算机网络', 3.0, '必修', 48, '计算机网络原理'),
('CS201', '软件工程', 3.0, '选修', 48, '软件工程导论'),
('CS202', '人工智能', 2.0, '选修', 32, '人工智能基础');

-- 插入测试教学班数据
INSERT OR IGNORE INTO teaching_class (class_id, course_db_id, teacher_db_id, semester, capacity, current_size, schedule_time, classroom) VALUES
('CS101-2024-1-01', 1, 1, '2024-2025-1', 60, 4, '周一 1-2节', '教学楼A101'),
('CS102-2024-1-01', 2, 2, '2024-2025-1', 50, 5, '周二 3-4节', '教学楼A102'),
('CS103-2024-1-01', 3, 3, '2024-2025-1', 45, 5, '周三 1-2节', '教学楼B201'),
('CS104-2024-1-01', 4, 1, '2024-2025-1', 55, 3, '周四 5-6节', '教学楼A103'),
('CS201-2024-1-01', 6, 4, '2024-2025-1', 40, 3, '周五 3-4节', '教学楼C301');

-- 插入测试选课数据
INSERT OR IGNORE INTO enrollment (student_db_id, teaching_class_db_id) VALUES
(1, 1), (1, 2), (1, 3),
(2, 1), (2, 2), (2, 3),
(3, 1), (3, 2), (3, 4),
(4, 1), (4, 3), (4, 4),
(5, 2), (5, 3), (5, 5),
(6, 1), (6, 2), (6, 5),
(7, 1), (7, 4), (7, 5),
(8, 2), (8, 3), (8, 4);

-- 插入测试成绩数据
INSERT OR IGNORE INTO score (student_db_id, teaching_class_db_id, regular_score, midterm_score, experiment_score, final_exam_score, final_score, grade_point) VALUES
(1, 1, 85, 80, 88, 82, 83.4, 3.3),
(1, 2, 90, 88, 92, 85, 88.0, 4.0),
(1, 3, 78, 75, 80, 72, 75.6, 2.5),
(2, 1, 82, 78, 85, 80, 81.0, 3.0),
(2, 2, 88, 85, 90, 87, 87.4, 3.7),
(2, 3, 75, 70, 78, 68, 71.6, 2.1),
(3, 1, 92, 90, 95, 88, 90.6, 4.0),
(3, 2, 85, 82, 88, 80, 83.0, 3.3),
(4, 1, 70, 68, 72, 65, 68.0, 1.8),
(4, 3, 80, 78, 82, 75, 78.0, 2.8),
(5, 2, 95, 92, 98, 90, 93.0, 4.0),
(5, 3, 88, 85, 90, 82, 85.6, 3.5);

-- 插入系统用户数据（密码统一为：123456）
-- 密码是BCrypt加密后的哈希值
INSERT OR IGNORE INTO sys_user (username, password, real_name, email, role, ref_id, status) VALUES
-- 管理员账号
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'admin@example.com', 'ADMIN', NULL, 1),

-- 教师账号（对应4位教师）
('T001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张教授', 'zhangprof@example.com', 'TEACHER', 1, 1),
('T002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李副教授', 'liprof@example.com', 'TEACHER', 2, 1),
('T003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王讲师', 'wanglect@example.com', 'TEACHER', 3, 1),
('T004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '陈教授', 'chenprof@example.com', 'TEACHER', 4, 1),

-- 学生账号（对应8位学生）
('2021001001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张三', 'zhangsan@example.com', 'STUDENT', 1, 1),
('2021001002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李四', 'lisi@example.com', 'STUDENT', 2, 1),
('2021001003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王五', 'wangwu@example.com', 'STUDENT', 3, 1),
('2021001004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '赵六', 'zhaoliu@example.com', 'STUDENT', 4, 1),
('2021001005', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '钱七', 'qianqi@example.com', 'STUDENT', 5, 1),
('2022001001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '孙八', 'sunba@example.com', 'STUDENT', 6, 1),
('2022001002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '周九', 'zhoujiu@example.com', 'STUDENT', 7, 1),
('2022001003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '吴十', 'wushi@example.com', 'STUDENT', 8, 1);
