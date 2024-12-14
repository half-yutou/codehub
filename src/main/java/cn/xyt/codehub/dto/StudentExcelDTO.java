package cn.xyt.codehub.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class StudentExcelDTO {
    @ExcelProperty("学号")
    private String studentNumber;

    @ExcelProperty("姓名")
    private String username;

    @ExcelProperty("行政班级")
    private String adminClass;

    @ExcelProperty("性别") // 性别以文本“男”或“女”形式导入
    private String gender;

    @ExcelProperty("手机号码")
    private String phone;

    @ExcelProperty("邮箱")
    private String email;
}
