package cn.xyt.codehub.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeacherVO {
    private Long id;                 // 主键ID
    private String username;         // 用户名
    private String email;            // 邮箱
    private String phone;            // 电话
    private String department;       // 所属院系
}
