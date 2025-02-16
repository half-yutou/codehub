package cn.xyt.codehub.task;

import cn.xyt.codehub.util.DatabaseBackupUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;

@Component
public class DynamicTaskService {
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

    private static final Logger logger = LoggerFactory.getLogger(DatabaseBackupTask.class);

    private final TaskScheduler taskScheduler;
    private ScheduledFuture<?> scheduledFuture;
    // 默认 cron 表达式：每天凌晨 2:00
    private String cronExpression = "0 0 2 * * ?";

    @Autowired
    public DynamicTaskService(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
        scheduleTask();
    }

    /**
     * 调度任务
     */
    public void scheduleTask() {
        // 如果之前已调度的任务存在，则取消
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
        // 使用新的 cron 表达式创建任务
        scheduledFuture = taskScheduler.schedule(this::doBackup, new CronTrigger(cronExpression));
    }

    /**
     * 执行备份逻辑
     */
    public void doBackup() {
        try {
            String backupFile = DatabaseBackupUtil.backupDatabase(host, port, username, password, database, backupDir);
            logger.info("数据库备份成功，备份文件：{}", backupFile);
        } catch (IOException e) {
            logger.error("定时备份失败：{}", e.getMessage());
        }
    }

    /**
     * 动态更新 cron 表达式
     */
    public void updateCronExpression(String newCron) {
        this.cronExpression = newCron;
        // 重新调度任务
        logger.info("更新 cron 表达式为：{}, 并重新调度任务", newCron);
        scheduleTask();
    }
}
