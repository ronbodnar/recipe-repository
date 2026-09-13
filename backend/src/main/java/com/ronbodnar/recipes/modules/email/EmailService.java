package com.ronbodnar.recipes.modules.email;

import com.ronbodnar.recipes.exception.BusinessException;
import com.ronbodnar.recipes.exception.ErrorCode;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    private final String appName;
    private final String replyToEmail;
    private final String fromEmail;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${app.name}") String appName,
            @Value("${app.mail.reply-to}") String replyToEmail,
            @Value("${spring.mail.username}") String fromEmail
    ) {
        this.appName = appName;
        this.mailSender = mailSender;
        this.replyToEmail = replyToEmail;
        this.fromEmail = fromEmail;
    }

    public void sendEmail(String email, String subject, String plainText, String htmlText, File attachment) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper;
        try {
            messageHelper = new MimeMessageHelper(message, true);
            messageHelper.setReplyTo(replyToEmail);
            messageHelper.setFrom("%s <%s>".formatted(appName, fromEmail));
            messageHelper.setTo(email);
            messageHelper.setSubject(subject);
            messageHelper.setText(plainText, htmlText);
            if (attachment != null) {
                messageHelper.addAttachment(attachment.getName(), attachment);
            }
        } catch (MessagingException e) {
            throw new BusinessException(
                    ErrorCode.CONSTRUCT_EMAIL_FAILED,
                    "Failed to construct email to " + email + " with subject '" + subject + "'"
            );
        }

        try {
            mailSender.send(message);
        } catch (MailException e) {
            log.error("Failed to send email to: {}", email, e);
            throw new BusinessException(
                    ErrorCode.SEND_EMAIL_FAILED,
                    "Failed to construct email to " + email + " with subject '" + subject + "'"
            );
        }

        if (attachment != null && !attachment.delete()) {
            log.warn("Failed to delete attachment file for email to: {}", email);
        }

        log.info("Email successfully sent to: {}", email);
    }

}
