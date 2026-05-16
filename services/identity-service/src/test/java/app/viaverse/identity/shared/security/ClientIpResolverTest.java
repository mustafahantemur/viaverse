package app.viaverse.identity.shared.security;

import static org.assertj.core.api.Assertions.assertThat;

import app.viaverse.identity.config.HttpProperties;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class ClientIpResolverTest {

    @Test
    void ignoresForwardedHeadersFromUntrustedPeers() {
        ClientIpResolver resolver = resolverWithTrustedProxies("10.0.0.0/8");
        MockHttpServletRequest request = request("198.51.100.10", "203.0.113.1, 10.0.0.5");

        assertThat(resolver.resolve(request)).isEqualTo("198.51.100.10");
    }

    @Test
    void returnsClosestUntrustedHopBehindTrustedProxyChain() {
        ClientIpResolver resolver = resolverWithTrustedProxies("10.0.0.0/8", "192.0.2.0/24");
        MockHttpServletRequest request = request("10.0.0.9", "203.0.113.20, 192.0.2.15");

        assertThat(resolver.resolve(request)).isEqualTo("203.0.113.20");
    }

    @Test
    void supportsStandardForwardedHeader() {
        ClientIpResolver resolver = resolverWithTrustedProxies("10.0.0.0/8");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("10.0.0.7");
        request.addHeader("Forwarded", "for=\"203.0.113.40\";proto=https, for=10.0.0.6");

        assertThat(resolver.resolve(request)).isEqualTo("203.0.113.40");
    }

    private ClientIpResolver resolverWithTrustedProxies(String... proxies) {
        HttpProperties properties = new HttpProperties();
        properties.getTrustedProxies().addAll(java.util.List.of(proxies));
        return new ClientIpResolver(properties);
    }

    private MockHttpServletRequest request(String remoteAddr, String xForwardedFor) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr(remoteAddr);
        request.addHeader("X-Forwarded-For", xForwardedFor);
        return request;
    }
}
