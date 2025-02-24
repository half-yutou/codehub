package cn.xyt.codehub.service;

import cn.xyt.codehub.entity.Submission;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SubmissionService extends IService<Submission> {

    void handleFileUpload(MultipartFile[] files, String content, Long assignmentId, Long studentId, Long classId);

    List<Submission> getSubmissionsByClassId(Long classId);

    List<Submission> getSubmissionsByStudentId(Long studentId);

    List<Submission> getSubmissionsByClassIdAndStudentId(Long classId, Long studentId);

    List<Submission> getSubmissionsByStudentIdAndAssignmentId(Long studentId, Long assignmentId);


}
