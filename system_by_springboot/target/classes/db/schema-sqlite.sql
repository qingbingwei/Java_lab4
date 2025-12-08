-- 学生成绩管理系统 SQLite 数据库表结构

-- 学生表
CREATE TABLE IF NOT EXISTS student (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    student_id VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    gender VARCHAR(10) NOT NULL,
    class_name VARCHAR(50),
    grade VARCHAR(10),
    major VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

-- 教师表
CREATE TABLE IF NOT EXISTS teacher (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    teacher_id VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    title VARCHAR(50),
    department VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

-- 课程表
CREATE TABLE IF NOT EXISTS course (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    course_id VARCHAR(20) NOT NULL UNIQUE,
    course_name VARCHAR(100) NOT NULL,
    credits DECIMAL(3,1),
    course_type VARCHAR(20),
    hours INTEGER,
    description TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0
);

-- 教学班表
CREATE TABLE IF NOT EXISTS teaching_class (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    class_id VARCHAR(30) NOT NULL UNIQUE,
    course_db_id INTEGER NOT NULL,
    teacher_db_id INTEGER NOT NULL,
    semester VARCHAR(20) NOT NULL,
    capacity INTEGER DEFAULT 100,
    current_size INTEGER DEFAULT 0,
    schedule_time VARCHAR(100),
    classroom VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0,
    FOREIGN KEY (course_db_id) REFERENCES course(id),
    FOREIGN KEY (teacher_db_id) REFERENCES teacher(id)
);

-- 选课表（学生-教学班关联）
CREATE TABLE IF NOT EXISTS enrollment (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    student_db_id INTEGER NOT NULL,
    teaching_class_db_id INTEGER NOT NULL,
    enroll_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0,
    FOREIGN KEY (student_db_id) REFERENCES student(id),
    FOREIGN KEY (teaching_class_db_id) REFERENCES teaching_class(id),
    UNIQUE (student_db_id, teaching_class_db_id)
);

-- 成绩表
CREATE TABLE IF NOT EXISTS score (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    student_db_id INTEGER NOT NULL,
    teaching_class_db_id INTEGER NOT NULL,
    regular_score DECIMAL(5,2),
    midterm_score DECIMAL(5,2),
    experiment_score DECIMAL(5,2),
    final_exam_score DECIMAL(5,2),
    final_score DECIMAL(5,2),
    grade_point DECIMAL(3,2),
    remark TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted INTEGER DEFAULT 0,
    FOREIGN KEY (student_db_id) REFERENCES student(id),
    FOREIGN KEY (teaching_class_db_id) REFERENCES teaching_class(id),
    UNIQUE (student_db_id, teaching_class_db_id)
);

-- 创建索引优化查询性能
CREATE INDEX IF NOT EXISTS idx_student_name ON student(name);
CREATE INDEX IF NOT EXISTS idx_student_class ON student(class_name);
CREATE INDEX IF NOT EXISTS idx_student_grade ON student(grade);
CREATE INDEX IF NOT EXISTS idx_teacher_name ON teacher(name);
CREATE INDEX IF NOT EXISTS idx_teacher_dept ON teacher(department);
CREATE INDEX IF NOT EXISTS idx_course_name ON course(course_name);
CREATE INDEX IF NOT EXISTS idx_course_type ON course(course_type);
CREATE INDEX IF NOT EXISTS idx_teaching_class_semester ON teaching_class(semester);
CREATE INDEX IF NOT EXISTS idx_teaching_class_course ON teaching_class(course_db_id);
CREATE INDEX IF NOT EXISTS idx_teaching_class_teacher ON teaching_class(teacher_db_id);
CREATE INDEX IF NOT EXISTS idx_enrollment_student ON enrollment(student_db_id);
CREATE INDEX IF NOT EXISTS idx_enrollment_class ON enrollment(teaching_class_db_id);
CREATE INDEX IF NOT EXISTS idx_score_student ON score(student_db_id);
CREATE INDEX IF NOT EXISTS idx_score_class ON score(teaching_class_db_id);
CREATE INDEX IF NOT EXISTS idx_score_final ON score(final_score);

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(200) NOT NULL,
    real_name VARCHAR(50),
    email VARCHAR(100),
    role VARCHAR(20) NOT NULL,
    ref_id INTEGER,
    status INTEGER DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_login_time DATETIME,
    deleted INTEGER DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_user_username ON sys_user(username);
CREATE INDEX IF NOT EXISTS idx_user_role ON sys_user(role);
