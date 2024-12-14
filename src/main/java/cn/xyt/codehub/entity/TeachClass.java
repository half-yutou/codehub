package cn.xyt.codehub.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("class")
public class TeachClass {
    @TableId
    private Long id;               // 主键ID
    private String name;           // 教学班级名称
    private Integer count;         // 班级人数
    private Long courseId;         // 课程ID（外键）
    private Long teacherId;        // 教师ID（外键）
    private Long semesterId;       // 学期ID（外键）
}
