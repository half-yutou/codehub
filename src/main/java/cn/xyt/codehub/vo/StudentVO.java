package cn.xyt.codehub.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentVO {
    private Long id;                // 主键ID
    private String username;        // 用户名
    private String email;           // 邮箱
    private String phone;           // 电话
    private String studentNumber;   // 学号
    private Integer grade = 0;          // 年级(默认不填)
    private String gender;          // 性别
    private String adminClass;      // 行政班级 (字符串表示即可)
}
