package app.viaverse.identity.auth.api.mapper;

import app.viaverse.identity.auth.api.dto.response.SessionView;
import app.viaverse.identity.auth.domain.model.AuthSession;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SessionDtoMapper {

    @Mapping(target = "sessionId", source = "session.id")
    @Mapping(target = "deviceName", source = "session.deviceName")
    @Mapping(target = "platform", source = "session.platform")
    @Mapping(target = "lastSeenAt", source = "session.lastSeenAt")
    @Mapping(target = "current", expression = "java(session.getId().equals(currentSessionId))")
    SessionView toView(AuthSession session, UUID currentSessionId);

    default List<SessionView> toViews(List<AuthSession> sessions, UUID currentSessionId) {
        return sessions.stream()
                .map(session -> toView(session, currentSessionId))
                .collect(Collectors.toList());
    }
}
