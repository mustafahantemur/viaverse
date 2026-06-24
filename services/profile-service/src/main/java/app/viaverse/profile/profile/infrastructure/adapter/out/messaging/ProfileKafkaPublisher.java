package app.viaverse.profile.profile.infrastructure.adapter.out.messaging;

import app.viaverse.contracts.profile.profile.ProfileCreatedV1KafkaEvent;
import app.viaverse.contracts.profile.profile.ProfileEventTypes;
import app.viaverse.contracts.profile.profile.ProfileBlockedV1KafkaEvent;
import app.viaverse.contracts.profile.profile.ProfileCapabilityDisabledV1KafkaEvent;
import app.viaverse.contracts.profile.profile.ProfileCapabilityEnabledV1KafkaEvent;
import app.viaverse.contracts.profile.profile.ProfileBusinessApprovedV1KafkaEvent;
import app.viaverse.contracts.profile.profile.ProfileBusinessRejectedV1KafkaEvent;
import app.viaverse.contracts.profile.profile.ProfileBusinessSubmittedV1KafkaEvent;
import app.viaverse.contracts.profile.profile.ProfileUnblockedV1KafkaEvent;
import app.viaverse.contracts.profile.profile.ProfileUpdatedV1KafkaEvent;
import app.viaverse.messaging.outbox.OutboxEventWriter;
import app.viaverse.profile.profile.application.port.out.ProfileEventPublisher;
import app.viaverse.profile.profile.domain.model.Profile;
import app.viaverse.profile.profile.domain.model.ProfileBlock;
import app.viaverse.profile.profile.domain.model.ProfileCapability;
import app.viaverse.profile.profile.domain.model.BusinessProfile;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ProfileKafkaPublisher implements ProfileEventPublisher {

    private static final String BINDING_NAME = "profileEvents-out-0";

    private final OutboxEventWriter outboxWriter;
    private final Clock clock;

    public ProfileKafkaPublisher(OutboxEventWriter outboxWriter, Clock clock) {
        this.outboxWriter = outboxWriter;
        this.clock = clock;
    }

    @Override
    public void publishCreated(Profile profile) {
        UUID eventId = UUID.randomUUID();
        outboxWriter.enqueue(
                eventId,
                ProfileEventTypes.PROFILE_CREATED_V1,
                BINDING_NAME,
                profile.getAccountId().toString(),
                new ProfileCreatedV1KafkaEvent(
                        eventId,
                        Instant.now(clock),
                        "v1",
                        profile.getAccountId(),
                        profile.getDisplayName(),
                        profile.getPublicVisibility().name()
                )
        );
    }

    @Override
    public void publishUpdated(Profile profile) {
        UUID eventId = UUID.randomUUID();
        outboxWriter.enqueue(
                eventId,
                ProfileEventTypes.PROFILE_UPDATED_V1,
                BINDING_NAME,
                profile.getAccountId().toString(),
                new ProfileUpdatedV1KafkaEvent(
                        eventId,
                        Instant.now(clock),
                        "v1",
                        profile.getAccountId(),
                        profile.getDisplayName(),
                        profile.getFirstName(),
                        profile.getLastName(),
                        profile.getAvatarMediaId(),
                        profile.getHeadline(),
                        profile.getPublicVisibility().name()
                )
        );
    }

    @Override
    public void publishCapabilityEnabled(ProfileCapability capability) {
        UUID eventId = UUID.randomUUID();
        outboxWriter.enqueue(
                eventId,
                ProfileEventTypes.PROFILE_CAPABILITY_ENABLED_V1,
                BINDING_NAME,
                capability.getAccountId().toString(),
                new ProfileCapabilityEnabledV1KafkaEvent(
                        eventId,
                        Instant.now(clock),
                        "v1",
                        capability.getAccountId(),
                        capability.getCapability().name()
                )
        );
    }

    @Override
    public void publishCapabilityDisabled(ProfileCapability capability) {
        UUID eventId = UUID.randomUUID();
        outboxWriter.enqueue(
                eventId,
                ProfileEventTypes.PROFILE_CAPABILITY_DISABLED_V1,
                BINDING_NAME,
                capability.getAccountId().toString(),
                new ProfileCapabilityDisabledV1KafkaEvent(
                        eventId,
                        Instant.now(clock),
                        "v1",
                        capability.getAccountId(),
                        capability.getCapability().name()
                )
        );
    }

    @Override
    public void publishBusinessSubmitted(BusinessProfile businessProfile) {
        UUID eventId = UUID.randomUUID();
        outboxWriter.enqueue(
                eventId,
                ProfileEventTypes.PROFILE_BUSINESS_SUBMITTED_V1,
                BINDING_NAME,
                businessProfile.getAccountId().toString(),
                new ProfileBusinessSubmittedV1KafkaEvent(
                        eventId,
                        Instant.now(clock),
                        "v1",
                        businessProfile.getAccountId(),
                        businessProfile.getTradeName(),
                        businessProfile.getSector().name()
                )
        );
    }

    @Override
    public void publishBusinessApproved(BusinessProfile businessProfile) {
        UUID eventId = UUID.randomUUID();
        outboxWriter.enqueue(
                eventId,
                ProfileEventTypes.PROFILE_BUSINESS_APPROVED_V1,
                BINDING_NAME,
                businessProfile.getAccountId().toString(),
                new ProfileBusinessApprovedV1KafkaEvent(
                        eventId,
                        Instant.now(clock),
                        "v1",
                        businessProfile.getAccountId(),
                        businessProfile.getTradeName(),
                        businessProfile.getSector().name()
                )
        );
    }

    @Override
    public void publishBusinessRejected(BusinessProfile businessProfile) {
        UUID eventId = UUID.randomUUID();
        outboxWriter.enqueue(
                eventId,
                ProfileEventTypes.PROFILE_BUSINESS_REJECTED_V1,
                BINDING_NAME,
                businessProfile.getAccountId().toString(),
                new ProfileBusinessRejectedV1KafkaEvent(
                        eventId,
                        Instant.now(clock),
                        "v1",
                        businessProfile.getAccountId(),
                        businessProfile.getRejectionReason()
                )
        );
    }

    @Override
    public void publishBlocked(ProfileBlock block) {
        UUID eventId = UUID.randomUUID();
        outboxWriter.enqueue(
                eventId,
                ProfileEventTypes.PROFILE_BLOCKED_V1,
                BINDING_NAME,
                block.getBlockerAccountId().toString(),
                new ProfileBlockedV1KafkaEvent(
                        eventId,
                        Instant.now(clock),
                        "v1",
                        block.getBlockerAccountId(),
                        block.getBlockedAccountId()
                )
        );
    }

    @Override
    public void publishUnblocked(ProfileBlock block) {
        UUID eventId = UUID.randomUUID();
        outboxWriter.enqueue(
                eventId,
                ProfileEventTypes.PROFILE_UNBLOCKED_V1,
                BINDING_NAME,
                block.getBlockerAccountId().toString(),
                new ProfileUnblockedV1KafkaEvent(
                        eventId,
                        Instant.now(clock),
                        "v1",
                        block.getBlockerAccountId(),
                        block.getBlockedAccountId()
                )
        );
    }
}
