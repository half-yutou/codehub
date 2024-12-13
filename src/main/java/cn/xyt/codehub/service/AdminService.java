package cn.xyt.codehub.service;

import cn.xyt.codehub.dto.LoginDTO;
import cn.xyt.codehub.dto.Result;
import cn.xyt.codehub.dto.SemesterDTO;
import cn.xyt.codehub.entity.Admin;
import cn.xyt.codehub.entity.Course;
import cn.xyt.codehub.entity.Semester;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface AdminService extends IService<Admin> {
    Result login(LoginDTO loginDTO);

    // region semester

    void setCurrentSemester(Long semesterId);   // 设置当前学期


    void resetCurrentSemester();

    // endregion


    // region course

    void setCodeSubmit(Long courseId, boolean isCodeSubmit); // 设置是否需要提交代码

    // endregion
}
