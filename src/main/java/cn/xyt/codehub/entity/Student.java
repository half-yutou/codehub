package cn.xyt.codehub.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("student")
public class Student {
    @TableId
    private Long id;                // 主键ID
    private String username;        // 用户名
    private String password = "csu";        // 密码,默认csu
    private String email;           // 邮箱
    private String phone;           // 电话
    private String studentNumber;   // 学号
    private Integer grade = 0;          // 年级(默认不填)
    private String gender;          // 性别
    private String adminClass;      // 行政班级 (字符串表示即可)

    @JsonIgnore
    private LocalDateTime createTime; // 创建时间

    @JsonIgnore
    private LocalDateTime updateTime; // 更新时间
}

