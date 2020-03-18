/**
 * projectName: webclound
 * fileName: MailServiceImpl.java
 * packageName: com.cyvation.webclound.mail
 * date: 2019-10-29 11:59
 * copyright(c) 2017-2020 同方赛威讯信息技术公司
 */
package com.cyvation.webclound.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

/**
 * @version: V1.0
 * @author: 代浩然
 * @className: MailServiceImpl
 * @packageName: com.cyvation.webclound.mail
 * @description: 邮件服务实现
 * @data: 2019-10-29 11:59
 **/
@Service
public class MailServiceImpl implements MailService{

    @Value("${spring.mail.mailTo}")
    private String mailTo;

    @Value("${spring.mail.username}")
    private String username;

    private JavaMailSender javaMailSender;

    @Autowired
    public MailServiceImpl(JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }

    /**
     * 发送邮件，方法异步执行
     * @param subject 邮件主题
     * @param text 邮件的文本内容
     * @throws Exception
     */
    @Async
    @Override
    public void sendTextMail(String subject,String text) throws Exception {
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage,true);
        message.setFrom(username);
        message.setTo(mailTo);
        message.setSubject(subject);
        message.setText(text,true);
        javaMailSender.send(mimeMessage);
    }
}