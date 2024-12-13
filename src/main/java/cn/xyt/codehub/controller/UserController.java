package cn.xyt.codehub.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.xyt.codehub.dto.LoginDTO;
import cn.xyt.codehub.dto.RegisterDTO;
import cn.xyt.codehub.dto.Result;
import cn.xyt.codehub.service.AdminService;
import cn.xyt.codehub.service.StudentService;
import cn.xyt.codehub.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Tag(name = "用户登录")
public class UserController {
    @Resource
    private AdminService adminService;

    @Resource
    private StudentService studentService;

    @Resource
    private TeacherService teacherService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        String role = loginDTO.getRole();
        return switch (role) {
            case "admin" -> adminService.login(loginDTO);
            case "teacher" -> teacherService.login(loginDTO);
            case "student" -> studentService.login(loginDTO);
            default -> Result.fail("请重试");
        };
    }

    @Operation(summary = "用户注册")
    @PostMapping("register")
    public Result register(@RequestBody RegisterDTO registerDTO) {
        String role = registerDTO.getRole();
        return switch (role) {
            case "admin" -> Result.fail("管理员无法注册,请联系其他管理员新增");
            case "teacher" -> teacherService.register(registerDTO);
            case "student" -> studentService.register(registerDTO);
            default -> Result.fail("请重试");
        };
    }

    @Operation(summary = "用户登出")
    @PostMapping("logout")
    public Result logout() {
        StpUtil.logout();
        return Result.ok();
    }
}
