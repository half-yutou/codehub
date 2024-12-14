package cn.xyt.codehub.service.impl;

import cn.xyt.codehub.entity.StudentClass;
import cn.xyt.codehub.mapper.StudentClassMapper;
import cn.xyt.codehub.service.StudentClassService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class StudentClassServiceImpl extends ServiceImpl<StudentClassMapper, StudentClass> implements StudentClassService {

}
