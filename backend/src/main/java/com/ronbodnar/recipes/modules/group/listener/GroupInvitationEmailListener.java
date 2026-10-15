package com.ronbodnar.recipes.modules.group.listener;

import com.ronbodnar.recipes.modules.email.EmailService;
import com.ronbodnar.recipes.modules.group.event.GroupInvitationEvent;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class GroupInvitationEmailListener {

    private final String appName;
    private final String groupInvitationUrl;

    private final EmailService emailService;

    public GroupInvitationEmailListener(
            @Value("${app.name}") String appName,
            @Value("${app.group-invitation-url}") String groupInvitationUrl,
            EmailService emailService) {
        this.appName = appName;
        this.groupInvitationUrl = groupInvitationUrl;
        this.emailService = emailService;
    }

    @Async("emailTaskExecutor")
    @EventListener
    public void sendGroupInvitationEmail(GroupInvitationEvent event) {
        String invitationUrl = groupInvitationUrl + event.token();

        emailService.sendEmail(
                event.email(),
                "You're invited to join %s on %s".formatted(event.groupName(), appName),
                """
                        Hi,
                        
                        %s has invited you to join the "%s" group on %s.

                        Click the following link to accept the invitation:
                        %s

                        If you weren't expecting this invitation, you can safely ignore this email.
                        """
                        .formatted(
                                event.inviterName(),
                                event.groupName(),
                                appName,
                                invitationUrl
                        ),
                getHtmlBody(event),
                null
        );
    }

    public String getHtmlBody(GroupInvitationEvent event) {
        String invitationUrl = groupInvitationUrl + event.token();

        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta http-equiv="X-UA-Compatible" content="IE=edge">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>You're invited to join %2$s</title>
                </head>
                <body>
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">

                        <div style="text-align: center; margin-bottom: 30px;">
                            <h1 style="margin: 0;">%1$s</h1>
                        </div>

                        <h2>You're Invited!</h2>

                        <p>
                            <strong>%3$s</strong> has invited you to join the
                            <strong>%2$s</strong> group on %1$s.
                        </p>

                        <p>
                            Join the group to view and share recipes with its members.
                        </p>

                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%4$s"
                               style="display: inline-block; padding: 12px 24px;
                                      background-color: #333333; color: #ffffff;
                                      text-decoration: none; border-radius: 6px;
                                      font-weight: bold;">
                                Accept Invitation
                            </a>
                        </div>

                        <p>
                            If the button above doesn't work, you can also copy and paste
                            the following link into your browser:
                        </p>

                        <p style="word-break: break-all;">
                            <a href="%4$s">%4$s</a>
                        </p>

                        <div style="background-color: #f5f5f5; padding: 20px; margin: 25px 0; border-radius: 6px;">
                            <p style="margin: 0;">
                                <strong>Didn't expect this invitation?</strong>
                            </p>
                            <p style="margin: 10px 0 0 0;">
                                If you weren't expecting an invitation to this group,
                                you can safely ignore this email.
                            </p>
                        </div>

                        <p>
                            This invitation is temporary and can only be used once.
                        </p>

                        <hr style="border: 0; border-top: 1px solid #dddddd; margin: 30px 0;">

                        <p style="text-align: center; font-size: 0.8em; color: #777777;">
                            This is an automated message from %1$s. Please do not reply to this email.
                        </p>

                    </div>
                </body>
                </html>
                """.formatted(
                appName,
                event.groupName(),
                event.inviterName(),
                invitationUrl
        );
    }
}