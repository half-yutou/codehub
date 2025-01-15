package cn.xyt.codehub.vo;

import cn.xyt.codehub.entity.Teacher;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentVO {
    private Long id;           // 作业ID

    private String title;      // 作业标题

    private String description;       // 作业内容

    private LocalDateTime deadline; // 作业截止时间

    private Long classId;     // 关联教学班级ID

    private Teacher teacher; // 创建者
}
