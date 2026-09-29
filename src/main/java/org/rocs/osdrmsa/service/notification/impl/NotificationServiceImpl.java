package org.rocs.osdrmsa.service.notification.impl;

import org.rocs.osdrmsa.service.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log =
            LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final JavaMailSender mailSender;

    @Value("${notifications.enabled:false}")
    private boolean enabled;

    @Value("${notifications.from-address:noreply@rc-osd.tech}")
    private String fromAddress;

    public NotificationServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void notifyRequestDecision(
            String recipientEmail,
            String recipientName,
            long requestId,
            String decision,
            String remarks) {

        if (!enabled) {
            log.info(
                    "Notifications disabled — would have emailed {} about request #{} ({})",
                    recipientEmail, requestId, decision
            );
            return;
        }

        if (recipientEmail == null || recipientEmail.isBlank()) {
            log.warn(
                    "Cannot send request-decision notification for request #{}: "
                            + "recipient has no email on file.",
                    requestId
            );
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(recipientEmail);
            message.setSubject("RC-OSD: Your request #" + requestId + " has been " + decision);

            StringBuilder body = new StringBuilder();
            body.append("Hello ").append(recipientName != null ? recipientName : "").append(",\n\n");
            body.append("Your disciplinary-record request #").append(requestId)
                    .append(" has been ").append(decision).append(".\n\n");

            if (remarks != null && !remarks.isBlank()) {
                body.append("Remarks: ").append(remarks).append("\n\n");
            }

            body.append("You can view the full details by logging in to the RC-OSD portal.\n\n")
                    .append("This is an automated message — please do not reply directly to this email.");

            message.setText(body.toString());

            mailSender.send(message);

        } catch (Exception e) {
            // A notification failure must never break the decision workflow.
            log.warn(
                    "Failed to send request-decision notification for request #{}: {}",
                    requestId, e.getMessage()
            );
        }
    }
}
