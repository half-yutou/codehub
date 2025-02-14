package cn.xyt.codehub.dto;

import lombok.Data;

@Data
public class DownloadSubDTO {
    private Long classId;

    private Long assignmentId;

    private Long studentId;

    private String fileName;
}
