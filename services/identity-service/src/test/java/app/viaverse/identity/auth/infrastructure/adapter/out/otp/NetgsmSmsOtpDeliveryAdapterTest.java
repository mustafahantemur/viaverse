package app.viaverse.identity.auth.infrastructure.adapter.out.otp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import app.viaverse.identity.auth.domain.value.NormalizedIdentifier;
import app.viaverse.identity.auth.domain.value.OtpDeliveryRequest;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.shared.kernel.error.AppErrorCode;
import app.viaverse.shared.kernel.error.TechnicalException;
import java.time.Instant;
import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class NetgsmSmsOtpDeliveryAdapterTest {

    private AuthProperties.Netgsm netgsm;
    private RestClient.Builder builder;
    private MockRestServiceServer server;
    private NetgsmSmsOtpDeliveryAdapter adapter;

    @BeforeEach
    void setUp() {
        netgsm = new AuthProperties.Netgsm();
        netgsm.setEndpoint("https://api.netgsm.test/sms/send/get");
        netgsm.setUsername("user");
        netgsm.setPassword("pass");
        netgsm.setHeader("VIAVERSE");
        netgsm.setMessageTemplate("Code: %s");

        builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        adapter = new NetgsmSmsOtpDeliveryAdapter(netgsm, builder.build());
    }

    @Test
    void supportsOnlyPhoneIdentifiers() {
        assertThat(adapter.supports(IdentifierTypeEnum.PHONE)).isTrue();
        assertThat(adapter.supports(IdentifierTypeEnum.EMAIL)).isFalse();
        assertThat(adapter.supports(IdentifierTypeEnum.SOCIAL)).isFalse();
    }

    @Test
    void deliversOtpAndReturnsOnSuccessResponse() {
        server.expect(requestTo(Matchers.startsWith("https://api.netgsm.test/sms/send/get")))
                .andExpect(queryParam("usercode", "user"))
                .andExpect(queryParam("password", "pass"))
                .andExpect(queryParam("gsmno", "905551234567"))
                .andExpect(queryParam("message", "Code:%20123456"))
                .andExpect(queryParam("msgheader", "VIAVERSE"))
                .andRespond(withSuccess("00 12345", MediaType.TEXT_PLAIN));

        adapter.deliver(phoneRequest("+905551234567", "123456"));

        server.verify();
    }

    @Test
    void mapsNonSuccessResponseToSmsDeliveryRejected() {
        server.expect(requestTo(Matchers.startsWith("https://api.netgsm.test/sms/send/get")))
                .andRespond(withSuccess("60 invalid header", MediaType.TEXT_PLAIN));

        assertThatThrownBy(() -> adapter.deliver(phoneRequest("+905551234567", "123456")))
                .isInstanceOf(TechnicalException.class)
                .extracting("errorCode")
                .isEqualTo(AppErrorCode.TECHNICAL_SMS_DELIVERY_FAILED);
    }

    @Test
    void mapsServerErrorToSmsDeliveryFailed() {
        server.expect(requestTo(Matchers.startsWith("https://api.netgsm.test/sms/send/get")))
                .andRespond(withServerError());

        assertThatThrownBy(() -> adapter.deliver(phoneRequest("+905551234567", "123456")))
                .isInstanceOf(TechnicalException.class)
                .extracting("errorCode")
                .isEqualTo(AppErrorCode.TECHNICAL_SMS_DELIVERY_FAILED);
    }

    @Test
    void throwsIllegalStateWhenRoutedNonPhoneRequest() {
        OtpDeliveryRequest emailRequest = new OtpDeliveryRequest(
                UUID.randomUUID(),
                new NormalizedIdentifier(IdentifierTypeEnum.EMAIL, "user@example.com"),
                "123456",
                Instant.parse("2026-05-16T12:00:00Z")
        );

        assertThatThrownBy(() -> adapter.deliver(emailRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("EMAIL");
    }

    private OtpDeliveryRequest phoneRequest(String phone, String otp) {
        return new OtpDeliveryRequest(
                UUID.randomUUID(),
                new NormalizedIdentifier(IdentifierTypeEnum.PHONE, phone),
                otp,
                Instant.parse("2026-05-16T12:00:00Z")
        );
    }
}
