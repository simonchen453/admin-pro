package com.adminpro.core.base.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.MimeMessage;

@Component
public class MailUtil {

    public static MailUtil getInstance() {
        return SpringUtil.getBean(MailUtil.class);
    }

    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleMail(SimpleMailMessage mail) {
        mailSender.send(mail);
    }

    public void sendMimeMessage(MimeMessage mail) {
        mailSender.send(mail);
    }
}
