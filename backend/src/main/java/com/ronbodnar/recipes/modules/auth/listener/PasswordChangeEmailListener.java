package com.ronbodnar.recipes.modules.auth.listener;

import com.ronbodnar.recipes.modules.auth.event.UserPasswordChangeEvent;
import com.ronbodnar.recipes.modules.email.EmailService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PasswordChangeEmailListener {

    private final String appName;

    private final EmailService emailService;

    public PasswordChangeEmailListener(
            @Value("${app.name}") String appName,
            EmailService emailService) {
        this.appName = appName;
        this.emailService = emailService;
    }

    @Async("emailTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendPasswordChangeEmail(UserPasswordChangeEvent event) {
        emailService.sendEmail(
                event.email(),
                "Your %s password was changed".formatted(appName),
                """
                        Hello %s,
                        
                        Your %s password was successfully changed.

                        If you made this change, no further action is required.

                        If you did not change your password, please reset your password immediately
                        and contact us if you believe your account has been compromised.
                        """
                        .formatted(event.username(), appName),
                getHtmlBody(event.username()),
                null
        );
    }

    public String getHtmlBody(String username) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta http-equiv="X-UA-Compatible" content="IE=edge">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Your %1$s password was changed</title>
                </head>
                <body>
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">

                        <div style="text-align: center; margin-bottom: 30px;">
                            <h1 style="margin: 0;">%1$s</h1>
                        </div>

                        <h2>Password Changed</h2>

                        <strong>Hello %2$s,</strong>

                        <p>
                            Your %1$s password was successfully changed.
                        </p>

                        <p>
                            If you made this change, no further action is required.
                        </p>

                        <div style="background-color: #f5f5f5; padding: 20px; margin: 25px 0; border-radius: 6px;">
                            <p style="margin: 0;">
                                <strong>Didn't change your password?</strong>
                            </p>
                            <p style="margin: 10px 0 0 0;">
                                If you did not make this change, your account may have been
                                compromised. Reset your password immediately and contact us
                                if you believe someone else has accessed your account.
                            </p>
                        </div>

                        <p>
                            For your security, we recommend using a unique password that you
                            don't use for other accounts.
                        </p>

                        <hr style="border: 0; border-top: 1px solid #dddddd; margin: 30px 0;">

                        <p style="text-align: center; font-size: 0.8em; color: #777777;">
                            This is an automated message from %1$s.
                        </p>

                    </div>
                </body>
                </html>
                """.formatted(appName, username);
    }
}