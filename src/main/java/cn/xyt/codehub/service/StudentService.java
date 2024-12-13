package cn.xyt.codehub.service;

import cn.xyt.codehub.dto.LoginDTO;
import cn.xyt.codehub.dto.RegisterDTO;
import cn.xyt.codehub.dto.Result;
import cn.xyt.codehub.entity.Student;
import com.baomidou.mybatisplus.extension.service.IService;

public interface StudentService extends IService<Student> {
    Result login(LoginDTO loginDTO);

    Result register(RegisterDTO registerDTO);
}
