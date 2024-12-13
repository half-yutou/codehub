package cn.xyt.codehub.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.xyt.codehub.dto.LoginDTO;
import cn.xyt.codehub.dto.RegisterDTO;
import cn.xyt.codehub.dto.Result;
import cn.xyt.codehub.entity.Admin;
import cn.xyt.codehub.entity.Student;
import cn.xyt.codehub.entity.Teacher;
import cn.xyt.codehub.mapper.StudentMapper;
import cn.xyt.codehub.service.StudentService;
import cn.xyt.codehub.util.MD5Util;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    @Resource
    private StudentMapper studentMapper;

    @Override
    public Result login(LoginDTO loginDTO) {
        // 从数据库查询有无
        Student student = getOne(new QueryWrapper<Student>()
                .eq("username", loginDTO.getUsername()));

        if (student == null || !MD5Util.encrypt(loginDTO.getPassword())
                .equals(student.getPassword()))
            return Result.fail("用户名不存在或密码错误");

        // 使用sa-token框架,返回token给前端
        StpUtil.login(student.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return Result.ok(tokenInfo);
    }

    @Override
    public Result register(RegisterDTO registerDTO) {
        Student student = BeanUtil.copyProperties(registerDTO, Student.class);
        student.setPassword(MD5Util.encrypt(registerDTO.getPassword()));
        return save(student) ? Result.ok() : Result.fail("注册失败");
    }
}
