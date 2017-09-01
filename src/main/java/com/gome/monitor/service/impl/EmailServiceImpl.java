package com.gome.monitor.service.impl;

import com.gome.monitor.component.EmailConfig;
import com.gome.monitor.service.EmailService;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    EmailConfig emailConfig;

    @Autowired
    JavaMailSenderImpl sender;

    @Autowired
    FreeMarkerConfigurationFactory factory;
    

    @Override
    public void sendSimpleMail(String sendTo, String title, String content) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(emailConfig.getEmailFrom());
        mail.setTo(sendTo.split(","));
        mail.setSubject(title);
        mail.setText(content);
        
        sender.send(mail);
    }

    @Override
    public void sendModelMail(String sendTo, String title, Map<String, Object> content, String model) {
        MimeMessage mimeMessage = sender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(emailConfig.getEmailFrom());
            helper.setTo(sendTo.split(","));
            helper.setSubject(title);
            String text = FreeMarkerTemplateUtils.processTemplateIntoString(factory.createConfiguration().getTemplate(model, "UTF-8"), content);
            helper.setText(text, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        sender.send(mimeMessage);
    }

    @Override
    public void sendAttachmentsMail(String sendTo, String title, String content, List<Pair<String, File>> attachments) {
        MimeMessage mimeMessage = sender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(emailConfig.getEmailFrom());
            helper.setTo(sendTo.split(","));
            helper.setSubject(title);
            helper.setText(content);
            for (Pair<String, File> pair : attachments) {
                helper.addAttachment(pair.getKey(), new FileSystemResource(pair.getValue()));
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        
        sender.send(mimeMessage);
    }

    @Override
    public void sendTemplateMail(String sendTo, String title, Map<String, Object> content, List<Pair<String, File>> attachments) {
        MimeMessage mimeMessage = sender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(emailConfig.getEmailFrom());
            helper.setTo(sendTo.split(","));
            helper.setSubject(title);

            String text = FreeMarkerTemplateUtils.processTemplateIntoString(factory.createConfiguration().getTemplate("process_email.vm", "UTF-8"), content);
            helper.setText(text, true);

            for (Pair<String, File> pair : attachments) {
                helper.addAttachment(pair.getKey(), new FileSystemResource(pair.getValue()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        sender.send(mimeMessage);
    }
}
