package cn.xyt.codehub.vo;

import cn.xyt.codehub.entity.Semester;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeachClassVO {
    private Long id;               // 主键ID
    private String name;           // 教学班级名称
    private Integer count;         // 班级人数
    private Long courseId;         // 课程ID（外键）
    private Long teacherId;        // 教师ID（外键）
    private Long semesterId;       // 学期ID（外键）
    private Semester semester;
}
