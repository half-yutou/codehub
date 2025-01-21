package cn.xyt.codehub.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.xyt.codehub.dto.AssignmentDTO;
import cn.xyt.codehub.dto.Result;
import cn.xyt.codehub.entity.Assignment;
import cn.xyt.codehub.entity.Student;
import cn.xyt.codehub.service.AssignmentService;
import cn.xyt.codehub.service.TeachClassService;
import cn.xyt.codehub.service.TeacherService;
import cn.xyt.codehub.util.MailUtil;
import cn.xyt.codehub.vo.AssignmentVO;
import cn.xyt.codehub.vo.StudentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/assign")
@Tag(name = "作业管理方法")
public class AssignmentController {

    @Resource
    private TeacherService teacherService;

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
    public Result createAssignment(@RequestBody AssignmentDTO assignmentDTO) {
        boolean success = assignmentService.addAssignment(assignmentDTO);
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
        List<AssignmentVO> assignmentVOS = new ArrayList<>();
        assignments.forEach(assignment -> {
            AssignmentVO assignmentVO = BeanUtil.copyProperties(assignment, AssignmentVO.class);
            assignmentVO.setTeacher(teacherService.getById(assignment.getTeacherId()));
            assignmentService.fillSubmitCountFromSubmission(assignmentVO);
            assignmentVOS.add(assignmentVO);
        });
        return Result.ok(assignmentVOS);
    }

    /**
     * 查询单个作业信息
     */
    @Operation(summary = "查询单个作业信息")
    @GetMapping("/get/{id}")
    public Result getAssignment(@PathVariable Long id) {
        Assignment assignment = assignmentService.getById(id);
        AssignmentVO assignmentVO = BeanUtil.copyProperties(assignment, AssignmentVO.class);
        assignmentVO.setTeacher(teacherService.getById(assignment.getTeacherId()));
        assignmentService.fillSubmitCountFromSubmission(assignmentVO);
        return Result.ok(assignmentVO);
    }

    /**
     * 根据班级ID查询作业列表
     */
    @Operation(summary = "根据班级ID查询作业列表")
    @GetMapping("/list/class/{classId}")
    public Result listAssignmentsByClassId(@PathVariable Long classId) {
        List<AssignmentVO> assignmentVOList = assignmentService.listByClassId(classId);
        return Result.ok(assignmentVOList);
    }

    /**
     * 根据教师ID查询作业列表
     */
    @Operation(summary = "根据教师ID查询作业列表")
    @GetMapping("/list/teacher/{teacherId}")
    public Result listAssignmentsByTeacherId(@PathVariable Long teacherId) {
        List<AssignmentVO> assignmentVOList = assignmentService.listByTeacherId(teacherId);
        return Result.ok(assignmentVOList);
    }

    /**
     * 根据学生ID查询作业列表
     */
    @Operation(summary = "根据学生ID查询作业列表")
    @GetMapping("/list/student/{studentId}")
    public Result listAssignmentsByStudentId(@PathVariable Long studentId) {
        List<AssignmentVO> assignmentVOList = assignmentService.listByStudentId(studentId);
        return Result.ok(assignmentVOList);
    }


    // endregion

    /**
     * 手动发送作业通知给班级所有学生
     */
    @Operation(summary = "发送作业通知给班级所有学生")
    @PostMapping("/notify")
    public Result notifyStudents(@RequestParam Long classId) {
        // 查询教学班级的所有学生
        List<StudentVO> students = teachClassService.getStudentsByClassId(classId);

        if (students.isEmpty()) {
            return Result.fail("该班级没有学生！");
        }

        // 遍历学生，逐一发送通知
        for (StudentVO student : students) {
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

}
