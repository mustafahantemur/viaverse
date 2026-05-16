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

public class NetgsmSmsOtpDeliveryAdapter implements OtpDeliveryPort {

    private final AuthProperties.Netgsm properties;
    private final RestClient restClient;

    public NetgsmSmsOtpDeliveryAdapter(AuthProperties.Netgsm properties, RestClient restClient) {
        this.properties = properties;
        this.restClient = restClient;
    }

    @Override
    public void deliver(OtpDeliveryRequest request) {
        if (request.identifier().type() != IdentifierTypeEnum.PHONE) {
            throw IdentityErrors.smsProviderDisabled();
        }

        URI uri = UriComponentsBuilder.fromUriString(properties.getEndpoint())
                .queryParam("usercode", properties.getUsername())
                .queryParam("password", properties.getPassword())
                .queryParam("gsmno", normalizePhone(request.identifier().value()))
                .queryParam("message", properties.getMessageTemplate().formatted(request.otp()))
                .queryParam("msgheader", properties.getHeader())
                .build(true)
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
