package cn.xyt.codehub.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;

@Data
@TableName("semester") // 映射到数据库中的表
public class Semester {
    @TableId
    private Long id;            // 学期ID

    private String name;        // 学期名称

    private LocalDate startDate; // 开学日期

    private LocalDate endDate;  // 结束日期

    @TableField("is_current")
    private Integer isCurrent;  // 是否当前学期（0：否，1：是）
}
