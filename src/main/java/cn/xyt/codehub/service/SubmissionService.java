package cn.xyt.codehub.service;

import cn.xyt.codehub.entity.Submission;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SubmissionService extends IService<Submission> {

    void handleFileUpload(MultipartFile[] files, Long assignmentId, Long studentId, Long classId);

    List<Submission> getSubmissionsByClassId(Long classId);

    List<Submission> getSubmissionsByStudentId(Long studentId);

    List<Submission> getSubmissionsByClassIdAndStudentId(Long classId, Long studentId);

    // 辅助方法：处理文件夹返回
    List<String> resolveSubmissionFiles(String filePath);
}
