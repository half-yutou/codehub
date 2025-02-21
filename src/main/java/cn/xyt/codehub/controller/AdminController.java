package cn.xyt.codehub.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.xyt.codehub.dto.*;
import cn.xyt.codehub.entity.Course;
import cn.xyt.codehub.entity.Semester;
import cn.xyt.codehub.entity.Teacher;
import cn.xyt.codehub.service.AdminService;
import cn.xyt.codehub.service.CourseService;
import cn.xyt.codehub.service.SemesterService;
import cn.xyt.codehub.service.TeacherService;
import cn.xyt.codehub.task.DynamicTaskService;
import cn.xyt.codehub.util.DatabaseBackupUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/admin")
@Tag(name = "管理员方法")
public class AdminController {

    // region database-backup-attributes

    @Value("${task.database.host}")
    private String host;

    @Value("${task.database.port}")
    private String port;

    @Value("${task.database.username}")
    private String username;

    @Value("${task.database.password}")
    private String password;

    @Value("${task.database.name}")
    private String database;

    @Value("${task.backup.dir}")
    private String backupDir;

    // endregion

    @Resource
    private AdminService adminService;

    @Resource
    private CourseService courseService;

    @Resource
    private DynamicTaskService dynamicTaskService;

    @Resource
    private TeacherService teacherService;


    // region database-backup-methods


    /**
     * 手动触发数据库备份
     * @return 备份文件路径
     */
    @Operation(summary = "手动触发数据库备份")
    @GetMapping("/backup")
    public Result manualBackup() {
        try {
            String backupFile = DatabaseBackupUtil.backupDatabase(host, port, username, password, database, backupDir);
            return Result.ok(backupFile);
        } catch (IOException e) {
            return Result.fail("备份失败");
        }
    }

    /**
     * 下载备份文件
     * @param fileName 文件名
     * @return 文件内容
     */
    @Operation(summary = "下载备份文件")
    @GetMapping("/backup/download/{fileName}")
    public ResponseEntity<?> downloadBackup(@PathVariable String fileName) {
        File file = new File(backupDir + File.separator + fileName);
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
     * 查询所有备份文件名
     * @return 所有备份文件名列表
     */
    @Operation(summary = "查询所有备份文件名")
    @GetMapping("/backup/list")
    public Result listBackupFiles() {
        File dir = new File(backupDir);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("备份目录不存在或不是一个有效的目录！");
        }

        // 获取备份目录下的所有文件名
        String[] files = dir.list();
        if (files == null || files.length == 0) {
            return Result.ok(new ArrayList<>()); // 没有备份文件，返回空列表
        }

        // 按文件名排序（文件带时间前缀,倒叙使得最新文件在前）
        List<String> fileList = Arrays.stream(files)
                                    .sorted(Comparator.reverseOrder())
                                    .toList();

        return Result.ok(fileList);
    }

    /**
     * 更改自动备份时间
     */
    @Operation(summary = "更改自动备份时间")
    @PostMapping("/updateCron")
    public Result updateCron(@RequestParam int hour, @RequestParam int min) {
        // 简单验证参数合法性
        if (hour < 0 || hour > 23 || min < 0 || min > 59) {
            return Result.fail("小时必须在0-23之间，分钟必须在0-59之间");
        }
        // 构造 cron 表达式，格式为：秒 分 时 日 月 星期
        String cron = String.format("0 %d %d * * ?", min, hour);
        dynamicTaskService.updateCronExpression(cron);
        return Result.ok();
    }


    // endregion



    // region course
    /**
     * 设置课程是否需要提交代码
     * @param courseId 课程ID
     * @param isCodeSubmit 是否需要提交代码
     * @return 操作结果
     */
    @Operation(summary = "设置课程是否需要提交代码")
    @PutMapping("/course/set-code-submit/{courseId}")
    public Result setCodeSubmit(@PathVariable Long courseId, @RequestParam boolean isCodeSubmit) {
        return adminService.setCodeSubmit(courseId, isCodeSubmit)
                ? Result.ok("设置成功")
                : Result.fail("设置失败");
    }

    /**
     * 查询所有课程
     * @return 课程列表
     */
    @Operation(summary = "查询所有课程")
    @GetMapping("/course/list")
    public Result listCourses() {
        List<Course> list = courseService.list(
                new QueryWrapper<Course>().orderByAsc("semester_id"));
        return Result.ok(list);
    }

    /**
     * 添加课程
     * @param courseDTO 课程对象
     */
    @Operation(summary = "添加课程")
    @PostMapping("/course/add")
    public Result addCourse(@RequestBody CourseDTO courseDTO) {
        Course course = BeanUtil.copyProperties(courseDTO, Course.class);
        boolean result = courseService.save(course);
        return result ? Result.ok("添加成功") : Result.fail("添加失败");
    }

    /**
     * 删除课程
     * @param id 课程ID
     */
    @Operation(summary = "删除课程")
    @DeleteMapping("/course/delete/{id}")
    public Result deleteCourse(@PathVariable Long id) {
        boolean result = courseService.removeById(id);
        return result ? Result.ok("删除成功") : Result.fail("删除失败");
    }

    /**
     *  管理教师信息
     */
    @Operation(summary = "管理教师信息")
    @PostMapping("/teacher/manage")
    // 姓名 电话 邮箱 学院
    public Result manageTeacher(@RequestBody TeacherManageDTO teacherDTO) {
        // 从数据库获取用户
        Teacher teacher = teacherService.getById(teacherDTO.getId());
        if (teacher == null) return Result.fail("用户不存在");
        if (!StrUtil.isBlank(teacherDTO.getUsername())) teacher.setUsername(teacherDTO.getUsername());
        if (!StrUtil.isBlank(teacherDTO.getPhone())) teacher.setPhone(teacherDTO.getPhone());
        if (!StrUtil.isBlank(teacherDTO.getEmail())) teacher.setEmail(teacherDTO.getEmail());
        if (!StrUtil.isBlank(teacherDTO.getDepartment())) teacher.setDepartment(teacherDTO.getDepartment());

        boolean result = teacherService.updateById(teacher);
        return result ? Result.ok("添加成功") : Result.fail("添加失败");
    }

    // endregion


}
