package com.ronbodnar.recipes.modules.auth.listener;

import com.ronbodnar.recipes.modules.auth.event.UserForgotPasswordRequestEvent;
import com.ronbodnar.recipes.modules.email.EmailService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ForgotPasswordInstructionEmailListener {

    private final String appName;
    private final String passwordResetUrl;

    private final EmailService emailService;

    public ForgotPasswordInstructionEmailListener(
            @Value("${app.name}") String appName,
            @Value("${app.password-reset-url}") String passwordResetUrl,
            EmailService emailService) {
        this.appName = appName;
        this.passwordResetUrl = passwordResetUrl;
        this.emailService = emailService;
    }

    @Async("emailTaskExecutor")
    @EventListener
    public void sendPasswordResetEmail(UserForgotPasswordRequestEvent event) {
        emailService.sendEmail(
                event.email(),
                "Reset your %s password".formatted(appName),
                """
                        Hi %s,
                        
                        We received a request to reset your %s password.

                        Use the following link to reset your password:
                        %s

                        If you did not request a password reset, you can safely ignore this email.
                        """
                        .formatted(appName, event.username(), passwordResetUrl + event.token()),
                getHtmlBody(event.username(), event.token()),
                null
        );
    }

    public String getHtmlBody(String username, String token) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta http-equiv="X-UA-Compatible" content="IE=edge">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Reset your %1$s password</title>
                </head>
                <body>
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">

                        <div style="text-align: center; margin-bottom: 30px;">
                            <h1 style="margin: 0;">%1$s</h1>
                        </div>

                        <h2>Password Reset</h2>

                        <p>
                            We received a request to reset your %1$s password for your account <strong>%3$s</strong>.
                        </p>

                        <p>
                            Click the button below to choose a new password:
                        </p>

                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%2$s"
                               style="display: inline-block; padding: 12px 24px;
                                      background-color: #333333; color: #ffffff;
                                      text-decoration: none; border-radius: 6px;
                                      font-weight: bold;">
                                Reset Your Password
                            </a>
                        </div>

                        <p>
                            If the button above doesn't work, you can also copy and paste
                            the following link into your browser:
                        </p>

                        <p style="word-break: break-all;">
                            <a href="%2$s">%2$s</a>
                        </p>

                        <div style="background-color: #f5f5f5; padding: 20px; margin: 25px 0; border-radius: 6px;">
                            <p style="margin: 0;">
                                <strong>Didn't request a password reset?</strong>
                            </p>
                            <p style="margin: 10px 0 0 0;">
                                If you didn't request this, you can safely ignore this email.
                                Your password will not be changed unless you complete the
                                password reset process.
                            </p>
                        </div>

                        <p>
                            For your security, this password reset link is temporary and
                            can only be used once.
                        </p>

                        <hr style="border: 0; border-top: 1px solid #dddddd; margin: 30px 0;">

                        <p style="text-align: center; font-size: 0.8em; color: #777777;">
                            This is an automated message from %1$s. Please do not reply to this email.
                        </p>

                    </div>
                </body>
                </html>
                """.formatted(appName, passwordResetUrl + token, username);
    }
}