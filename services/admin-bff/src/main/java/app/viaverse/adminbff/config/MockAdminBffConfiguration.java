package app.viaverse.adminbff.config;

import app.viaverse.adminbff.application.AdminBusinessProfileProxy;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;

@Configuration
@Profile("mock")
public class MockAdminBffConfiguration {

    @Bean
    @Primary
    AdminBusinessProfileProxy mockAdminBusinessProfileProxy(
            @Qualifier("adminProfileRestClient") RestClient restClient,
            AdminBffProperties properties
    ) {
        return new AdminBusinessProfileProxy(restClient, properties) {
            @Override
            public ProxyResponse get(String path) {
                return new ProxyResponse(HttpStatus.OK, Map.of(
                        "success", true,
                        "items", List.of(Map.of(
                                "accountId", "00000000-0000-0000-0000-000000000001",
                                "displayName", "Mock Provider",
                                "status", "PENDING_REVIEW"
                        )),
                        "path", path
                ));
            }

            @Override
            public ProxyResponse post(String path, Object body) {
                return new ProxyResponse(HttpStatus.OK, Map.of(
                        "success", true,
                        "path", path
                ));
            }
        };
    }
}
