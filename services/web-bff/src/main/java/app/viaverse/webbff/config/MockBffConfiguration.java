package app.viaverse.webbff.config;

import app.viaverse.webbff.content.ContentProxy;
import app.viaverse.webbff.identity.IdentityProxy;
import app.viaverse.webbff.identity.JsonBodyParser;
import app.viaverse.webbff.marketplace.MarketplaceProxy;
import app.viaverse.webbff.media.MediaProxy;
import app.viaverse.webbff.profile.ProfileProxy;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;

@Configuration
@Profile("mock")
public class MockBffConfiguration {

    @Bean
    @Primary
    IdentityProxy mockIdentityProxy(
            @Qualifier("identityRestClient") RestClient restClient,
            JsonBodyParser jsonBodyParser
    ) {
        return new IdentityProxy(restClient, jsonBodyParser) {
            @Override
            public ProxyResponse exchange(HttpMethod method, String path, Object body, String authorization) {
                if (path.endsWith("/password-login") || path.endsWith("/register")
                        || path.endsWith("/verify-totp") || path.endsWith("/refresh")
                        || path.endsWith("/register/verify-email") || path.endsWith("/register/verify-phone")) {
                    return new ProxyResponse(HttpStatus.OK, Map.of(
                            "success", true,
                            "accessToken", "mock-access-token",
                            "refreshToken", "mock-refresh-token",
                            "account", mockAccount()
                    ));
                }

                if (path.endsWith("/register/start") || path.endsWith("/start")) {
                    return new ProxyResponse(HttpStatus.OK, Map.of(
                            "success", true,
                            "flowId", "mock-flow-id",
                            "nextStep", "VERIFY_EMAIL"
                    ));
                }

                if (path.endsWith("/logout")) {
                    return new ProxyResponse(HttpStatus.OK, Map.of("success", true));
                }

                return new ProxyResponse(HttpStatus.OK, Map.of(
                        "success", true,
                        "mock", true,
                        "path", path
                ));
            }

            @Override
            public ProxyResponse get(String path, String authorization) {
                if (path.endsWith("/required-consents")) {
                    return new ProxyResponse(HttpStatus.OK, Map.of(
                            "success", true,
                            "items", List.of("terms-of-service", "personal-data-protection")
                    ));
                }

                if (path.endsWith("/capability-terms")) {
                    return new ProxyResponse(HttpStatus.OK, Map.of(
                            "success", true,
                            "items", List.of("provider-terms", "business-terms")
                    ));
                }

                return new ProxyResponse(HttpStatus.OK, Map.of(
                        "success", true,
                        "account", mockAccount()
                ));
            }
        };
    }

    @Bean
    @Primary
    ProfileProxy mockProfileProxy(
            @Qualifier("profileRestClient") RestClient restClient,
            JsonBodyParser jsonBodyParser
    ) {
        return new ProfileProxy(restClient, jsonBodyParser) {
            @Override
            public ProxyResponse exchange(HttpMethod method, String path, Object body, String authorization) {
                return response(path);
            }

            @Override
            public ProxyResponse get(String path, String authorization) {
                return response(path);
            }

            private ProxyResponse response(String path) {
                return new ProxyResponse(HttpStatus.OK, Map.of(
                        "success", true,
                        "profile", Map.of(
                                "accountId", "00000000-0000-0000-0000-000000000001",
                                "displayName", "Mock Viaverse User",
                                "city", "Istanbul",
                                "providerReady", true
                        ),
                        "path", path
                ));
            }
        };
    }

    @Bean
    @Primary
    ContentProxy mockContentProxy(
            @Qualifier("contentRestClient") RestClient restClient,
            JsonBodyParser jsonBodyParser
    ) {
        return new ContentProxy(restClient, jsonBodyParser) {
            @Override
            public ProxyResponse exchange(HttpMethod method, String path, Object body, String authorization) {
                return response(path);
            }

            @Override
            public ProxyResponse get(String path, String authorization) {
                return response(path);
            }

            private ProxyResponse response(String path) {
                return new ProxyResponse(HttpStatus.OK, Map.of(
                        "success", true,
                        "items", List.of(Map.of(
                                "id", "mock-post-1",
                                "title", "Mock content",
                                "summary", "Standalone BFF mock response"
                        )),
                        "path", path
                ));
            }
        };
    }

    @Bean
    @Primary
    MarketplaceProxy mockMarketplaceProxy(
            @Qualifier("marketplaceRestClient") RestClient restClient,
            JsonBodyParser jsonBodyParser
    ) {
        return new MarketplaceProxy(restClient, jsonBodyParser) {
            @Override
            public ProxyResponse exchange(HttpMethod method, String path, Object body, String authorization) {
                return response(path);
            }

            @Override
            public ProxyResponse get(String path, String authorization) {
                return response(path);
            }

            private ProxyResponse response(String path) {
                return new ProxyResponse(HttpStatus.OK, Map.of(
                        "success", true,
                        "items", List.of(Map.of(
                                "id", "mock-listing-1",
                                "title", "Mock marketplace listing"
                        )),
                        "path", path
                ));
            }
        };
    }

    @Bean
    @Primary
    MediaProxy mockMediaProxy(
            @Qualifier("mediaRestClient") RestClient restClient,
            JsonBodyParser jsonBodyParser
    ) {
        return new MediaProxy(restClient, jsonBodyParser) {
            @Override
            public ProxyResponse exchange(HttpMethod method, String path, Object body, String authorization) {
                return response(path);
            }

            @Override
            public ProxyResponse get(String path, String authorization) {
                return response(path);
            }

            private ProxyResponse response(String path) {
                return new ProxyResponse(HttpStatus.OK, Map.of(
                        "success", true,
                        "assets", List.of(),
                        "path", path
                ));
            }
        };
    }

    private static Map<String, Object> mockAccount() {
        return Map.of(
                "id", "00000000-0000-0000-0000-000000000001",
                "displayName", "Mock Viaverse User",
                "email", "mock.user@viaverse.local"
        );
    }
}
