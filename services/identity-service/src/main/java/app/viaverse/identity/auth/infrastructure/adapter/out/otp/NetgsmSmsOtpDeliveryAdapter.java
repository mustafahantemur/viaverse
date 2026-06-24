package app.viaverse.identity.auth.infrastructure.adapter.out.otp;

import app.viaverse.identity.auth.application.port.out.OtpDeliveryPort;
import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import app.viaverse.identity.auth.domain.value.OtpDeliveryRequest;
import app.viaverse.identity.config.AuthProperties;
import app.viaverse.identity.shared.error.IdentityErrors;
import java.net.URI;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * NetGSM HTTP API adapter for SMS OTP delivery.
 *
 * <p><b>Security note:</b> NetGSM's {@code sms/send/get} endpoint accepts
 * credentials, recipient number, and message body as query-string parameters.
 * The supplied {@link RestClient} MUST NOT be the Spring Boot
 * auto-instrumented client — otherwise credentials and the OTP code would be
 * captured in HTTP span attributes (and any downstream OTLP/OpenSearch sink).
 * Use the dedicated NetGSM client built in {@code OtpDeliveryConfiguration}.
 *
 * <p>TODO (step 8): switch to NetGSM's POST endpoint once we have staging
 * credentials to validate the alternate response shape, then re-attach
 * observation with a URI-sanitising convention.
 */
public class NetgsmSmsOtpDeliveryAdapter implements OtpDeliveryPort {

    private final AuthProperties.Netgsm properties;
    private final RestClient restClient;

    public NetgsmSmsOtpDeliveryAdapter(AuthProperties.Netgsm properties, RestClient restClient) {
        this.properties = properties;
        this.restClient = restClient;
    }

    @Override
    public boolean supports(IdentifierTypeEnum identifierType) {
        return identifierType == IdentifierTypeEnum.PHONE;
    }

    @Override
    public void deliver(OtpDeliveryRequest request) {
        if (!supports(request.identifier().type())) {
            throw new IllegalStateException(
                    "NetgsmSmsOtpDeliveryAdapter received unsupported identifier type "
                            + request.identifier().type()
                            + " — dispatcher routed to the wrong adapter");
        }

        URI uri = UriComponentsBuilder.fromUriString(properties.getEndpoint())
                .queryParam("usercode", properties.getUsername())
                .queryParam("password", properties.getPassword())
                .queryParam("gsmno", normalizePhone(request.identifier().value()))
                .queryParam("message", properties.getMessageTemplate().formatted(request.otp()))
                .queryParam("msgheader", properties.getHeader())
                .encode()
                .build()
                .toUri();
        try {
            String response = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(String.class);
            if (response == null || !response.startsWith("00")) {
                throw IdentityErrors.smsDeliveryRejected();
            }
        } catch (RestClientException exception) {
            throw IdentityErrors.smsDeliveryFailed(exception);
        }
    }

    private String normalizePhone(String phone) {
        return phone.startsWith("+") ? phone.substring(1) : phone;
    }
}
