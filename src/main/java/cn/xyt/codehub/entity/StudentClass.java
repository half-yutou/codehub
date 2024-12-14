package cn.xyt.codehub.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("student_class") // 对应数据库表 student_class
public class StudentClass {

    @TableId
    private Long id;         // 主键ID（自动生成）

    private String studentNumber;  // 学生学号，对应学生表的外键

    private Long classId;    // 班级ID，对应教学班级表的外键

    // 联合唯一键
    public StudentClass(String studentNumber, Long classId) {
        this.studentNumber = studentNumber;
        this.classId = classId;
    }
}
