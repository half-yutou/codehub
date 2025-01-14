package cn.xyt.codehub.service;

import cn.xyt.codehub.entity.Assignment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface AssignmentService extends IService<Assignment> {
    List<Assignment> listAssignmentsByStudentId(Long studentId);
    // 其他需要的自定义方法可以在此定义
}
