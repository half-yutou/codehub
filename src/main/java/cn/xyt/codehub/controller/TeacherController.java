package cn.xyt.codehub.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.xyt.codehub.dto.Result;
import cn.xyt.codehub.dto.TeacherDTO;
import cn.xyt.codehub.entity.Assignment;
import cn.xyt.codehub.entity.Student;
import cn.xyt.codehub.entity.TeachClass;
import cn.xyt.codehub.entity.Teacher;
import cn.xyt.codehub.service.AssignmentService;
import cn.xyt.codehub.service.TeachClassService;
import cn.xyt.codehub.service.TeacherService;
import cn.xyt.codehub.util.MailUtil;
import cn.xyt.codehub.vo.TeacherVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teacher")
@Tag(name = "教师方法")
public class TeacherController {

    @Resource
    private TeacherService teacherService;



    // region teacher-manage

    // 新增教师
    @Operation(summary = "新增教师")
    @PostMapping("/add")
    public Result addTeacher(@RequestBody @Validated TeacherDTO teacherDTO) {
        boolean isSaved = teacherService.addTeacher(teacherDTO);
        return isSaved ? Result.ok("新增教师成功") : Result.fail("新增教师失败");
    }

    // 删除教师
    @Operation(summary = "删除教师")
    @DeleteMapping("/delete/{id}")
    public Result deleteTeacher(@PathVariable Long id) {
        boolean isRemoved = teacherService.removeById(id);
        return isRemoved ? Result.ok("删除教师成功") : Result.fail("删除教师失败");
    }

    // 修改教师信息
    @Operation(summary = "修改教师信息")
    @PutMapping("/update")
    public Result updateTeacher(@RequestBody Teacher teacher) {
        boolean isUpdated = teacherService.updateById(teacher);
        return isUpdated ? Result.ok("更新教师信息成功") : Result.fail("更新教师信息失败");
    }

    // 查询单个教师信息
    @Operation(summary = "根据ID查询单个教师信息")
    @GetMapping("/get/{id}")
    public Result getTeacher(@PathVariable Long id) {
        Teacher teacher = teacherService.getById(id);
        if (teacher == null) {
            return Result.fail("教师不存在");
        }
        TeacherVO teacherVO = BeanUtil.copyProperties(teacher, TeacherVO.class);
        return Result.ok(teacherVO);
    }

    // 根据教师名称查询教师信息
    @Operation(summary = "根据教师名称查询教师信息")
    @GetMapping("/get/name")
    public Result getTeacherByName(@RequestParam String name) {
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", name);
        Teacher teacher = teacherService.getOne(queryWrapper);
        if (teacher == null) {
            return Result.fail("教师不存在");
        }
        TeacherVO teacherVO = BeanUtil.copyProperties(teacher, TeacherVO.class);
        return Result.ok(teacherVO);
    }

    // 查询所有教师信息
    @Operation(summary = "查询所有教师信息")
    @GetMapping("/list")
    public Result listTeachers() {
        List<Teacher> teachers = teacherService.list();
        List<TeacherVO> teacherVOS = teachers.stream()
                .map(teacher -> BeanUtil.copyProperties(teacher, TeacherVO.class))
                .toList();
        return Result.ok(teacherVOS);
    }


    // endregion

}
