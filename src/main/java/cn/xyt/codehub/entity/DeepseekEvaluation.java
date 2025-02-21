package cn.xyt.codehub.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("deepseek") // 绑定数据库表 course
public class DeepseekEvaluation {
    @TableId
    private Long id;
    private Long submissionId;
    private String evaluation;

    public DeepseekEvaluation(Long id, Long submissionId, String evaluation) {
        this.id = id;
        this.submissionId = submissionId;
        this.evaluation = evaluation;
    }
}
