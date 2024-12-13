package cn.xyt.codehub.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("course") // 绑定数据库表 course
public class Course {
    @TableId
    private Long id;              // 课程ID

    private String name;          // 课程名称

    private String description;   // 课程描述

    private String teacherId;     // 教师ID（外键）

    private String semesterId;    // 学期ID（外键）

    @TableField("is_code_submit")
    private Integer isCodeSubmit; // 是否需要提交代码（0：否，1：是）
}
