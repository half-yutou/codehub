package cn.xyt.codehub.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class TeacherDTO {
    @NotEmpty
    private String username;         // 用户名
    private String password;         // 密码（加密存储）
    private String email;            // 邮箱
    private String phone;            // 电话
    private String department;       // 所属院系
}
