package cn.xyt.codehub.controller;

import cn.xyt.codehub.dto.Result;
import cn.xyt.codehub.dto.SemesterDTO;
import cn.xyt.codehub.entity.Course;
import cn.xyt.codehub.entity.Semester;
import cn.xyt.codehub.entity.Teacher;
import cn.xyt.codehub.service.AdminService;
import cn.xyt.codehub.service.CourseService;
import cn.xyt.codehub.service.SemesterService;
import cn.xyt.codehub.service.TeacherService;
import cn.xyt.codehub.util.DatabaseBackupUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
    private SemesterService semesterService;

    @Resource
    private CourseService courseService;

    @Resource
    private TeacherService teacherService;

    // region database-backup-methods

    /**
     * 手动触发数据库备份
     * @return 备份文件路径
     */
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


    // endregion

    // region Semester

    /**
     * 创建学期
     * @param semesterDTO 学期对象
     * @return 操作结果
     */
    @PostMapping("/semester/create")
    public Result createSemester(@RequestBody SemesterDTO semesterDTO) {
        semesterService.createSemester(semesterDTO);
        return Result.ok("学期创建成功");
    }

    @GetMapping("/semester/delete/{semesterId}")
    public Result deleteSemester(@PathVariable Long semesterId) {
        semesterService.removeById(semesterId);
        return Result.ok("学期删除成功");
    }

    @PostMapping("/semester/update")
    public Result updateSemester(@RequestBody Semester semester) {
        semesterService.updateById(semester);
        return Result.ok("学期更新成功");
    }

    /**
     * 查询所有学期
     * @return 学期列表
     */
    @GetMapping("/semester/list")
    public Result listSemesters() {
        List<Semester> list = semesterService.list(new QueryWrapper<Semester>().orderByAsc("start_date"));
        return Result.ok(list);
    }

    /**
     * 设置当前学期
     * @param semesterId 学期ID
     * @return 操作结果
     */
    @PutMapping("/semester/set-current/{semesterId}")
    public Result setCurrentSemester(@PathVariable Long semesterId) {
        adminService.setCurrentSemester(semesterId);
        return Result.ok("当前学期设置成功");
    }

    @GetMapping("/semester/reset")
    public Result resetCurrentSemester() {
        adminService.resetCurrentSemester();
        return Result.ok("当前学期重置成功");
    }

    // endregion

    // region course
    /**
     * 设置课程是否需要提交代码
     * @param courseId 课程ID
     * @param isCodeSubmit 是否需要提交代码
     * @return 操作结果
     */
    @PutMapping("/course/set-code-submit/{courseId}")
    public Result setCodeSubmit(@PathVariable Long courseId, @RequestParam boolean isCodeSubmit) {
        return adminService.setCodeSubmit(courseId, isCodeSubmit);
    }

    /**
     * 查询所有课程
     * @return 课程列表
     */
    @GetMapping("/course/list")
    public Result listCourses() {
        List<Course> list = courseService.list(
                new QueryWrapper<Course>().orderByAsc("semester_id"));
        return Result.ok(list);
    }

    // endregion

    // region teacher-manage

    // 新增教师
    @PostMapping("teacher/add")
    public Result addTeacher(@RequestBody Teacher teacher) {
        boolean isSaved = teacherService.save(teacher);
        return isSaved ? Result.ok("新增教师成功") : Result.fail("新增教师失败");
    }

    // 删除教师
    @DeleteMapping("teacher/delete/{id}")
    public Result deleteTeacher(@PathVariable Long id) {
        boolean isRemoved = teacherService.removeById(id);
        return isRemoved ? Result.ok("删除教师成功") : Result.fail("删除教师失败");
    }

    // 修改教师信息
    @PutMapping("teacher/update")
    public Result updateTeacher(@RequestBody Teacher teacher) {
        boolean isUpdated = teacherService.updateById(teacher);
        return isUpdated ? Result.ok("更新教师信息成功") : Result.fail("更新教师信息失败");
    }

    // 查询单个教师信息
    @GetMapping("teacher/get/{id}")
    public Result getTeacher(@PathVariable Long id) {
        Teacher teacher = teacherService.getById(id);
        return teacher != null ? Result.ok(teacher) : Result.fail("教师信息不存在");
    }

    // 查询所有教师信息
    @GetMapping("teacher/list")
    public Result listTeachers() {
        List<Teacher> teachers = teacherService.list();
        return Result.ok(teachers);
    }


    // endregion
}
