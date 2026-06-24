package app.viaverse.profile.profile.infrastructure.adapter.in.web.dto.request;

import app.viaverse.profile.profile.domain.enums.ActiveModeEnum;
import jakarta.validation.constraints.NotNull;

public record UpdateActiveModeRequest(@NotNull ActiveModeEnum activeMode) {
}
