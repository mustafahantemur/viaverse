package app.viaverse.mockwebbff.app;

import app.viaverse.mockwebbff.app.AppDtos.PatchProfileRequest;
import app.viaverse.mockwebbff.app.AppDtos.PatchSettingsRequest;
import app.viaverse.mockwebbff.app.AppDtos.ProfileView;
import app.viaverse.mockwebbff.app.AppDtos.SettingsView;
import app.viaverse.mockwebbff.app.AppDtos.UserView;
import org.springframework.stereotype.Service;

@Service
public class MockProfileService extends MockDomainService {

    public MockProfileService(MockAppRepository repository) {
        super(repository);
    }

    public synchronized ProfileView profile() {
        return currentProfile(state());
    }

    public synchronized ProfileView patchProfile(PatchProfileRequest request) {
        MockAppState state = state();
        ProfileView current = currentProfile(state);
        String activeCapability = request.activeCapability() == null
            ? current.activeCapability()
            : request.activeCapability();
        if (!capabilityEnabled(current, activeCapability)) {
            throw badRequest("Selected capability is not enabled for this persona");
        }
        ProfileView updated = new ProfileView(
            state.currentUserId(),
            fallback(request.displayName(), current.displayName()),
            fallback(request.headline(), current.headline()),
            fallback(request.bio(), current.bio()),
            fallback(request.locationLabel(), current.locationLabel()),
            activeCapability,
            current.capabilities(),
            current.individualProviderProfile(),
            current.businessProfile(),
            current.trustScore(),
            current.completionScore()
        );
        replaceProfile(state, updated);
        replaceUserForProfile(state, updated);
        repository.save(state);
        return updated;
    }

    public synchronized SettingsView settings() {
        return state().settings();
    }

    public synchronized SettingsView patchSettings(PatchSettingsRequest request) {
        MockAppState state = state();
        SettingsView current = state.settings();
        SettingsView updated = new SettingsView(
            request.pushNotifications() == null ? current.pushNotifications() : request.pushNotifications(),
            request.emailDigest() == null ? current.emailDigest() : request.emailDigest(),
            request.requestUpdates() == null ? current.requestUpdates() : request.requestUpdates(),
            fallback(request.privacyLevel(), current.privacyLevel()),
            fallback(request.language(), current.language()),
            fallback(request.theme(), current.theme())
        );
        MockAppState next = new MockAppState(
            state.currentUserId(),
            state.identityAccounts(), state.registrationDrafts(),
            state.users(), state.profiles(), updated,
            state.categories(), state.providers(), state.businesses(),
            state.feedItems(), state.postComments(),
            state.serviceRequests(), state.offers(),
            state.conversations(), state.messages(),
            state.transactions(), state.notifications(), state.savedSearches()
        );
        repository.save(next);
        return updated;
    }

    private boolean capabilityEnabled(ProfileView profile, String capabilityKey) {
        return profile.capabilities().stream()
            .anyMatch(capability -> capability.key().equals(capabilityKey) && capability.enabled());
    }

    private void replaceProfile(MockAppState state, ProfileView updated) {
        for (int i = 0; i < state.profiles().size(); i++) {
            if (state.profiles().get(i).accountId().equals(updated.accountId())) {
                state.profiles().set(i, updated);
                return;
            }
        }
        throw notFound("Profile not found");
    }

    private void replaceUserForProfile(MockAppState state, ProfileView profile) {
        for (int i = 0; i < state.users().size(); i++) {
            UserView user = state.users().get(i);
            if (user.id().equals(profile.accountId())) {
                String activeLabel = user.capabilities().stream()
                    .filter(capability -> capability.key().equals(profile.activeCapability()))
                    .findFirst()
                    .map(AppDtos.CapabilityView::label)
                    .orElse(user.activeCapabilityLabel());
                state.users().set(i, new UserView(
                    user.id(), profile.displayName(), user.firstName(), user.lastName(),
                    initials(profile.displayName()), profile.activeCapability(), activeLabel,
                    profile.locationLabel(), user.capabilities()
                ));
                return;
            }
        }
        throw notFound("User not found");
    }
}
