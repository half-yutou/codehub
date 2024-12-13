package cn.xyt.codehub.task;

import cn.xyt.codehub.util.DatabaseBackupUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DatabaseBackupTask {

    @Value("${task.database.host}")
    private String host;

    @Value("${task.database.port}")
    private String port;

    @Value("${task.database.username}")
    private String username;

    @Value("${task.database.password}")
    private String password;

    @Value("${task.database.name}")
    private String database;

    @Value("${task.backup.dir}")
    private String backupDir;

    /**
     * 定时备份任务（每天凌晨 2:00 触发）
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void autoBackup() {
        try {
            String backupFile = DatabaseBackupUtil.backupDatabase(host, port, username, password, database, backupDir);
            System.out.println("数据库备份成功，备份文件：" + backupFile);
        } catch (IOException e) {
            System.err.println("定时备份失败：" + e.getMessage());
        }
    }
}
