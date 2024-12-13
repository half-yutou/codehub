package cn.xyt.codehub.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseBackupUtil {

/**
 * 执行数据库备份
 * @param host 数据库主机地址
 * @param port 数据库端口
 * @param username 数据库用户名
 * @param password 数据库密码
 * @param database 数据库名称
 * @param backupDir 备份文件存储目录
 * @return 备份文件路径
 * @throws IOException 异常
 */
    public static String backupDatabase(String host, String port, String username, String password, String database, String backupDir) throws IOException {
        // 生成备份文件名
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String backupFile = backupDir + File.separator + database + "_" + timestamp + ".sql";

        // mysqldump 命令
        String command = String.format("mysqldump -h%s -P%s -u%s -p%s %s --result-file=%s",
                host, port, username, password, database, backupFile);

        // 执行命令
        Process process = Runtime.getRuntime().exec(command);

        try {
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return backupFile; // 成功返回文件路径
            } else {
                throw new IOException("数据库备份失败，退出代码：" + exitCode);
            }
        } catch (InterruptedException e) {
            throw new IOException("数据库备份过程中断", e);
        }
    }
}
