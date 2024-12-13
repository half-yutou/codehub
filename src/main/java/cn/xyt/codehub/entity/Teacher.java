package cn.xyt.codehub.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {
    private Long id;                 // 主键ID
    private String username;         // 用户名
    private String password;         // 密码（加密存储）
    private String email;            // 邮箱
    private String phone;            // 电话
    private String department;       // 所属院系

    @JsonIgnore
    private LocalDateTime createTime; // 创建时间

    @JsonIgnore
    private LocalDateTime updateTime; // 更新时间
}
