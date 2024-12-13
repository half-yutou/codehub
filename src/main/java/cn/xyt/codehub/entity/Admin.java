package cn.xyt.codehub.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
    private Long id;                 // 主键ID
    private String username;         // 用户名
    private String password;         // 密码（加密存储）
    private String role;             // 管理员角色，默认为 "ADMIN"

    @JsonIgnore
    private LocalDateTime createTime; // 创建时间

    @JsonIgnore
    private LocalDateTime updateTime; // 更新时间
}
