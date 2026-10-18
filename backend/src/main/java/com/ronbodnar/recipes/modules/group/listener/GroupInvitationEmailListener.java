package com.ronbodnar.recipes.modules.group.listener;

import com.ronbodnar.recipes.modules.email.EmailService;
import com.ronbodnar.recipes.modules.group.event.GroupInvitationEvent;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GroupInvitationEmailListener {

    private final String appName;
    private final String groupInvitationUrl;

    private final EmailService emailService;

    public GroupInvitationEmailListener(
            @Value("${app.name}") String appName,
            @Value("${app.groups.invitation-url}") String groupInvitationUrl,
            EmailService emailService) {
        this.appName = appName;
        this.groupInvitationUrl = groupInvitationUrl;
        this.emailService = emailService;
    }

    @Async("emailTaskExecutor")
    @EventListener
    public void sendGroupInvitationEmail(GroupInvitationEvent event) {
        log.info("Sending group invitation email: {}", event);
        String invitationUrl = groupInvitationUrl.replace("GROUP_ID", String.valueOf(event.groupId())) + event.token();

        emailService.sendEmail(
                event.email(),
                "You're invited to join %s on %s".formatted(event.groupName(), appName),
                """
                        Hi,
                        
                        %s has invited you to join the "%s" group on %s.

                        Click the following link to accept the invitation:
                        %4$s?response=accept

                        If you weren't expecting this invitation, you can safely ignore this email or click this link:
                        %4$s?response=decline
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
        String invitationUrl = groupInvitationUrl.replace("GROUP_ID", String.valueOf(event.groupId())) + event.token();
        String acceptUrl = invitationUrl + "?response=accept";
        String declineUrl = invitationUrl + "?response=decline";

        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta http-equiv="X-UA-Compatible" content="IE=edge">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>You're invited to join %2$s</title>
            </head>
            <body style="margin: 0; padding: 0; background-color: #f7f7f7;">
                <div style="font-family: Arial, sans-serif; max-width: 600px;
                            margin: 0 auto; padding: 32px 20px; color: #333333;">

                    <div style="text-align: center; margin-bottom: 28px;">
                        <h1 style="margin: 0; font-size: 26px; color: #333333;">
                            %1$s
                        </h1>
                    </div>

                    <div style="background-color: #ffffff; padding: 32px;
                                border-radius: 10px;">

                        <h2 style="margin-top: 0; font-size: 22px;">
                            You're invited!
                        </h2>

                        <p style="font-size: 16px; line-height: 1.6;">
                            <strong>%3$s</strong> has invited you to join the
                            <strong>%2$s</strong> group on %1$s.
                        </p>

                        <p style="font-size: 16px; line-height: 1.6;">
                            Join the group to share recipes and discover what
                            other members are cooking.
                        </p>

                        <div style="text-align: center; margin: 30px 0 20px;">
                            <a href="%4$s"
                               style="display: inline-block; padding: 13px 28px;
                                      background-color: #333333; color: #ffffff;
                                      text-decoration: none; border-radius: 6px;
                                      font-size: 15px; font-weight: bold;">
                                Accept invitation
                            </a>
                        </div>

                        <p style="text-align: center; font-size: 14px;
                                  color: #666666; margin: 0 0 16px;">
                            Not interested? You can decline the invitation below.
                        </p>

                        <div style="text-align: center; margin: 20px 0 30px;">
                            <a href="%5$s"
                               style="display: inline-block; padding: 11px 24px;
                                      background-color: #ffffff; color: #b42318;
                                      border: 1px solid #b42318;
                                      text-decoration: none; border-radius: 6px;
                                      font-size: 14px; font-weight: bold;">
                                Decline invitation
                            </a>
                        </div>

                        <hr style="border: 0; border-top: 1px solid #eeeeee;
                                   margin: 28px 0;">

                        <p style="font-size: 14px; line-height: 1.6;
                                  color: #666666;">
                            If you weren't expecting this invitation, no worries
                            — you can simply ignore this email.
                        </p>

                        <p style="font-size: 14px; line-height: 1.6;
                                  color: #666666;">
                            This invitation can only be used once.
                        </p>

                        <p style="font-size: 14px; line-height: 1.6;">
                            Happy cooking,<br>
                            The %1$s Team
                        </p>
                    </div>

                    <p style="font-size: 12px; line-height: 1.6;
                              text-align: center; color: #888888;
                              margin-top: 24px;">
                        If the buttons don't work, use the links below.
                    </p>

                    <p style="font-size: 12px; line-height: 1.6;
                              word-break: break-all;">
                        Accept: <a href="%4$s" style="color: #555555;">%4$s</a>
                    </p>

                    <p style="font-size: 12px; line-height: 1.6;
                              word-break: break-all;">
                        Decline: <a href="%5$s" style="color: #b42318;">%5$s</a>
                    </p>
                </div>
            </body>
            </html>
            """.formatted(
                appName,
                event.groupName(),
                event.inviterName(),
                acceptUrl,
                declineUrl
        );
    }
}