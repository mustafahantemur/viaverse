package app.viaverse.identity.config;

import app.viaverse.identity.auth.application.port.out.OtpDeliveryPort;
import app.viaverse.identity.auth.domain.enums.OtpDeliveryProviderEnum;
import app.viaverse.identity.auth.domain.enums.SmsProviderEnum;
import app.viaverse.identity.auth.infrastructure.adapter.out.otp.DebugOtpDeliveryAdapter;
import app.viaverse.identity.auth.infrastructure.adapter.out.otp.NetgsmSmsOtpDeliveryAdapter;
import app.viaverse.identity.shared.error.IdentityErrors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class OtpDeliveryConfiguration {

    @Bean
    OtpDeliveryPort otpDeliveryPort(AuthProperties properties, RestClient.Builder restClientBuilder) {
        if (properties.getOtp().getDelivery().getProvider() == OtpDeliveryProviderEnum.DEBUG) {
            return new DebugOtpDeliveryAdapter();
        }
        if (properties.getSms().getProvider() == SmsProviderEnum.NETGSM) {
            return new NetgsmSmsOtpDeliveryAdapter(properties.getSms().getNetgsm(), restClientBuilder.build());
        }
        throw IdentityErrors.smsProviderDisabled();
    }
}
