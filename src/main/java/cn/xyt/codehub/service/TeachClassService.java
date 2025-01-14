package cn.xyt.codehub.service;

import cn.xyt.codehub.dto.Result;
import cn.xyt.codehub.entity.Student;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.xyt.codehub.entity.TeachClass;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TeachClassService extends IService<TeachClass> {
    void flushStudentCount(Long classId);

    List<Student> getStudentsByClassId(Long classId);

    boolean addSingleStudentToClass(Long classId, Student student);

    boolean deleteSingleStudentFromClass(Long classId, Long studentId);

    boolean importStudents(Long classId, MultipartFile file) throws IOException;

}
