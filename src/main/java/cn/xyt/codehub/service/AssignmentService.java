package cn.xyt.codehub.service;

import cn.xyt.codehub.dto.AssignmentDTO;
import cn.xyt.codehub.entity.Assignment;
import cn.xyt.codehub.vo.AssignmentVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface AssignmentService extends IService<Assignment> {
    boolean addAssignment(AssignmentDTO assignmentDTO);

    List<AssignmentVO> listByStudentId(Long studentId);

    List<AssignmentVO> listByClassId(Long classId);

    List<AssignmentVO> listByTeacherId(Long teacherId);

    void fillSubmitCountFromSubmission(AssignmentVO assignmentVO);
}
