package cn.xyt.codehub.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("code_review_report")
public class CodeReviewReport {
    @TableId
    private Long id;

    private boolean overThreshold;

    private Long studentId;

    private Long submissionId;

    private Long anotherSubmissionId;

    private int similarityPercentage;

    private String similarityReport;
}
