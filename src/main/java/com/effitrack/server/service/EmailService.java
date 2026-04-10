package com.effitrack.server.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import static com.effitrack.server.constant.StringConst.FILE_NAME_SHIFT_CHART;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendReportWithAttachment(String to, String subject, String body, byte[] chartImage) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);

        if (chartImage != null && chartImage.length > 0) {
            helper.addAttachment(FILE_NAME_SHIFT_CHART, new ByteArrayResource(chartImage));
        }

        mailSender.send(message);
    }
}
