package cn.xyt.codehub.dto;

import lombok.Data;

@Data
public class TeachClassDTO {
    private String name;           // 教学班级名称
    private Long courseId;         // 课程ID（外键）
    private Long teacherId;        // 教师ID（外键）
    private Long semesterId;       // 学期ID（外键）
}
