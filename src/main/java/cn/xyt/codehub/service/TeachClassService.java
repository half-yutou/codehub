package cn.xyt.codehub.service;

import cn.xyt.codehub.dto.TeachClassDTO;
import cn.xyt.codehub.vo.StudentVO;
import cn.xyt.codehub.vo.TeachClassVO;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.xyt.codehub.entity.TeachClass;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TeachClassService extends IService<TeachClass> {
    void flushStudentCount(Long classId);

    List<StudentVO> getStudentsByClassId(Long classId);

    boolean addSingleStudentToClass(Long classId, String studentNumber);

    boolean deleteSingleStudentFromClass(Long classId, Long studentId);

    boolean importStudents(Long classId, MultipartFile file) throws IOException;

    // region TeachClass-CRUD

    boolean addTeachClass(TeachClassDTO teachClassDTO);

    TeachClassVO getTeachClassById(Long id);

    List<TeachClassVO> getTeachClassByTeacherId(Long teacherId);

    List<TeachClassVO> getTeachClassByStudentNumber(Long studentNumber);

    // endregion

}
