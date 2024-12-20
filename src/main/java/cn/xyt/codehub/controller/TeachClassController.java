package cn.xyt.codehub.controller;

import cn.xyt.codehub.dto.Result;
import cn.xyt.codehub.entity.Student;
import cn.xyt.codehub.entity.TeachClass;
import cn.xyt.codehub.service.TeachClassService;
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

    // region teachClass-methods

    // 创建教学班级
    @Operation(summary = "创建教学班级")
    @PostMapping("/add")
    public Result addTeachClass(@RequestBody TeachClass teachClass) {
        boolean isSaved = teachClassService.save(teachClass);
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
    @Operation(summary = "查询单个教学班级")
    @GetMapping("/get/{id}")
    public Result getTeachClass(@PathVariable Long id) {
        TeachClass teachClass = teachClassService.getById(id);
        return teachClass != null ? Result.ok(teachClass) : Result.fail("教学班级信息不存在");
    }

    // 查询该教师所有教学班级
    @Operation(summary = "查询该教师所有教学班级")
    @GetMapping("/list/{teacherId}")
    public Result listTeachClasses(@PathVariable Long teacherId) {
        List<TeachClass> teachClasses = teachClassService.list(
                new QueryWrapper<TeachClass>().eq("teacher_id", teacherId)
        );
        return Result.ok(teachClasses);
    }

    // endregion
}
