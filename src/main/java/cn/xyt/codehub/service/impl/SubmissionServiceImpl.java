package cn.xyt.codehub.service.impl;

import cn.xyt.codehub.entity.Submission;
import cn.xyt.codehub.mapper.SubmissionMapper;
import cn.xyt.codehub.service.SubmissionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@SuppressWarnings("ResultOfMethodCallIgnored")
@Service
public class SubmissionServiceImpl extends ServiceImpl<SubmissionMapper, Submission> implements SubmissionService {

    @Value("${submit.dir}")
    private String submitPath;

    // region upload

    @Override
    public void handleFileUpload(MultipartFile[] files, Long assignmentId, Long studentId, Long classId) {
        // 创建存储目录
        File directory = new File(submitPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        for (MultipartFile file : files) {
            try {
                // 判断文件类型是否为压缩文件
                if (isZipFile(file)) {
                    // 解压缩文件
                    File zipDestination = new File(submitPath, generateUniqueDir(assignmentId, studentId));
                    unzipFile(file, zipDestination, "GBK");

                    // 遍历解压后的文件并保存
                    File[] extractedFiles = zipDestination.listFiles();
                    if (extractedFiles != null) {
                        for (File extractedFile : extractedFiles) {
                            saveFileInfo(extractedFile, assignmentId, studentId, classId);
                        }
                    }
                } else {
                    // 普通文件保存
                    File destination = new File(directory, generateUniqueFileName(file.getOriginalFilename(), studentId));
                    file.transferTo(destination);
                    saveFileInfo(destination, assignmentId, studentId, classId);
                }
            } catch (IOException e) {
                throw new RuntimeException("文件上传失败: " + file.getOriginalFilename());
            }
        }
    }

    private boolean isZipFile(MultipartFile file) {
        String name = file.getOriginalFilename();
        return name.endsWith(".zip") || name.endsWith(".ZIP");
    }

    public void unzipFile(MultipartFile zipFile, File destination, String charsetName) throws IOException {
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

                if (entry.isDirectory()) {
                    // 创建目录
                    if (!newFile.exists() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory: " + newFile.getAbsolutePath());
                    }
                } else {
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
                }
            }
        }
    }

    // 辅助方法：将 MultipartFile 转为 File
    private File convertToFile(MultipartFile multipartFile) throws IOException {
        File convFile = File.createTempFile("upload", ".zip");
        multipartFile.transferTo(convFile);
        return convFile;
    }

    private String generateUniqueFileName(String originalName, Long studentId) {
        return studentId + "_" + System.currentTimeMillis() + "_" + originalName;
    }

    private String generateUniqueDir(Long assignmentId, Long studentId) {
        return assignmentId + "_" + studentId + "_" + System.currentTimeMillis();
    }

    private void saveFileInfo(File file, Long assignmentId, Long studentId, Long classId) {
        Submission submission = new Submission();
        submission.setAssignmentId(assignmentId);
        submission.setStudentId(studentId);
        submission.setClassId(classId);
        submission.setFilename(file.getName());
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setStatus("SUBMITTED");

        this.save(submission);
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
    public List<Submission> getSubmissionsStudentIdAndAssignmentId(Long studentId, Long assignmentId) {
        return list(new QueryWrapper<Submission>()
                .eq("student_id", studentId)
                .eq("assignment_id", assignmentId));
    }

    // 辅助方法：处理文件夹返回
    @Override
    public List<String> resolveSubmissionFiles(String filePath) {
        File file = new File(filePath);
        List<String> fileNames = new ArrayList<>();
        if (file.isDirectory()) {
            for (File subFile : Objects.requireNonNull(file.listFiles())) {
                fileNames.add(subFile.getAbsolutePath());
            }
        } else {
            fileNames.add(file.getAbsolutePath());
        }
        return fileNames;
    }


    // endregion
}
