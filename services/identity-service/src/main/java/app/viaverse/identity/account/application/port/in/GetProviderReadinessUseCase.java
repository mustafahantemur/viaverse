package app.viaverse.identity.account.application.port.in;

import java.util.UUID;

public interface GetProviderReadinessUseCase {

    Result execute(UUID accountId);

    record Result(UUID accountId, boolean active, boolean hasVerifiedIdentifier) {
    }
}
