package cn.xyt.codehub.service.impl;

import cn.xyt.codehub.entity.Assignment;
import cn.xyt.codehub.entity.StudentClass;
import cn.xyt.codehub.mapper.AssignmentMapper;
import cn.xyt.codehub.mapper.StudentClassMapper;
import cn.xyt.codehub.service.AssignmentService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssignmentServiceImpl extends ServiceImpl<AssignmentMapper, Assignment> implements AssignmentService {

    @Resource
    private StudentClassMapper studentClassMapper;

    @Override
    public List<Assignment> listAssignmentsByStudentId(Long studentId) {
        // 先根据studentId查询其所在所有班级
        List<StudentClass> sc = studentClassMapper.selectList(
                new QueryWrapper<StudentClass>().eq("student_id", studentId));
        if (sc.isEmpty()) {
            return List.of();
        }
        // 再根据班级返回其所有作业
        List<Assignment> as = new ArrayList<>();
        sc.forEach(s -> {
            Long classId = s.getClassId();
            as.addAll(list(new QueryWrapper<Assignment>().eq("class_id", classId)));
        });
        return as;
    }
}
