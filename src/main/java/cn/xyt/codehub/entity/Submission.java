package cn.xyt.codehub.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("submission")
public class Submission {
    @TableId
    private Long id;               // 主键
    private Long assignmentId;     // 作业ID
    private Long studentId;        // 学生ID
    private Long classId;          // 班级ID
    private String filename;       // 文件名
    private LocalDateTime submittedAt; // 提交时间
    private String status;         // 状态 (e.g., SUBMITTED)
}
