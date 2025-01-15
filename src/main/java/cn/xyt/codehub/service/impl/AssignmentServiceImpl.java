package cn.xyt.codehub.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.xyt.codehub.dto.AssignmentDTO;
import cn.xyt.codehub.entity.Assignment;
import cn.xyt.codehub.entity.Student;
import cn.xyt.codehub.entity.StudentClass;
import cn.xyt.codehub.mapper.AssignmentMapper;
import cn.xyt.codehub.mapper.StudentClassMapper;
import cn.xyt.codehub.mapper.StudentMapper;
import cn.xyt.codehub.service.AssignmentService;
import cn.xyt.codehub.service.TeacherService;
import cn.xyt.codehub.vo.AssignmentVO;
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
    private TeacherService teacherService;

    @Resource
    private StudentClassMapper studentClassMapper;

    @Resource
    private StudentMapper studentMapper;

    @Override
    public boolean addAssignment(AssignmentDTO assignmentDTO) {
        return save(BeanUtil.copyProperties(assignmentDTO, Assignment.class));
    }

    @Override
    public List<AssignmentVO> listByStudentId(Long studentId) {
        // 先根据studentId查询其studentNumber
        Student student = studentMapper.selectOne(new QueryWrapper<Student>().eq("id", studentId));
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }
        String studentNumber = student.getStudentNumber();
        // 先根据studentId查询其所在所有班级
        List<StudentClass> sc = studentClassMapper.selectList(
                new QueryWrapper<StudentClass>().eq("student_number", studentNumber));
        if (sc.isEmpty()) {
            return List.of();
        }
        // 再根据班级返回其所有作业
        List<AssignmentVO> as = new ArrayList<>();
        sc.forEach(s -> {
            Long classId = s.getClassId();
            List<Assignment> assignments = list(new QueryWrapper<Assignment>().eq("class_id", classId));
            ArrayList<AssignmentVO> assignmentVOS = new ArrayList<>();
            assignments.forEach(assignment -> {
                AssignmentVO assignmentVO = BeanUtil.copyProperties(assignment, AssignmentVO.class);
                assignmentVO.setTeacher(teacherService.getById(assignment.getTeacherId()));
                assignmentVOS.add(assignmentVO);
            });
            as.addAll(assignmentVOS);
        });
        return as;
    }

    @Override
    public List<AssignmentVO> listByClassId(Long classId) {
        List<Assignment> assignments = list(new QueryWrapper<Assignment>().eq("class_id", classId));
        ArrayList<AssignmentVO> assignmentVOS = new ArrayList<>();
        assignments.forEach(assignment -> {
            AssignmentVO assignmentVO = BeanUtil.copyProperties(assignment, AssignmentVO.class);
            assignmentVO.setTeacher(teacherService.getById(assignment.getTeacherId()));
            assignmentVOS.add(assignmentVO);
        });
        return assignmentVOS;
    }

    @Override
    public List<AssignmentVO> listByTeacherId(Long teacherId) {
        List<Assignment> assignments = list(new QueryWrapper<Assignment>().eq("teacher_id", teacherId));
        ArrayList<AssignmentVO> assignmentVOS = new ArrayList<>();
        assignments.forEach(assignment -> {
            AssignmentVO assignmentVO = BeanUtil.copyProperties(assignment, AssignmentVO.class);
            assignmentVO.setTeacher(teacherService.getById(teacherId));
            assignmentVOS.add(assignmentVO);
        });
        return assignmentVOS;
    }


}
