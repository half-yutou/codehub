package cn.xyt.codehub.dto;

import lombok.Data;

@Data
public class CodeReviewDTO {
    private boolean overThreshold;

    private Long studentId;

    private Long submissionId;

    private Long anotherSubmissionId;

    private int similarityPercentage;

    private String similarityReport;

    public static CodeReviewDTO pass() {
        CodeReviewDTO codeReviewDTO = new CodeReviewDTO();
        codeReviewDTO.setOverThreshold(false);
        codeReviewDTO.setSimilarityPercentage(0);
        codeReviewDTO.setSimilarityReport("");
        return codeReviewDTO;
    }

    public static CodeReviewDTO fail(int similarityPercentage, String similarityReport) {
        CodeReviewDTO codeReviewDTO = new CodeReviewDTO();
        codeReviewDTO.setOverThreshold(true);
        codeReviewDTO.setSimilarityPercentage(similarityPercentage);
        codeReviewDTO.setSimilarityReport(similarityReport);
        return codeReviewDTO;
    }
}
