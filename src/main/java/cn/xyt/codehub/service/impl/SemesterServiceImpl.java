package cn.xyt.codehub.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.xyt.codehub.dto.SemesterDTO;
import cn.xyt.codehub.entity.Semester;
import cn.xyt.codehub.mapper.SemesterMapper;
import cn.xyt.codehub.service.SemesterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SemesterServiceImpl extends ServiceImpl<SemesterMapper, Semester> implements SemesterService {
    @Override
    public boolean createSemester(SemesterDTO semesterDTO) {
        return save(BeanUtil.copyProperties(semesterDTO, Semester.class));
    }
}
