package cn.xyt.codehub.util;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailUtil {

    @Resource
    private JavaMailSender mailSender;

    public void sendMail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("3369726918@qq.com"); // 发件人邮箱
        message.setTo(to); // 收件人
        message.setSubject(subject); // 邮件主题
        message.setText(content); // 邮件内容
        mailSender.send(message); // 发送邮件
    }
}
