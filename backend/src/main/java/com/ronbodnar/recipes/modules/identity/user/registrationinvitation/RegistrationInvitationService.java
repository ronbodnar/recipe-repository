package com.ronbodnar.recipes.modules.identity.user.registrationinvitation;

import com.ronbodnar.recipes.exception.BusinessException;
import com.ronbodnar.recipes.exception.ErrorCode;
import com.ronbodnar.recipes.modules.email.EmailService;

import com.ronbodnar.recipes.modules.identity.user.dto.RegistrationInvitationRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Service
public class RegistrationInvitationService {

    private final String appName;
    private final String registrationUrl;
    private final EmailService emailService;
    private final RegistrationInvitationRepository registrationInvitationRepository;

    public RegistrationInvitationService(
            @Value("${app.name}") String appName,
            @Value("${app.registration.url}") String registrationUrl,
            EmailService emailService,
            RegistrationInvitationRepository registrationInvitationRepository
    ) {
        this.appName = appName;
        this.registrationUrl = registrationUrl;
        this.emailService = emailService;
        this.registrationInvitationRepository = registrationInvitationRepository;
    }

    public boolean isValidCode(UUID registrationCode, String email) {
        RegistrationInvitation invitation = registrationInvitationRepository.findByRegistrationCode(registrationCode)
                .orElse(null);

        if (invitation == null || !invitation.getEmail().equalsIgnoreCase(email)) {
            return false;
        }

        return true;
    }

    public void markCodeClaimed(UUID registrationCode) {
        RegistrationInvitation invitation = registrationInvitationRepository.findByRegistrationCode(registrationCode)
                .orElse(null);

        if (invitation != null) {
            invitation.setStatus(RegistrationInvitationStatus.CLAIMED);

            registrationInvitationRepository.save(invitation);
        }
    }

    public RegistrationInvitation createInvitation(RegistrationInvitationRequest invitation) {
        RegistrationInvitation registrationInvitation = new RegistrationInvitation();
        registrationInvitation.setEmail(invitation.email());
        registrationInvitation.setRegistrationCode(UUID.randomUUID());
        registrationInvitation.setStatus(RegistrationInvitationStatus.PENDING);

        return registrationInvitationRepository.save(registrationInvitation);
    }

    public void sendRegistrationInvitationEmail(String email, UUID registrationCode) {
        log.info("Sending registration invitation email to: {}", email);

        String url = registrationUrl
                + UriUtils.encodeQueryParam(
                registrationCode.toString(),
                StandardCharsets.UTF_8
        );

        emailService.sendEmail(
                email,
                "You're invited to join %s".formatted(appName),
                getTextBody(url),
                getHtmlBody(url),
                null
        );
    }

    private String getTextBody(String registrationUrl) {
        return """
                Hi,

                You've been invited to create an account on %s.

                Registration is restricted on this self-hosted instance.
                Use the following link to register your account:

                %s

                If you weren't expecting this invitation, you can safely
                ignore this email.

                Happy cooking,
                The %s Team
                """.formatted(appName, registrationUrl, appName);
    }

    private String getHtmlBody(String registrationUrl) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta http-equiv="X-UA-Compatible" content="IE=edge">
                    <meta name="viewport"
                          content="width=device-width, initial-scale=1.0">
                    <title>You're invited to join %1$s</title>
                </head>
                <body style="margin: 0; padding: 0;
                             background-color: #f7f7f7;">
                    <div style="font-family: Arial, sans-serif;
                                max-width: 600px; margin: 0 auto;
                                padding: 32px 20px; color: #333333;">

                        <div style="text-align: center;
                                    margin-bottom: 28px;">
                            <h1 style="margin: 0; font-size: 26px;
                                       color: #333333;">
                                %1$s
                            </h1>
                        </div>

                        <div style="background-color: #ffffff;
                                    padding: 32px; border-radius: 10px;">

                            <h2 style="margin-top: 0; font-size: 22px;">
                                You're invited!
                            </h2>

                            <p style="font-size: 16px; line-height: 1.6;">
                                You've been invited to create an account
                                on %1$s.
                            </p>

                            <p style="font-size: 16px; line-height: 1.6;">
                                Registration is restricted on this
                                self-hosted instance. Use the invitation
                                link below to create your account.
                            </p>

                            <div style="text-align: center;
                                        margin: 30px 0;">
                                <a href="%2$s"
                                   style="display: inline-block;
                                          padding: 13px 28px;
                                          background-color: #333333;
                                          color: #ffffff;
                                          text-decoration: none;
                                          border-radius: 6px;
                                          font-size: 15px;
                                          font-weight: bold;">
                                    Create your account
                                </a>
                            </div>

                            <hr style="border: 0;
                                       border-top: 1px solid #eeeeee;
                                       margin: 28px 0;">

                            <p style="font-size: 14px; line-height: 1.6;
                                      color: #666666;">
                                If you weren't expecting this invitation,
                                you can safely ignore this email.
                            </p>

                            <p style="font-size: 14px; line-height: 1.6;">
                                Happy cooking,<br>
                                The %1$s Team
                            </p>
                        </div>

                        <p style="font-size: 12px; line-height: 1.6;
                                  text-align: center; color: #888888;
                                  margin-top: 24px;">
                            If the button doesn't work, use the link below.
                        </p>

                        <p style="font-size: 12px; line-height: 1.6;
                                  word-break: break-all;">
                            <a href="%2$s" style="color: #555555;">
                                %2$s
                            </a>
                        </p>
                    </div>
                </body>
                </html>
                """.formatted(appName, registrationUrl);
    }
}