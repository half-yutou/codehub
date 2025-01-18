package cn.xyt.codehub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CodeReviewTest {
    public static void main(String[] args) {
        // 定义要执行的命令和参数
        String command = "D:\\APP_File\\sim_exe\\sim_c++.exe"; // 可执行文件的路径
        String param = "-p";          // 参数
        String file1 = "D:\\APP_File\\sim_exe\\a.cpp";      // 第一个文件路径
        String file2 = "D:\\APP_File\\sim_exe\\b.cpp";      // 第二个文件路径

        // 构建命令
        ProcessBuilder processBuilder = new ProcessBuilder(command, param, file1, file2);

        // 设置工作目录 (可选，如果需要在特定路径下执行)
//        processBuilder.directory(new java.io.File("D:\\APP_File\\sim_exe"));

        try {
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
            System.out.println("Exit Code: " + exitCode);

            // 输出结果
            if (exitCode == 0) {
                System.out.println("Output:\n" + output);
            } else {
                System.err.println("Error Output:\n" + errorOutput);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

