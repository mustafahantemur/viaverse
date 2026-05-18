package app.viaverse.profile.profile.infrastructure.adapter.in.web.controller;

import app.viaverse.profile.profile.application.port.in.GetCurrentPreferencesUseCase;
import app.viaverse.profile.profile.application.port.in.PutCurrentPreferenceUseCase;
import app.viaverse.profile.profile.domain.model.ProfilePreference;
import app.viaverse.web.api.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/me/preferences")
public class ProfilePreferenceController {

    private final GetCurrentPreferencesUseCase getCurrentPreferencesUseCase;
    private final PutCurrentPreferenceUseCase putCurrentPreferenceUseCase;
    private final ObjectMapper objectMapper;

    public ProfilePreferenceController(
            GetCurrentPreferencesUseCase getCurrentPreferencesUseCase,
            PutCurrentPreferenceUseCase putCurrentPreferenceUseCase,
            ObjectMapper objectMapper
    ) {
        this.getCurrentPreferencesUseCase = getCurrentPreferencesUseCase;
        this.putCurrentPreferenceUseCase = putCurrentPreferenceUseCase;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> getCurrent(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> decoded = new LinkedHashMap<>();
        getCurrentPreferencesUseCase.execute(accountId(jwt)).forEach((key, valueJson) ->
                decoded.put(key, readJson(valueJson))
        );
        return ApiResponse.ok(decoded);
    }

    @PutMapping("/{key}")
    public ApiResponse<Object> putCurrent(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String key,
            @RequestBody Object value
    ) {
        ProfilePreference saved = putCurrentPreferenceUseCase.execute(new PutCurrentPreferenceUseCase.Command(
                accountId(jwt),
                key,
                writeJson(value)
        ));
        return ApiResponse.ok(readJson(saved.getValueJson()));
    }

    private UUID accountId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Preference value must be JSON serializable", exception);
        }
    }

    private Object readJson(String valueJson) {
        try {
            return objectMapper.readValue(valueJson, new TypeReference<>() {});
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Stored preference value is not valid JSON", exception);
        }
    }
}
