package cn.xyt.codehub.controller;

import cn.xyt.codehub.dto.Result;
import cn.xyt.codehub.entity.Submission;
import cn.xyt.codehub.service.SubmissionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/submit")
@Tag(name = "作业提交方法")
public class SubmissionController {

    @Resource
    private SubmissionService submissionService;


    // region submission-CRUD-methods

    // Create -> upload
    // 不支持更改,建议重新提交

    /**
     * 删除指定作业
     */
    @Operation(summary = "删除指定提交")
    @DeleteMapping("/delete/{id}")
    public Result deleteAssignment(@PathVariable Long id) {
        return submissionService.removeById(id) ?
                Result.ok("作业提交删除成功！") : Result.fail("作业提交删除失败！");
    }

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
    @Operation(summary = "查询当前作业所有提交")
    @GetMapping("/list/assignment/{assignmentId}")
    public Result listSubmissionsByAssignmentId(@PathVariable Long assignmentId) {
        return Result.ok(submissionService.list(
                new QueryWrapper<Submission>()
                        .eq("assignment_id", assignmentId)));
    }


    // endregion


    // region assignment-submit-methods


    /**
     * 提交作业文件
     */
    @Operation(summary = "上传作业")
    @PostMapping("/upload")
    public Result uploadSubmission(@RequestParam("files") MultipartFile[] files,
                                   @RequestParam("assignmentId") Long assignmentId,
                                   @RequestParam("studentId") Long studentId,
                                   @RequestParam("classId") Long classId) {
        try {
            submissionService.handleFileUpload(files, assignmentId, studentId, classId);
            return Result.ok("作业提交成功！");
        } catch (Exception e) {
            return Result.fail("作业提交失败");
        }
    }

    // endregion
}
