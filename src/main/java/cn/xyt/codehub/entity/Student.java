package cn.xyt.codehub.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String studentNumber; // 学号
    private Long classId;         // 班级ID
    private Integer grade;        // 年级

    @JsonIgnore
    private LocalDateTime createTime;

    @JsonIgnore
    private LocalDateTime updateTime;
}
