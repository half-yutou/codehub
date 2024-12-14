package cn.xyt.codehub.controller;

import cn.xyt.codehub.dto.Result;
import cn.xyt.codehub.entity.Student;
import cn.xyt.codehub.entity.TeachClass;
import cn.xyt.codehub.service.TeachClassService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/teacher")
public class TeacherController {

    @Resource
    private TeachClassService teachClassService;

    // region teachClass-methods

    // 创建教学班级
    @PostMapping("/class/add")
    public Result addTeachClass(@RequestBody TeachClass teachClass) {
        boolean isSaved = teachClassService.save(teachClass);
        return isSaved ? Result.ok("新增教学班级成功") : Result.fail("新增教学班级失败");
    }

    // 删除教学班级
    @DeleteMapping("/class/delete/{id}")
    public Result deleteTeachClass(@PathVariable Long id) {
        boolean isRemoved = teachClassService.removeById(id);
        return isRemoved ? Result.ok("删除教学班级成功") : Result.fail("删除教学班级失败");
    }

    // 更新教学班级信息
    @PutMapping("/class/update")
    public Result updateTeachClass(@RequestBody TeachClass teachClass) {
        boolean isUpdated = teachClassService.updateById(teachClass);
        return isUpdated ? Result.ok("更新教学班级信息成功") : Result.fail("更新教学班级信息失败");
    }

    // 查询单个教学班级
    @GetMapping("/class/get/{id}")
    public Result getTeachClass(@PathVariable Long id) {
        TeachClass teachClass = teachClassService.getById(id);
        return teachClass != null ? Result.ok(teachClass) : Result.fail("教学班级信息不存在");
    }

    // 查询该教师所有教学班级
    @GetMapping("/class/list")
    public Result listTeachClasses(@PathVariable Long teacherId) {
        List<TeachClass> teachClasses = teachClassService.list(
                new QueryWrapper<TeachClass>().eq("teacher_id", teacherId)
        );
        return Result.ok(teachClasses);
    }

    // endregion


    // region student-manage-methods

    // 获取当前班级所有学生
    @GetMapping("/class/{classId}/students")
    public Result getStudentsByClassId(@PathVariable Long classId) {
        List<Student> students = teachClassService.getStudentsByClassId(classId);
        return Result.ok(students);
    }

    // 新增一个学生到指定班级
    @PutMapping("/class/{classId}/student/add")
    public Result addSingleStudentToClass(@PathVariable Long classId, @RequestBody Student student) {
        return teachClassService.addSingleStudentToClass(classId, student);
    }

    // 删除一个学生在指定班级
    @DeleteMapping("/class/{classId}/student/delete/{studentId}")
    public Result deleteSingleStudentFromClass(@PathVariable Long classId, @PathVariable Long studentId) {
        return teachClassService.deleteSingleStudentFromClass(classId, studentId);
    }

    // 导入Excel批量增加学生
    @PostMapping("/class/{classId}/students/import")
    public Result importStudents(@PathVariable Long classId, @RequestParam("file") MultipartFile file) throws IOException {
        return teachClassService.importStudents(classId, file);
    }



    // endregion
}
