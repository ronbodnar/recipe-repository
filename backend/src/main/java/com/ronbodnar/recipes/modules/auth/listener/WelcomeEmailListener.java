package com.ronbodnar.recipes.modules.auth.listener;

import com.ronbodnar.recipes.modules.auth.event.UserRegisteredEvent;
import com.ronbodnar.recipes.modules.email.EmailService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class WelcomeEmailListener {

    private final String appName;

    private final EmailService emailService;

    public WelcomeEmailListener(@Value("${app.name}") String appName,
                                EmailService emailService) {
        this.appName = appName;
        this.emailService = emailService;
    }

    @Async("emailTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendWelcomeEmail(UserRegisteredEvent event) {
        emailService.sendEmail(
                event.email(),
                "Welcome to %s!".formatted(appName),
                "Welcome to %s! Your account has been successfully created. Your username is: %s"
                        .formatted(appName, event.username()),
                getHtmlBody(event.username(), event.email()),
                null
        );
    }

    public String getHtmlBody(String username, String email) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta http-equiv="X-UA-Compatible" content="IE=edge">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Welcome to %1$s</title>
                </head>
                <body>
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">

                        <div style="text-align: center; margin-bottom: 30px;">
                            <h1 style="margin: 0;">%1$s</h1>
                        </div>

                        <h2>Welcome, %2$s!</h2>

                        <p>
                            Thank you for creating an account with %1$s.
                            We're excited to have you here!
                        </p>

                        <p>
                            Your account has been successfully created and you're ready to
                            start discovering, creating, and organizing your favorite recipes.
                        </p>

                        <div style="background-color: #f5f5f5; padding: 20px; margin: 25px 0; border-radius: 6px;">
                            <p style="margin: 0 0 10px 0;">
                                <strong>Username:</strong> %2$s
                            </p>
                            <p style="margin: 0;">
                                <strong>Email:</strong> %3$s
                            </p>
                        </div>

                        <p>
                            We hope %1$s makes it easier for you to keep your
                            recipes organized and share great food with the people you care about.
                        </p>

                        <p>
                            If you didn't create this account, please contact us as soon as possible.
                        </p>

                        <hr style="border: 0; border-top: 1px solid #dddddd; margin: 30px 0;">

                        <p style="text-align: center; font-size: 0.8em; color: #777777;">
                            This is an automated message from %1$s.
                        </p>

                    </div>
                </body>
                </html>
                """.formatted(appName, username, email);
    }
}