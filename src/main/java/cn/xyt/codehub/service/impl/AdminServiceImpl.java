package cn.xyt.codehub.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.xyt.codehub.dto.LoginDTO;
import cn.xyt.codehub.dto.Result;
import cn.xyt.codehub.dto.SemesterDTO;
import cn.xyt.codehub.entity.Admin;
import cn.xyt.codehub.entity.Course;
import cn.xyt.codehub.entity.Semester;
import cn.xyt.codehub.mapper.AdminMapper;
import cn.xyt.codehub.mapper.CourseMapper;
import cn.xyt.codehub.mapper.SemesterMapper;
import cn.xyt.codehub.service.AdminService;
import cn.xyt.codehub.util.MD5Util;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Resource
    private AdminMapper adminMapper;

    @Resource
    private SemesterMapper semesterMapper;

    @Resource
    private CourseMapper courseMapper;

    @Override
    public Result login(LoginDTO loginDTO) {
        // 从数据库查询有无
        Admin admin = getOne(new QueryWrapper<Admin>()
                .eq("username", loginDTO.getUsername()));

        if (admin == null || !MD5Util.encrypt(loginDTO.getPassword())
                .equals(admin.getPassword()))
            return Result.fail("用户名不存在或密码错误");

        // 使用sa-token框架,返回token给前端
        StpUtil.login(admin.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return Result.ok(tokenInfo);
    }

    // region semester

    /**
     * 设置当前学期
     * @param semesterId 学期ID
     */
    @Override
    public void setCurrentSemester(Long semesterId) {
        // 重置所有学期的当前状态
        semesterMapper.resetCurrentSemester();

        // 设置指定学期为当前学期
        semesterMapper.updateCurrentSemester(semesterId);
    }

    @Override
    public void resetCurrentSemester() {
        semesterMapper.resetCurrentSemester();
    }

    // endregion


    // region course

    /**
     * 设置课程是否需要提交代码
     * @param courseId 课程ID
     * @param isCodeSubmit 是否需要提交代码
     */
    @Override
    public void setCodeSubmit(Long courseId, boolean isCodeSubmit) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("课程不存在！");
        }
        course.setIsCodeSubmit(isCodeSubmit ? 1 : 0);
        courseMapper.updateById(course);
    }

    // endregion

}
