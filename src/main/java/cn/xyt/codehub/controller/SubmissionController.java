package cn.xyt.codehub.controller;

import cn.xyt.codehub.dto.DownloadSubDTO;
import cn.xyt.codehub.dto.Result;
import cn.xyt.codehub.entity.DeepseekEvaluation;
import cn.xyt.codehub.entity.Submission;
import cn.xyt.codehub.mapper.DeepseekEvaluationMapper;
import cn.xyt.codehub.service.SubmissionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/submit")
@Tag(name = "作业提交方法")
public class SubmissionController {
    @Value("${submit.dir}")
    private String submitDir;

    @Resource
    private SubmissionService submissionService;
    @Autowired
    private DeepseekEvaluationMapper deepseekEvaluationMapper;


    // region submission-CRUD-methods

    // Create -> upload
    // 不支持更改,建议重新提交

    // 不支持删除

    /**
     * 删除指定作业
     */
//    @Operation(summary = "删除指定提交")
//    @DeleteMapping("/delete/{id}")
//    public Result deleteAssignment(@PathVariable Long id) {
//        return submissionService.removeById(id)
//                ? Result.ok("作业提交删除成功！")
//                : Result.fail("作业提交删除失败！");
//    }

    /**
     * 查询单个提交信息
     */
    @Operation(summary = "查询单个提交信息")
    @GetMapping("/get/{id}")
    public Result getSubmission(@PathVariable Long id) {
        return submissionService.getById(id) != null
                ? Result.ok(submissionService.getById(id))
                : Result.fail("提交信息不存在！");
    }

    /**
     * 查询当前学生所有提交
     */
    @Operation(summary = "查询当前学生所有提交")
    @GetMapping("/list/student/{studentId}")
    public Result listSubmissionsByStudentId(@PathVariable Long studentId) {
        return Result.ok(submissionService.list(
                new QueryWrapper<Submission>()
                        .eq("student_id", studentId)));
    }

    /**
     * 查询当前学生的当前作业提交
     */
    @Operation(summary = "查询当前学生的当前作业提交")
    @GetMapping("/get/student/{studentId}/assignment/{assignmentId}")
    public Result getSubmissionByStudentIdAndAssignmentId(@PathVariable Long studentId,
                                                          @PathVariable Long assignmentId) {
        List<Submission> submissions = submissionService.getSubmissionsByStudentIdAndAssignmentId(studentId, assignmentId);
        return Result.ok(submissions);
    }

    /**
     * 查询当前班级所有提交
     */
    @Operation(summary = "查询当前班级所有提交")
    @GetMapping("/list/class/{classId}")
    public Result listSubmissionsByClassId(@PathVariable Long classId) {
        return Result.ok(submissionService.list(
                new QueryWrapper<Submission>()
                        .eq("class_id", classId)));
    }


    /**
     * 查询当前作业所有提交
     */
    @Operation(summary = "查询某作业的所有提交")
    @GetMapping("/list/assignment/{assignmentId}")
    public Result listSubmissionsByAssignmentId(@PathVariable Long assignmentId) {
        return Result.ok(submissionService.list(
                new QueryWrapper<Submission>()
                        .eq("assignment_id", assignmentId)));
    }


    // endregion


    // region assignment-submit-download-methods


    /**
     * 提交作业文件
     */
    @Operation(summary = "上传作业")
    @CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
    @PostMapping("/upload")
    public Result uploadSubmission(@RequestParam("files") MultipartFile[] files,
                                   @RequestParam("content") String content,
                                   @RequestParam("assignmentId") Long assignmentId,
                                   @RequestParam("studentId") Long studentId,
                                   @RequestParam("classId") Long classId) {
        try {
            submissionService.handleFileUpload(files, content, assignmentId, studentId, classId);
            return Result.ok("作业提交成功！");
        } catch (Exception e) {
            return Result.fail("作业提交失败");
        }
    }

    /**
     * 给作业评分
     */
    @Operation(summary = "给作业评分")
    @PutMapping("/score/{id}/{score}")
    public Result scoreSubmission(@PathVariable Long id, @PathVariable Integer score) {
        boolean success = submissionService.update()
                .eq("id", id)
                .set("score", score)
                .update();
        return success ? Result.ok("作业评分成功！") : Result.fail("作业评分失败！");
    }

    /**
     * 根据返回的文件名下载作业附件
     */
    // endregion
    @Operation(summary = "下载作业附件")
    @PostMapping("/download")
    public ResponseEntity<?> downloadSubmission(@RequestBody DownloadSubDTO SubDTO) {
        String fileName = SubDTO.getFileName();
        Long classId = SubDTO.getClassId();
        Long assignmentId = SubDTO.getAssignmentId();
        Long studentId = SubDTO.getStudentId();
        File file = new File(submitDir
                + File.separator + classId
                + File.separator + assignmentId
                + File.separator + studentId
                + File.separator + fileName);
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.fail("文件不存在！"));
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

            // 返回文件流
            byte[] fileBytes = FileCopyUtils.copyToByteArray(inputStream);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.fail("文件下载失败：" + e.getMessage()));
        }
    }


    /**
     * 返回代码文件的原始内容（纯文本）
     */
    @Operation(summary = "预览附件代码(纯文本)")
    @PostMapping("/preview/code")
    public ResponseEntity<String> getCodeContent(@RequestBody DownloadSubDTO SubDTO) {
        String fileName = SubDTO.getFileName();
        Long classId = SubDTO.getClassId();
        Long assignmentId = SubDTO.getAssignmentId();
        Long studentId = SubDTO.getStudentId();
        File file = new File(submitDir
                + File.separator + classId
                + File.separator + assignmentId
                + File.separator + studentId
                + File.separator + fileName);
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("文件不存在！");
        }
        String code;
        try {
            code = Files.readString(file.toPath());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("读取文件出错");
        }
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(code);
    }

    @Operation(summary = "获取指定提交的deepseek评价")
    @GetMapping("/deepseek/{submissionId}")
    public Result getDeepSeek(@PathVariable Long submissionId) {
        DeepseekEvaluation evaluation = deepseekEvaluationMapper.selectOne(new QueryWrapper<DeepseekEvaluation>().eq("submission_id", submissionId));
        return evaluation != null ? Result.ok(evaluation.getEvaluation()) : Result.fail("未获取到deepseek评价,请检查submissionId是否正确");
    }
}
