package cn.xyt.codehub.util;


import cn.xyt.codehub.dto.CodeReviewDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeReviewUtil {
    private static final Logger logger = LoggerFactory.getLogger(CodeReviewUtil.class);

    private static final Pattern SIMILARITY_PATTERN = Pattern.compile("(\\d+)\\s*%");

    private static final int THRESHOLD = 70;

    public static CodeReviewDTO codeReview(String command, String firstFilePath, String secondFilePath) throws IOException, InterruptedException {

        String paramP = "-p";
        String paramD = "-d";
        String output = runProcess(command, paramP, firstFilePath, secondFilePath);
        int repetitionRate = extractSimilarityPercentage(new StringBuilder(output));

        if (repetitionRate < THRESHOLD) {
            return CodeReviewDTO.pass();
        }

        String report = runProcess(command, paramD, firstFilePath, secondFilePath);
        return CodeReviewDTO.fail(repetitionRate, report);

    }

    public static String runProcess(String command, String param, String firstFilePath, String secondFilePath) throws IOException, InterruptedException {
        // 构建命令
        logger.debug("Running command: {}", command);
        ProcessBuilder processBuilder = new ProcessBuilder(command, param, firstFilePath, secondFilePath);

        // 启动进程
        Process process = processBuilder.start();

        // 获取标准输出（正常输出结果）
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        // 获取错误输出（如发生错误时的输出）
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        StringBuilder errorOutput = new StringBuilder();
        while ((line = errorReader.readLine()) != null) {
            errorOutput.append(line).append("\n");
        }

        // 等待进程完成
        int exitCode = process.waitFor();

        // 输出结果
        if (exitCode == 0) {
            return output.toString();
        } else {
            logger.error("Error Output:\n" + errorOutput);
            throw new RuntimeException("Error executing process");
        }
    }

    public static int extractSimilarityPercentage(StringBuilder output) {
        Matcher matcher = SIMILARITY_PATTERN.matcher(output.toString());

        // 查找匹配项
        if (matcher.find()) {
            // 提取匹配的数字并转换为整数
            String percentageStr = matcher.group(1);
            return Integer.parseInt(percentageStr);
        } else {
            // 如果没有找到匹配项，返回一个默认值或抛出异常
            return 0;
        }
    }

}
