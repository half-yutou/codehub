package cn.xyt.codehub.dto;

import lombok.Data;

@Data
public class CourseDTO {
    private String name;          // 课程名称

    private String description;   // 课程描述

    private String semesterId;    // 学期ID（外键）

    private Integer isCodeSubmit; // 是否需要提交代码（0：否，1：是）
}
