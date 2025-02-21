package cn.xyt.codehub.dto;

import lombok.Data;

@Data
public class TeacherManageDTO {
    private Long id;
    private String username;         // 用户名
    private String email;            // 邮箱
    private String phone;            // 电话
    private String department;       // 所属院系
}
