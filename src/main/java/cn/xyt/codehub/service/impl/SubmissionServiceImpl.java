package cn.xyt.codehub.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.xyt.codehub.dto.CodeReviewDTO;
import cn.xyt.codehub.entity.Assignment;
import cn.xyt.codehub.entity.CodeReviewReport;
import cn.xyt.codehub.entity.DeepseekEvaluation;
import cn.xyt.codehub.entity.Submission;
import cn.xyt.codehub.mapper.CodeReviewReportMapper;
import cn.xyt.codehub.mapper.DeepseekEvaluationMapper;
import cn.xyt.codehub.mapper.SubmissionMapper;
import cn.xyt.codehub.service.*;
import cn.xyt.codehub.util.CodeReviewUtil;
import cn.xyt.codehub.util.DeepSeekUtil;
import cn.xyt.codehub.util.MailUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@SuppressWarnings("ResultOfMethodCallIgnored")
@Service
public class SubmissionServiceImpl extends ServiceImpl<SubmissionMapper, Submission> implements SubmissionService {

    @Value("${submit.dir}")
    private String submitPath;

    @Value("${submit.review.command}")
    private String command; // 可执行文件的路径

    @Value("${deepseek.apiKey}")
    private String apiKey;


    Logger logger = LoggerFactory.getLogger(SubmissionServiceImpl.class);

    @Resource
    private CodeReviewReportMapper codeReviewReportMapper;

    @Resource
    private SubmissionMapper submissionMapper;

    @Resource
    private MailUtil mailUtil;

    private final Executor executor = Executors.newFixedThreadPool(10);
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private TeachClassService teachClassService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private AssignmentService assignmentService;
    @Autowired
    private DeepseekEvaluationMapper deepseekEvaluationMapper;

    // region upload

    @Override
    public void handleFileUpload(MultipartFile[] files, String content, Long assignmentId, Long studentId, Long classId) {
        // 创建存储目录
        File studentDir = getFile(assignmentId, studentId, classId);
        // 清空之前的提交记录
        this.remove(new QueryWrapper<Submission>()
                .eq("student_id", studentId)
                .eq("assignment_id", assignmentId)
                .eq("class_id", classId));

        for (MultipartFile multipartFile : files) {
            try {
                // 判断文件类型是否为压缩文件
                if (isZipFile(multipartFile)) {
                    // 解压缩文件并保存
                    unzipFile(multipartFile, studentDir, "GBK", studentId, assignmentId, classId, content);
                } else {
                    // 普通文件保存
                    File destination = new File(studentDir, multipartFile.getOriginalFilename());
                    multipartFile.transferTo(destination);
                    saveSubmitInfo(destination.getName(), content, studentId, assignmentId, classId);
                }
            } catch (IOException e) {
                throw new RuntimeException("文件上传失败: " + multipartFile.getOriginalFilename());
            }
        }
    }

    private File getFile(Long assignmentId, Long studentId, Long classId) {
        File classDir = new File(submitPath, classId.toString());
        if (!classDir.exists()) {
            classDir.mkdirs();
        }

        File assignmentDir = new File(classDir, assignmentId.toString());
        if (!assignmentDir.exists()) {
            assignmentDir.mkdirs();
        }

        File studentDir = new File(assignmentDir, studentId.toString());
        if (studentDir.exists()) {
            // 清空学生目录下的所有文件
            for (File file : studentDir.listFiles()) {
                file.delete();
            }
        } else {
            studentDir.mkdirs();
        }
        return studentDir;
    }

    public void unzipFile(MultipartFile zipFile, File destination, String charsetName, Long studentId, Long assignmentId, Long classId, String content) throws IOException {
        // 确保目标文件夹存在
        if (!destination.exists() && !destination.mkdirs()) {
            throw new IOException("Failed to create destination directory: " + destination.getAbsolutePath());
        }

        // 使用指定字符集读取 ZIP 文件
        try (ZipFile zip = new ZipFile(convertToFile(zipFile), Charset.forName(charsetName))) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File newFile = new File(destination, entry.getName());

                // 检查路径安全性
                if (!newFile.getCanonicalPath().startsWith(destination.getCanonicalPath() + File.separator)) {
                    throw new IOException("Entry is outside of the target dir: " + entry.getName());
                }

                // 解压文件
                File parentDir = newFile.getParentFile();
                if (!parentDir.exists() && !parentDir.mkdirs()) {
                    throw new IOException("Failed to create directory: " + parentDir.getAbsolutePath());
                }

                try (InputStream is = zip.getInputStream(entry);
                     BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newFile))) {
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = is.read(buffer)) > 0) {
                        bos.write(buffer, 0, len);
                    }
                }
                saveSubmitInfo(newFile.getName(), content, studentId, assignmentId, classId);
            }
        }
    }

    private boolean isZipFile(MultipartFile file) {
        String name = file.getOriginalFilename();
        return name.endsWith(".zip") || name.endsWith(".ZIP");
    }

    // 辅助方法：将 MultipartFile 转为 File
    private File convertToFile(MultipartFile multipartFile) throws IOException {
        File convFile = File.createTempFile("upload", ".zip");
        multipartFile.transferTo(convFile);
        return convFile;
    }


    private void saveSubmitInfo(String fileName, String content, Long studentId, Long assignmentId, Long classId) {
        Submission submission = new Submission();
        submission.setAssignmentId(assignmentId);
        submission.setStudentId(studentId);
        submission.setClassId(classId);
        submission.setFilename(fileName);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setStatus("查重中");
        submission.setContent(content);
        this.save(submission);
        this.codeReview(fileName, assignmentId, studentId, classId);


        CompletableFuture.runAsync(() -> {
            Submission curSubmission = this.getOne(new QueryWrapper<Submission>().eq("assignment_id", assignmentId).eq("student_id", studentId).eq("class_id", classId));
            String deepseeked = deepseekCheck(assignmentId, content);
            // 将结果存到数据表deepseek (id, submissionId, evaluation)
            deepseekEvaluationMapper.insert(new DeepseekEvaluation(null, curSubmission.getId(), deepseeked));
        }, executor);

    }

    private String deepseekCheck(Long assignmentId, String content) {
        // 先从数据库获取assign信息
        Assignment assignment = assignmentService.getById(assignmentId);
        String description = assignment.getDescription();
        String res = "作业描述" + description + "\n学生提交内容" + content;
        DeepSeekUtil deepSeekUtil = new DeepSeekUtil();
        return deepSeekUtil.chatCompletion(apiKey, res);
    }

    private void codeReview(String fileName, Long assignmentId, Long studentId, Long classId) {
        CompletableFuture.supplyAsync(() -> {
            // 拿到文件父目录:submitDir + classID + assignmentID
            File parentDir = new File(submitPath, classId + File.separator + assignmentId);
            // 遍历该目录下其他学生的提交目录,并遍历该目录下所有文件
            if (!parentDir.isDirectory()) {
                throw new RuntimeException("Invalid parent directory: " + parentDir);
            }
            for (File studentDir : parentDir.listFiles()) {
                if (studentDir.getName().equals(studentId.toString())) continue;
                for (File otherStudentFile : studentDir.listFiles()) {
                    if (otherStudentFile.getName().endsWith(".java")) {
                            try {
                                CodeReviewDTO codeReviewDTO = CodeReviewUtil.codeReview(
                                        command,
                                        new File(parentDir, studentId + File.separator + fileName).getAbsolutePath(),
                                        otherStudentFile.getAbsolutePath());
                                if (codeReviewDTO.isOverThreshold()) {
                                    // 将查重不通过的两份提交id根据作业id和学生id和文件名查出来
                                    Submission the = getOne(new QueryWrapper<Submission>()
                                            .eq("assignment_id", assignmentId)
                                            .eq("student_id", studentId)
                                            .eq("class_id", classId)
                                            .eq("filename", fileName));
                                    Submission another = getOne(new QueryWrapper<Submission>()
                                            .eq("assignment_id", assignmentId)
                                            .eq("student_id", Long.parseLong(studentDir.getName()))
                                            .eq("class_id", classId)
                                            .eq("filename", otherStudentFile.getName()));
                                    if (the == null || another == null) throw new RuntimeException("作业提交信息缺失,查重失败");
                                    codeReviewDTO.setStudentId(studentId);
                                    codeReviewDTO.setSubmissionId(the.getId());
                                    codeReviewDTO.setAnotherSubmissionId(another.getId());
                                    return codeReviewDTO;
                                }
                            } catch (IOException | InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                    }
                }
            }
            CodeReviewDTO pass = CodeReviewDTO.pass();
            pass.setStudentId(studentId);
            return pass;
        }, executor).thenAccept(dto -> {
            if (!dto.isOverThreshold()) {
                logger.debug("进入 dto.isOverThreshold() == false");
                // 修改该提交状态为查重通过
                this.update().eq("student_id", studentId)
                        .eq("assignment_id", assignmentId)
                        .eq("class_id", classId)
                        .set("status", "查重通过")
                        .update();
                // 将过往不通过的记录删除
                codeReviewReportMapper.delete(new QueryWrapper<CodeReviewReport>()
                        .eq("student_id", studentId));
                return ;
            }
            // 修改为查重不通过
            this.update().eq("student_id", studentId)
                    .eq("assignment_id", assignmentId)
                    .eq("class_id", classId)
                    .set("status", "查重不通过")
                    .update();

            // 并且将报告记录于code_review_report表中
            CodeReviewReport codeReviewReport = BeanUtil.copyProperties(dto, CodeReviewReport.class);
            codeReviewReportMapper.insert(codeReviewReport);
            // 并且将报告发送给教师邮箱
            Long teacherId = teachClassService.getById(classId).getTeacherId();
            String email = teacherService.getById(teacherId).getEmail();
            mailUtil.sendMail(
                    email,
                    "查重报告",
                    "学号" + studentService.getById(studentId).getStudentNumber() + "的学生的提交" + fileName + "被查重不通过,请及时查看");
        }).exceptionally(e -> {
            logger.error("codeReview 异常: {}", e.getMessage());
            return null;
        });
    }


    // endregion


    // region download


    @Override
    public List<Submission> getSubmissionsByClassId(Long classId) {
        return baseMapper.getSubmissionsByClassId(classId);
    }

    @Override
    public List<Submission> getSubmissionsByStudentId(Long studentId) {
        return baseMapper.getSubmissionsByStudentId(studentId);
    }

    @Override
    public List<Submission> getSubmissionsByClassIdAndStudentId(Long classId, Long studentId) {
        return baseMapper.getSubmissionsByClassIdAndStudentId(classId, studentId);
    }

    @Override
    public List<Submission> getSubmissionsByStudentIdAndAssignmentId(Long studentId, Long assignmentId) {
        return list(new QueryWrapper<Submission>()
                .eq("student_id", studentId)
                .eq("assignment_id", assignmentId));
    }


    // endregion
}
