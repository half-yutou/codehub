package cn.xyt.codehub.controller;

import cn.xyt.codehub.dto.Result;
import cn.xyt.codehub.dto.TeachClassDTO;
import cn.xyt.codehub.entity.Student;
import cn.xyt.codehub.entity.TeachClass;
import cn.xyt.codehub.service.SemesterService;
import cn.xyt.codehub.service.TeachClassService;
import cn.xyt.codehub.vo.TeachClassVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/class")
@Tag(name = "教学班级方法")
public class TeachClassController {

    @Resource
    private TeachClassService teachClassService;

    @Resource
    private SemesterService semesterService;

    // region teachClass-methods

    // 创建教学班级
    @Operation(summary = "创建教学班级")
    @PostMapping("/add")
    public Result addTeachClass(@RequestBody TeachClassDTO teachClassDTO) {
        boolean isSaved = teachClassService.addTeachClass(teachClassDTO);
        return isSaved ? Result.ok("新增教学班级成功") : Result.fail("新增教学班级失败");
    }

    // 删除教学班级
    @Operation(summary = "删除教学班级")
    @DeleteMapping("/delete/{id}")
    public Result deleteTeachClass(@PathVariable Long id) {
        boolean isRemoved = teachClassService.removeById(id);
        return isRemoved ? Result.ok("删除教学班级成功") : Result.fail("删除教学班级失败");
    }

    // 更新教学班级信息
    @Operation(summary = "更新教学班级信息")
    @PutMapping("/update")
    public Result updateTeachClass(@RequestBody TeachClass teachClass) {
        boolean isUpdated = teachClassService.updateById(teachClass);
        return isUpdated ? Result.ok("更新教学班级信息成功") : Result.fail("更新教学班级信息失败");
    }

    // 查询单个教学班级
    @Operation(summary = "根据id查询单个教学班级")
    @GetMapping("/get/{id}")
    public Result getTeachClass(@PathVariable Long id) {
        TeachClassVO teachClassVO = teachClassService.getTeachClassById(id);
        return Result.ok(teachClassVO);
    }

    // 查询教师所有教学班级
    @Operation(summary = "查询该教师所有教学班级")
    @GetMapping("/list/teacher/{teacherId}")
    public Result listTeachClasses(@PathVariable Long teacherId) {
        List<TeachClassVO> teachClassesVO =teachClassService.getTeachClassByTeacherId(teacherId);
        return Result.ok(teachClassesVO);
    }

    // 查询学生所有教学班级
    @Operation(summary = "查询该学生所有教学班级")
    @GetMapping("/list/student/{studentId}")
    public Result listTeachClassesByStudentId(@PathVariable Long studentId) {
        List<TeachClassVO> teachClassesVO =teachClassService.getTeachClassByStudentId(studentId);
        return Result.ok(teachClassesVO);
    }

    // endregion
}
