create database codehub;

use codehub;

CREATE TABLE admin (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 主键ID
                       username VARCHAR(50) NOT NULL UNIQUE, -- 用户名
                       password VARCHAR(100) NOT NULL,       -- 密码（加密存储）
                       role VARCHAR(50) DEFAULT 'ADMIN',     -- 管理员角色
                       create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 创建时间
                       update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 更新时间
);

CREATE TABLE teacher (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 主键ID
                         username VARCHAR(50) NOT NULL UNIQUE, -- 用户名
                         password VARCHAR(100) NOT NULL,       -- 密码（加密存储）
                         email VARCHAR(100),                   -- 邮箱
                         phone VARCHAR(20),                    -- 电话
                         department VARCHAR(100),              -- 所属院系
                         create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 创建时间
                         update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 更新时间
);

CREATE TABLE student (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 主键ID
                         username VARCHAR(50) NOT NULL UNIQUE, -- 用户名
                         password VARCHAR(100) NOT NULL,       -- 密码（加密存储）
                         email VARCHAR(100),                   -- 邮箱
                         phone VARCHAR(20),                    -- 电话
                         student_number VARCHAR(50) UNIQUE,    -- 学号
                         class_id BIGINT,                      -- 所属班级ID
                         grade INT,                            -- 年级
                         create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 创建时间
                         update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 更新时间
);


-- --
CREATE TABLE semester (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 主键ID
                          name VARCHAR(100) NOT NULL,           -- 学期名称（如 "2024 秋季学期"）
                          start_date DATE NOT NULL,             -- 学期开始日期
                          end_date DATE NOT NULL                -- 学期结束日期
);

CREATE TABLE course (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 主键ID
                        name VARCHAR(100) NOT NULL,           -- 课程名称
                        description TEXT,                     -- 课程描述
                        teacher_id BIGINT NOT NULL,           -- 任课教师ID（关联 teacher 表）
                        semester_id BIGINT NOT NULL,          -- 所属学期ID（关联 semester 表）
                        FOREIGN KEY (teacher_id) REFERENCES teacher(id),
                        FOREIGN KEY (semester_id) REFERENCES semester(id)
);

-- --
CREATE TABLE class (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 主键ID
                       name VARCHAR(100) NOT NULL,           -- 班级名称
                       semester_id BIGINT NOT NULL,          -- 所属学期ID（关联 semester 表）
                       teacher_id BIGINT,                    -- 班主任ID
                       FOREIGN KEY (semester_id) REFERENCES semester(id),
                       FOREIGN KEY (teacher_id) REFERENCES teacher(id)
);

-- --
-- 作业表
CREATE TABLE assignment (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 主键ID
                            title VARCHAR(200) NOT NULL,          -- 作业标题
                            description TEXT,                     -- 作业描述
                            course_id BIGINT NOT NULL,            -- 所属课程ID（关联 course 表）
                            deadline DATETIME NOT NULL,           -- 截止时间
                            created_by BIGINT NOT NULL,           -- 创建人ID（关联 user 表）
                            FOREIGN KEY (course_id) REFERENCES course(id),
                            FOREIGN KEY (created_by) REFERENCES student(id)
);

-- 提交表
CREATE TABLE submission (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 主键ID
                            assignment_id BIGINT NOT NULL,        -- 作业ID（关联 assignment 表）
                            student_id BIGINT NOT NULL,           -- 提交学生ID（关联 student 表）
                            file_path VARCHAR(255) NOT NULL,      -- 提交的文件路径
                            submitted_at DATETIME NOT NULL,       -- 提交时间
                            status VARCHAR(50) DEFAULT 'PENDING', -- 作业状态（如 PENDING, APPROVED）
                            FOREIGN KEY (assignment_id) REFERENCES assignment(id),
                            FOREIGN KEY (student_id) REFERENCES student(id)
);

