package cn.xyt.codehub.controller;

import cn.xyt.codehub.dto.Result;
import cn.xyt.codehub.entity.CodeReviewReport;
import cn.xyt.codehub.service.CodeReviewReportService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/review")
@Tag(name = "获取代码查重信息方法")
public class CodeReviewReportController {
    @Resource
    private CodeReviewReportService codeReviewReportService;

    /**
     * 根据submissionId查询代码查重信息
     */
    @Operation(summary = "根据submissionId查询代码查重信息")
    @GetMapping("/get/submission/{submissionId}")
    public Result listAllCodeReviewReports(@PathVariable Long submissionId) {
        CodeReviewReport reviewReport = codeReviewReportService.getOne(new QueryWrapper<CodeReviewReport>().eq("submission_id", submissionId));
        if (reviewReport == null) {
            return Result.fail("查无此记录");
        }
        return Result.ok(reviewReport);
    }
}
