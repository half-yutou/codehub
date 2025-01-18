package cn.xyt.codehub.vo;

import cn.xyt.codehub.entity.Teacher;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AssignmentVO {
    private Long id;           // 作业ID

    private String title;      // 作业标题

    private String description;       // 作业内容

    private LocalDateTime deadline; // 作业截止时间

    private Long classId;     // 关联教学班级ID

    private Teacher teacher; // 创建者

    private Long submitCount; // 提交次数

    private Integer totalCount ; // 总人数
}
