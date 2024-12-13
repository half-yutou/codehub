package cn.xyt.codehub.service.impl;

import cn.xyt.codehub.entity.Course;
import cn.xyt.codehub.mapper.CourseMapper;
import cn.xyt.codehub.service.CourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

}
