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

    @Override
    @Async
    public void sendEmail(String to, String subject, String message) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(message, false);

        mailSender.send(mimeMessage);
        log.info("Email sent to: {}", to);
    }

    @Override
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

    @Async
    @Override
    public void sendPasswordResetEmail(String to, String otp) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        String subject = "Đặt lại mật khẩu - MindMap";
        String content = "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;\">"
                + "<h2 style=\"color: #d32f2f;\">Đặt lại mật khẩu</h2>"
                + "<p>Chào bạn,</p>"
                + "<p>Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.</p>"
                + "<p>Dưới đây là mã xác thực để đặt lại mật khẩu:</p>"
                + "<h1 style=\"letter-spacing: 5px; background-color: #ffebee; padding: 10px; text-align: center; font-family: monospace; color: #d32f2f;\">"
                + otp + "</h1>"
                + "<p>Mã này sẽ hết hạn sau 5 phút.</p>"
                + "<p><strong>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này và bảo mật tài khoản của bạn.</strong></p>"
                + "<p>Trân trọng,<br/>MindMap Team</p>"
                + "</div>";

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(mimeMessage);
        log.info("Password reset email sent to: {}", to);
    }
}
