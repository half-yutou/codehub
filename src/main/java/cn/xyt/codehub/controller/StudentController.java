package cn.xyt.codehub.controller;

import cn.xyt.codehub.dto.Result;
import cn.xyt.codehub.entity.Student;
import cn.xyt.codehub.service.TeachClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/student")
@Tag(name = "教学班学生管理方法")
public class StudentController {

    @Resource
    private TeachClassService teachClassService;

    // region student-manage-methods

    // 获取指定班级所有学生
    @Operation(summary = "获取指定班级所有学生")
    @GetMapping("/get/{classId}")
    public Result getStudentsByClassId(@PathVariable Long classId) {
        List<Student> students = teachClassService.getStudentsByClassId(classId);
        return Result.ok(students);
    }

    // 新增一个学生到指定班级
    @Operation(summary = "新增一个学生到指定班级")
    @PutMapping("add/class/{classId}")
    public Result addSingleStudentToClass(@PathVariable Long classId, @RequestBody Student student) {
        return teachClassService.addSingleStudentToClass(classId, student);
    }

    // 删除一个学生在指定班级
    @Operation(summary = "删除一个学生在指定班级")
    @DeleteMapping("/delete/class/{classId}/{studentId}")
    public Result deleteSingleStudentFromClass(@PathVariable Long classId, @PathVariable Long studentId) {
        return teachClassService.deleteSingleStudentFromClass(classId, studentId);
    }

    // 导入Excel批量增加学生
    @Operation(summary = "导入Excel批量增加学生")
    @PostMapping("/import/class/{classId}")
    public Result importStudents(@PathVariable Long classId, @RequestParam("file") MultipartFile file) throws IOException {
        return teachClassService.importStudents(classId, file);
    }

    // endregion
}
