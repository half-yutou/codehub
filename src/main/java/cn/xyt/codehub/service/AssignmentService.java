package cn.xyt.codehub.service;

import cn.xyt.codehub.dto.AssignmentDTO;
import cn.xyt.codehub.entity.Assignment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface AssignmentService extends IService<Assignment> {
    boolean addAssignment(AssignmentDTO assignmentDTO);

    List<Assignment> listAssignmentsByStudentId(Long studentId);
}
