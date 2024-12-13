package cn.xyt.codehub.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String role;
    private String username;         // 用户名
    private String password;         // 密码（加密存储）
    private String email;            // 邮箱
    private String phone;            // 电话

    // teacher
    private String department;       // 所属院系

    // student
    private String studentNumber; // 学号
    private Long classId;         // 班级ID
    private Integer grade;        // 年级
}
