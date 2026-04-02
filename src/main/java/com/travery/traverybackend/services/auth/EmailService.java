package com.travery.traverybackend.services.auth;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;
  private final EmailTemplateLoader templateLoader;

  @Value("${app.otp.ttl-minutes}")
  private long otpTtlMinutes;

  /**
   * Sends an HTML account verification OTP email.
   *
   * <p>Template: templates/email/register-otp.html
   */
  @Async
  public void sendOtp(String toEmail, String otp) {
    String html =
        templateLoader.load(
            "register-otp.html",
            Map.of("otpCode", otp, "expirationMinutes", String.valueOf(otpTtlMinutes)));
    sendHtml(toEmail, "Travery — Verify your account", html);
  }

  /**
   * Sends an HTML password-reset OTP email.
   *
   * <p>Template: templates/email/password-reset-otp.html
   */
  @Async
  public void sendResetPasswordOtp(String toEmail, String otp) {
    String html =
        templateLoader.load(
            "password-reset-otp.html",
            Map.of("otpCode", otp, "expirationMinutes", String.valueOf(otpTtlMinutes)));
    sendHtml(toEmail, "Travery — Reset your password", html);
  }

  private void sendHtml(String to, String subject, String htmlBody) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(htmlBody, true); // true = isHtml
      mailSender.send(message);
    } catch (MessagingException e) {
      // Log and swallow — email failure must not crash the caller thread.
      // The user can trigger a resend from the app.
      log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
    }
  }
}
