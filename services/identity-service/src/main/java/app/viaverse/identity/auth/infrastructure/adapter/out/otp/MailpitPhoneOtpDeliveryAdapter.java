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
 * Local-only fallback: forwards a phone OTP to the same Mailpit inbox that
 * receives email OTPs, addressed to a synthetic
 * {@code phone-+9055...@dev.local} address. Gives developers one UI
 * ({@code http://localhost:8025}) to see every OTP the service ever issues,
 * instead of needing a real SMS gateway in local. Wired only when SMTP is
 * configured AND no real SMS provider is set — see
 * {@code OtpDeliveryConfiguration}.
 */
public class MailpitPhoneOtpDeliveryAdapter implements OtpDeliveryPort {

    private final AuthProperties.Smtp properties;
    private final JavaMailSender mailSender;

    public MailpitPhoneOtpDeliveryAdapter(AuthProperties.Smtp properties, JavaMailSender mailSender) {
        this.properties = properties;
        this.mailSender = mailSender;
    }

    @Override
    public boolean supports(IdentifierTypeEnum identifierType) {
        return identifierType == IdentifierTypeEnum.PHONE;
    }

    @Override
    public void deliver(OtpDeliveryRequest request) {
        if (!supports(request.identifier().type())) {
            throw new IllegalStateException(
                    "MailpitPhoneOtpDeliveryAdapter received unsupported identifier type "
                            + request.identifier().type());
        }
        String synthetic = "phone-" + request.identifier().value().replace("+", "") + "@dev.local";
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            helper.setFrom(new InternetAddress(
                    properties.getFromAddress(),
                    properties.getFromName(),
                    StandardCharsets.UTF_8.name()));
            helper.setTo(synthetic);
            helper.setSubject("[SMS to " + request.identifier().value() + "] verification code: " + request.otp());
            helper.setText(
                    "Phone OTP for " + request.identifier().value() + ":\n"
                            + request.otp() + "\n\n"
                            + "(In production this would be delivered via NetGSM. "
                            + "In local it lands in Mailpit so you can copy the code.)",
                    false);
            mailSender.send(message);
        } catch (MailException | MessagingException | UnsupportedEncodingException exception) {
            throw IdentityErrors.smtpDeliveryFailed(exception);
        }
    }
}
