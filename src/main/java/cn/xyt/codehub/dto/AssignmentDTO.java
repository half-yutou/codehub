package cn.xyt.codehub.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentDTO {
    private String title;      // 作业标题

    private String description;       // 作业内容

    private LocalDateTime deadline; // 作业截止时间

    private Long classId;     // 关联教学班级ID

    private Long teacherId;    // 创建者ID (教师ID)
}
