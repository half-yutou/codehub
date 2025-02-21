package cn.xyt.codehub.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.xyt.codehub.dto.*;
import cn.xyt.codehub.entity.Student;
import cn.xyt.codehub.entity.Teacher;
import cn.xyt.codehub.mapper.TeacherMapper;
import cn.xyt.codehub.service.TeacherService;
import cn.xyt.codehub.util.MD5Util;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    @Resource
    private TeacherMapper teacherMapper;

    @Override
    public Result login(LoginDTO loginDTO) {
        // 从数据库查询有无
        Teacher teacher = getOne(new QueryWrapper<Teacher>()
                .eq("username", loginDTO.getUsername()));

        if (teacher == null || !MD5Util.encrypt(loginDTO.getPassword())
                .equals(teacher.getPassword()))
            return Result.fail("用户名不存在或密码错误");

        // 使用sa-token框架,返回token给前端
        StpUtil.login(teacher.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return Result.ok(tokenInfo);
    }

    @Override
    public Result register(RegisterDTO registerDTO) {
        // 判断数据库中是否有该用户
        Teacher t = getOne(new QueryWrapper<Teacher>()
                .eq("username", registerDTO.getUsername()));
        if (t != null)
            return Result.fail("用户名已存在");
        Teacher teacher = BeanUtil.copyProperties(registerDTO, Teacher.class);
        teacher.setPassword(MD5Util.encrypt(registerDTO.getPassword()));
        return save(teacher) ? Result.ok() : Result.fail("注册失败");
    }

    @Override
    public boolean addTeacher(TeacherDTO teacherDTO) {
        Teacher teacher = BeanUtil.copyProperties(teacherDTO, Teacher.class);
        teacher.setPassword(MD5Util.encrypt(teacherDTO.getPassword()));
        return save(teacher);
    }

    @Override
    public Result changePassword(ChangePasswordDTO changePasswordDTO) {
        Teacher teacher = getOne(new QueryWrapper<Teacher>()
                .eq("username", changePasswordDTO.getUsername()));
        if (teacher == null || !MD5Util.encrypt(changePasswordDTO.getOldPassword())
                .equals(teacher.getPassword()))
            return Result.fail("用户名不存在或旧密码错误");
        teacher.setPassword(MD5Util.encrypt(changePasswordDTO.getNewPassword()));
        return updateById(teacher) ? Result.ok() : Result.fail("修改密码失败");
    }
}
