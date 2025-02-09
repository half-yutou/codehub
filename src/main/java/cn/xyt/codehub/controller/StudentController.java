package cn.xyt.codehub.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.xyt.codehub.dto.Result;
import cn.xyt.codehub.entity.Student;
import cn.xyt.codehub.service.StudentService;
import cn.xyt.codehub.service.TeachClassService;
import cn.xyt.codehub.vo.StudentVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

    @Resource
    private StudentService studentService;

    // region student-crud-method
    @Operation(summary = "根据学生id获取学生信息")
    @GetMapping("/get/id/{id}")
    public Result getStudentById(@PathVariable Long id) {
        Student student = studentService.getById(id);
        if (student == null) {
            return Result.fail("该学生不存在");
        }
        StudentVO studentVO = BeanUtil.copyProperties(student, StudentVO.class);
        return Result.ok(studentVO);
    }

    @Operation(summary = "根据学生学号获取信息")
    @GetMapping("/get/number/{studentNumber}")
    public Result getStudentByNumber(@PathVariable String studentNumber) {
        Student student = studentService.getOne(new QueryWrapper<Student>().eq("student_number", studentNumber));
        if (student == null) {
            return Result.fail("该学生不存在");
        }
        StudentVO studentVO = BeanUtil.copyProperties(student, StudentVO.class);
        return Result.ok(studentVO);
    }

    // endregion


    // region student-manage-methods

    // 获取指定班级所有学生
    @Operation(summary = "获取指定班级所有学生")
    @GetMapping("/get/{classId}")
    public Result getStudentsByClassId(@PathVariable Long classId) {
        List<StudentVO> studentVOS = teachClassService.getStudentsByClassId(classId);
        return Result.ok(studentVOS);
    }

    // 根据姓名获取指定班级学生
    @Operation(summary = "根据姓名获取某班学生")
    @PostMapping("/get/name/{classId}")
    public Result getStudentsByName(@PathVariable Long classId, @RequestParam String name) {
        List<StudentVO> studentVOSByClassId = teachClassService.getStudentsByClassId(classId);
        StudentVO studentVO = studentVOSByClassId.stream()
                .filter(s -> s.getUsername().equals(name))
                .findFirst()
                .orElse(null);
        return studentVO != null ? Result.ok(studentVO) :Result.fail("该学生不存在");
    }

    // 新增一个学生到指定班级
    @Operation(summary = "新增一个学生到指定班级")
    @PutMapping("add/class/{classId}/{studentNumber}")
    public Result addSingleStudentToClass(@PathVariable Long classId, @PathVariable String studentNumber) {
        return teachClassService.addSingleStudentToClass(classId, studentNumber)
                ? Result.ok("添加成功")
                : Result.fail("添加失败");
    }

    // 删除一个学生在指定班级
    @Operation(summary = "删除一个学生在指定班级")
    @DeleteMapping("/delete/class/{classId}/{studentId}")
    public Result deleteSingleStudentFromClass(@PathVariable Long classId, @PathVariable Long studentId) {
        return teachClassService.deleteSingleStudentFromClass(classId, studentId)
                ? Result.ok("删除成功")
                : Result.fail("删除失败");
    }

    // 导入Excel批量增加学生
    @Operation(summary = "导入Excel批量增加学生")
    @CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
    @PostMapping("/import/class/{classId}")
    public Result importStudents(@PathVariable Long classId, @RequestParam("file") MultipartFile file) throws IOException {
        return teachClassService.importStudents(classId, file)
                ? Result.ok("导入成功")
                : Result.fail("导入失败");
    }

    // endregion
}
