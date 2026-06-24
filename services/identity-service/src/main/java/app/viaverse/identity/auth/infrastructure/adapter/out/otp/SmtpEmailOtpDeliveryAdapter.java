package app.viaverse.identity.auth.infrastructure.adapter.out.otp;

import app.viaverse.identity.auth.application.port.out.OtpDeliveryPort;
import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import app.viaverse.identity.auth.domain.value.OtpDeliveryRequest;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.shared.error.IdentityErrors;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * SMTP email OTP adapter. Local profile points at Mailpit
 * ({@code localhost:1025}); staging/prod use a real SMTP relay
 * (SES, SendGrid, etc.) configured via {@code viaverse.auth.email.smtp.*}.
 *
 * <p>Templates are kept simple plain-text strings with a single {@code %s}
 * placeholder for the OTP. Future i18n / HTML templating should live in a
 * dedicated template service rather than ballooning this adapter.
 */
public class SmtpEmailOtpDeliveryAdapter implements OtpDeliveryPort {

    private final AuthProperties.Smtp properties;
    private final JavaMailSender mailSender;

    public SmtpEmailOtpDeliveryAdapter(AuthProperties.Smtp properties, JavaMailSender mailSender) {
        this.properties = properties;
        this.mailSender = mailSender;
    }

    @Override
    public boolean supports(IdentifierTypeEnum identifierType) {
        return identifierType == IdentifierTypeEnum.EMAIL;
    }

    @Override
    public void deliver(OtpDeliveryRequest request) {
        if (!supports(request.identifier().type())) {
            throw new IllegalStateException(
                    "SmtpEmailOtpDeliveryAdapter received unsupported identifier type "
                            + request.identifier().type()
                            + " — dispatcher routed to the wrong adapter");
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            helper.setFrom(new InternetAddress(properties.getFromAddress(), properties.getFromName(), StandardCharsets.UTF_8.name()));
            helper.setTo(request.identifier().value());
            helper.setSubject(String.format(properties.getSubjectTemplate(), request.otp()));
            helper.setText(String.format(properties.getBodyTemplate(), request.otp()), false);
            mailSender.send(message);
        } catch (MailException | MessagingException | UnsupportedEncodingException exception) {
            throw IdentityErrors.smtpDeliveryFailed(exception);
        }
    }
}
