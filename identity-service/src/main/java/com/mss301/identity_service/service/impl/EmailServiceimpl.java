package com.mss301.identity_service.service.impl;

import com.mss301.identity_service.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceimpl implements EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendOtpEmail(String to, String otp) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        String subject = "Email Verification Code - MindMap";
        String content = "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;\">"
                + "<h2 style=\"color: #333366;\">Xác thực email</h2>"
                + "<p>Chào bạn,</p>"
                + "<p>Dưới đây là mã xác thực cho tài khoản của bạn:</p>"
                + "<h1 style=\"letter-spacing: 5px; background-color: #f1f1f1; padding: 10px; text-align: center; font-family: monospace;\">"
                + otp + "</h1>"
                + "<p>Mã này sẽ hết hạn sau 5 phút.</p>"
                + "<p>Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email này.</p>"
                + "<p>Trân trọng,<br/>MindMap Team</p>"
                + "</div>";

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(mimeMessage);
        log.info("Email sent to: {}", to);
    }
}
