package cn.xyt.codehub.controller;

import cn.xyt.codehub.dto.Result;
import cn.xyt.codehub.entity.Assignment;
import cn.xyt.codehub.entity.Student;
import cn.xyt.codehub.service.AssignmentService;
import cn.xyt.codehub.service.TeachClassService;
import cn.xyt.codehub.util.MailUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assign")
@Tag(name = "作业管理方法")
public class AssignmentController {

    @Resource
    private TeachClassService teachClassService;

    @Resource
    private AssignmentService assignmentService;

    @Resource
    private MailUtil mailUtil;

    // region assignment-CRUD-methods

    /**
     * 创建作业
     */
    @Operation(summary = "创建作业")
    @PostMapping("/add")
    public Result createAssignment(@RequestBody Assignment assignment) {
        boolean success = assignmentService.save(assignment);
        return success ? Result.ok("作业创建成功") : Result.fail("作业创建失败");
    }

    /**
     * 修改作业信息
     */
    @Operation(summary = "修改作业信息")
    @PutMapping("/update")
    public Result updateAssignment(@RequestBody Assignment assignment) {
        boolean success = assignmentService.updateById(assignment);
        return success ? Result.ok("作业修改成功") : Result.fail("作业修改失败");
    }

    /**
     * 删除作业
     */
    @Operation(summary = "删除作业")
    @DeleteMapping("/delete/{id}")
    public Result deleteAssignment(@PathVariable Long id) {
        boolean success = assignmentService.removeById(id);
        return success ? Result.ok("作业删除成功") : Result.fail("作业删除失败");
    }

    /**
     * 查询作业信息
     */
    @Operation(summary = "查询所有作业信息")
    @GetMapping("/list")
    public Result listAssignments() {
        List<Assignment> assignments = assignmentService.list();
        return Result.ok(assignments);
    }


    /**
     * 手动发送作业通知给班级所有学生
     */
    @Operation(summary = "发送作业通知给班级所有学生")
    @PostMapping("/notify")
    public Result notifyStudents(@RequestParam Long classId) {
        // 查询教学班级的所有学生
        List<Student> students = teachClassService.getStudentsByClassId(classId);

        if (students.isEmpty()) {
            return Result.fail("该班级没有学生！");
        }

        // 遍历学生，逐一发送通知
        for (Student student : students) {
            String email = student.getEmail();
            if (email == null || email.isEmpty()) {
                continue; // 如果学生未设置邮箱，则跳过
            }

            try {
                mailUtil.sendMail(
                        email,
                        "新作业通知",
                        "您所在的教学班级有新作业，请及时查看系统。"
                );
            }
            catch (Exception e) {
                return Result.fail("发送失败，当前失败学生为：" + student.getUsername() + " 邮箱：" + email);
            }
        }

        return Result.ok("作业通知已发送！");
    }

    // endregion

}
