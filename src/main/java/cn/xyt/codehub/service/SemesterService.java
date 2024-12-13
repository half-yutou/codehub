package cn.xyt.codehub.service;

import cn.xyt.codehub.dto.SemesterDTO;
import cn.xyt.codehub.entity.Semester;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SemesterService extends IService<Semester> {
    void createSemester(SemesterDTO semesterDTO);
}
