package app.viaverse.identity.config;

import app.viaverse.identity.auth.application.port.out.OtpDeliveryPort;
import app.viaverse.identity.auth.infrastructure.adapter.out.otp.DebugOtpDeliveryAdapter;
import app.viaverse.identity.auth.infrastructure.adapter.out.otp.MailpitPhoneOtpDeliveryAdapter;
import app.viaverse.identity.auth.infrastructure.adapter.out.otp.NetgsmSmsOtpDeliveryAdapter;
import app.viaverse.identity.auth.infrastructure.adapter.out.otp.SmtpEmailOtpDeliveryAdapter;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Properties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestClient;

/**
 * Declares the configured {@link OtpDeliveryPort} beans. Multiple adapters may
 * coexist — {@code OtpChallengeService} dispatches by
 * {@link OtpDeliveryPort#supports} and picks the first match. Real adapters
 * (NetGSM, SMTP) are ordered ahead of dev fallbacks (Mailpit phone bridge,
 * Debug log) so they win in production environments where they're enabled.
 *
 * <p>The NetGSM client is built with the static {@link RestClient#builder()}
 * (not the auto-instrumented {@code RestClient.Builder} bean) so credentials
 * carried in the URL never leak into HTTP span attributes or metrics.
 */
@Configuration
public class OtpDeliveryConfiguration {

    private static final Duration NETGSM_CONNECT_TIMEOUT = Duration.ofSeconds(3);
    private static final Duration NETGSM_READ_TIMEOUT = Duration.ofSeconds(5);

    @Bean
    @Order(1)
    @ConditionalOnExpression("'${viaverse.auth.sms.provider:none}'.equalsIgnoreCase('netgsm')")
    NetgsmSmsOtpDeliveryAdapter netgsmSmsOtpDeliveryAdapter(AuthProperties properties) {
        return new NetgsmSmsOtpDeliveryAdapter(properties.getSms().getNetgsm(), netgsmRestClient());
    }

    @Bean
    @Order(1)
    @ConditionalOnExpression("'${viaverse.auth.email.provider:none}'.equalsIgnoreCase('smtp')")
    SmtpEmailOtpDeliveryAdapter smtpEmailOtpDeliveryAdapter(
            AuthProperties properties,
            JavaMailSender javaMailSender
    ) {
        return new SmtpEmailOtpDeliveryAdapter(properties.getEmail().getSmtp(), javaMailSender);
    }

    @Bean
    @ConditionalOnExpression("'${viaverse.auth.email.provider:none}'.equalsIgnoreCase('smtp')")
    JavaMailSender javaMailSender(AuthProperties properties) {
        AuthProperties.Smtp smtp = properties.getEmail().getSmtp();
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(smtp.getHost());
        sender.setPort(smtp.getPort());
        if (smtp.isAuthEnabled()) {
            sender.setUsername(smtp.getUsername());
            sender.setPassword(smtp.getPassword());
        }
        Properties mailProperties = new Properties();
        mailProperties.put("mail.transport.protocol", "smtp");
        mailProperties.put("mail.smtp.auth", String.valueOf(smtp.isAuthEnabled()));
        mailProperties.put("mail.smtp.starttls.enable", String.valueOf(smtp.isStartTlsEnabled()));
        mailProperties.put("mail.smtp.starttls.required", String.valueOf(smtp.isStartTlsRequired()));
        mailProperties.put("mail.smtp.connectiontimeout", String.valueOf(smtp.getConnectionTimeout().toMillis()));
        mailProperties.put("mail.smtp.timeout", String.valueOf(smtp.getWriteTimeout().toMillis()));
        mailProperties.put("mail.smtp.writetimeout", String.valueOf(smtp.getWriteTimeout().toMillis()));
        sender.setJavaMailProperties(mailProperties);
        return sender;
    }

    /**
     * Local-only convenience: when there's no real SMS gateway but Mailpit
     * SMTP is wired up (the typical dev setup), forward phone OTPs to Mailpit
     * too. Sits below the real NetGSM adapter via {@code @Order(50)} so it
     * never wins in production.
     */
    @Bean
    @Order(50)
    @ConditionalOnExpression(
            "'${viaverse.auth.email.provider:none}'.equalsIgnoreCase('smtp') "
                    + "&& '${viaverse.auth.sms.provider:none}'.equalsIgnoreCase('none') "
                    + "&& '${viaverse.auth.debug.enabled:false}'.equalsIgnoreCase('true')"
    )
    MailpitPhoneOtpDeliveryAdapter mailpitPhoneOtpDeliveryAdapter(
            AuthProperties properties,
            JavaMailSender javaMailSender
    ) {
        return new MailpitPhoneOtpDeliveryAdapter(properties.getEmail().getSmtp(), javaMailSender);
    }

    /** Final fallback: log the OTP. Only active when debug is on. */
    @Bean
    @Order(100)
    @ConditionalOnExpression("'${viaverse.auth.debug.enabled:false}'.equalsIgnoreCase('true')")
    DebugOtpDeliveryAdapter debugOtpDeliveryAdapter() {
        return new DebugOtpDeliveryAdapter();
    }

    private static RestClient netgsmRestClient() {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(NETGSM_CONNECT_TIMEOUT)
                .build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(NETGSM_READ_TIMEOUT);
        return RestClient.builder()
                .requestFactory(factory)
                .build();
    }
}
