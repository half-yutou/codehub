package cn.xyt.codehub.controller;

import cn.xyt.codehub.dto.Result;
import cn.xyt.codehub.dto.SemesterDTO;
import cn.xyt.codehub.entity.Semester;
import cn.xyt.codehub.service.AdminService;
import cn.xyt.codehub.service.SemesterService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/semester")
@Tag(name = "学期管理")
public class SemesterController {
    @Resource
    private AdminService adminService;

    @Resource
    private SemesterService semesterService;

    // region Semester

    /**
     * 创建学期
     * @param semesterDTO 学期对象
     * @return 操作结果
     */
    @Operation(summary = "创建学期")
    @PostMapping("/create")
    public Result createSemester(@RequestBody SemesterDTO semesterDTO) {
        return semesterService.createSemester(semesterDTO)
                ? Result.ok("学期创建成功")
                : Result.fail("学期创建失败");
    }

    @Operation(summary = "删除学期")
    @GetMapping("/delete/{semesterId}")
    public Result deleteSemester(@PathVariable Long semesterId) {
        semesterService.removeById(semesterId);
        return Result.ok("学期删除成功");
    }

    @Operation(summary = "更新学期")
    @PostMapping("/update")
    public Result updateSemester(@RequestBody Semester semester) {
        semesterService.updateById(semester);
        return Result.ok("学期更新成功");
    }

    /**
     * 查询所有学期
     * @return 学期列表
     */
    @Operation(summary = "查询所有学期")
    @GetMapping("/list")
    public Result listSemesters() {
        List<Semester> list = semesterService.list(new QueryWrapper<Semester>().orderByAsc("start_date"));
        return Result.ok(list);
    }

    /**
     * 设置当前学期
     * @param semesterId 学期ID
     * @return 操作结果
     */
    @Operation(summary = "设置当前学期")
    @PutMapping("/set-current/{semesterId}")
    public Result setCurrentSemester(@PathVariable Long semesterId) {
        adminService.setCurrentSemester(semesterId);
        return Result.ok("当前学期设置成功");
    }

    @Operation(summary = "重置当前学期")
    @GetMapping("/reset")
    public Result resetCurrentSemester() {
        adminService.resetCurrentSemester();
        return Result.ok("当前学期重置成功");
    }

    // endregion
}
