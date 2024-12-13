package cn.xyt.codehub.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SemesterDTO {
    private String name;        // 学期名称

    private LocalDate startDate; // 开学日期

    private LocalDate endDate;  // 结束日期
}
