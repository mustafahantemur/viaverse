package app.viaverse.identity.config;

import app.viaverse.identity.auth.application.port.out.OtpDeliveryPort;
import app.viaverse.identity.auth.infrastructure.adapter.out.otp.DebugOtpDeliveryAdapter;
import app.viaverse.identity.auth.infrastructure.adapter.out.otp.NetgsmSmsOtpDeliveryAdapter;
import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Declares the configured {@link OtpDeliveryPort} beans. Multiple adapters may
 * coexist — {@code OtpChallengeService} dispatches by {@link OtpDeliveryPort#supports}.
 *
 * <p>The NetGSM client is built with the static {@link RestClient#builder()}
 * (not the auto-instrumented {@code RestClient.Builder} bean) so that
 * credentials carried in the request URL are never captured into HTTP span
 * attributes or metrics. See {@link NetgsmSmsOtpDeliveryAdapter} for the
 * security rationale and the step-8 follow-up.
 */
@Configuration
public class OtpDeliveryConfiguration {

    private static final Duration NETGSM_CONNECT_TIMEOUT = Duration.ofSeconds(3);
    private static final Duration NETGSM_READ_TIMEOUT = Duration.ofSeconds(5);

    @Bean
    @ConditionalOnExpression("'${viaverse.auth.otp.delivery.provider:debug}'.equalsIgnoreCase('debug')")
    DebugOtpDeliveryAdapter debugOtpDeliveryAdapter() {
        return new DebugOtpDeliveryAdapter();
    }

    @Bean
    @ConditionalOnExpression("'${viaverse.auth.sms.provider:none}'.equalsIgnoreCase('netgsm')")
    NetgsmSmsOtpDeliveryAdapter netgsmSmsOtpDeliveryAdapter(AuthProperties properties) {
        return new NetgsmSmsOtpDeliveryAdapter(properties.getSms().getNetgsm(), netgsmRestClient());
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
