package cn.xyt.codehub.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("assignment") // 对应数据库表 assignment
public class Assignment {

    @TableId
    private Long id;           // 作业ID

    private String title;      // 作业标题

    private String desc;       // 作业内容

    private LocalDateTime deadline; // 作业截止时间

    private Long courseId;     // 关联课程ID

    private Long createdBy;    // 创建者ID (教师ID)
}
