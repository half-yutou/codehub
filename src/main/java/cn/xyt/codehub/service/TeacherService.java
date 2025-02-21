package cn.xyt.codehub.service;

import cn.xyt.codehub.dto.*;
import cn.xyt.codehub.entity.Teacher;
import com.baomidou.mybatisplus.extension.service.IService;

public interface TeacherService extends IService<Teacher> {
    Result login(LoginDTO loginDTO);

    Result register(RegisterDTO registerDTO);

    boolean addTeacher(TeacherDTO teacherDTO);

    Result changePassword(ChangePasswordDTO changePasswordDTO);
}
