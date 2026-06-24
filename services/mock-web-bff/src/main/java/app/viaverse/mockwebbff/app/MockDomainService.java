package app.viaverse.mockwebbff.app;

import app.viaverse.mockwebbff.app.AppDtos.FeedItemView;
import app.viaverse.mockwebbff.app.AppDtos.ProfileView;
import app.viaverse.mockwebbff.app.AppDtos.UserView;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public abstract class MockDomainService {

    protected final MockAppRepository repository;

    protected MockDomainService(MockAppRepository repository) {
        this.repository = repository;
    }

    protected MockAppState state() {
        MockAppState loaded = repository.loadOrSeed(SeedData::initialState);
        if (loaded.identityAccounts() == null
            || loaded.registrationDrafts() == null
            || loaded.postComments() == null
            || loaded.savedSearches() == null) {
            return repository.reset(SeedData::initialState);
        }
        return migrateFeedMedia(loaded);
    }

    protected UserView currentUser(MockAppState state) {
        return findUser(state, state.currentUserId());
    }

    protected ProfileView currentProfile(MockAppState state) {
        return state.profiles().stream()
            .filter(profile -> profile.accountId().equals(state.currentUserId()))
            .findFirst()
            .orElseThrow(() -> notFound("Profile not found"));
    }

    protected UserView findUser(MockAppState state, String userId) {
        return state.users().stream()
            .filter(user -> user.id().equals(userId))
            .findFirst()
            .orElseThrow(() -> notFound("User persona not found"));
    }

    protected String now() {
        return Instant.now().toString();
    }

    protected String fallback(String value, String fallbackValue) {
        return isBlank(value) ? fallbackValue : value.trim();
    }

    protected boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    protected boolean equalsNullable(String left, String right) {
        return left == null ? right == null : left.equals(right);
    }

    protected String initials(String displayName) {
        if (isBlank(displayName)) return "?";
        String[] parts = displayName.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase(Locale.ROOT);
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase(Locale.ROOT);
    }

    protected String firstNameFromDisplay(String displayName) {
        if (isBlank(displayName)) return "";
        return displayName.trim().split("\\s+")[0];
    }

    protected void requireText(String value, String fieldName) {
        if (isBlank(value)) throw badRequest(fieldName + " is required");
    }

    protected ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    protected ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    private MockAppState migrateFeedMedia(MockAppState state) {
        boolean changed = false;
        List<FeedItemView> feedItems = new ArrayList<>(state.feedItems());
        for (int i = 0; i < feedItems.size(); i++) {
            FeedItemView item = feedItems.get(i);
            String mediaUrl = item.mediaUrl();
            String mediaType = item.mediaType();
            if (List.of("feed-market-event", "feed-rain-alert").contains(item.id())) {
                mediaUrl = "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4";
                mediaType = "VIDEO";
            } else if (isBlank(mediaType)) {
                mediaType = normalizeMediaType(null, mediaUrl);
            }
            if (!equalsNullable(mediaUrl, item.mediaUrl()) || !equalsNullable(mediaType, item.mediaType())) {
                feedItems.set(i, new FeedItemView(
                    item.id(), item.type(), item.typeLabel(), item.title(), item.body(),
                    item.authorName(), item.authorType(), item.locationLabel(), item.categoryId(),
                    item.createdAt(), item.likeCount(), item.liked(), item.commentCount(),
                    item.shareCount(), item.saved(), item.highlight(), item.relatedRequestId(),
                    item.hashtags(), mediaUrl, mediaType
                ));
                changed = true;
            }
        }
        if (!changed) return state;
        MockAppState migrated = withFeedItems(state, feedItems);
        repository.save(migrated);
        return migrated;
    }

    protected MockAppState withFeedItems(MockAppState state, List<FeedItemView> feedItems) {
        return new MockAppState(
            state.currentUserId(), state.identityAccounts(), state.registrationDrafts(),
            state.users(), state.profiles(), state.settings(), state.categories(),
            state.providers(), state.businesses(), feedItems, state.postComments(),
            state.serviceRequests(), state.offers(), state.conversations(), state.messages(),
            state.transactions(), state.notifications(), state.savedSearches()
        );
    }

    protected List<String> normalizeHashtags(List<String> explicitHashtags, String body) {
        ArrayList<String> tags = new ArrayList<>();
        if (explicitHashtags != null) {
            explicitHashtags.stream().map(this::cleanHashtag).filter(tag -> !tag.isEmpty()).forEach(tags::add);
        }
        if (body != null) {
            for (String token : body.split("\\s+")) {
                if (token.startsWith("#")) {
                    String tag = cleanHashtag(token);
                    if (!tag.isEmpty() && !tags.contains(tag)) tags.add(tag);
                }
            }
        }
        return tags;
    }

    protected String cleanHashtag(String raw) {
        if (raw == null) return "";
        return raw.replace("#", "").replaceAll("[^\\p{L}\\p{N}_-]", "").toLowerCase(Locale.forLanguageTag("tr-TR"));
    }

    protected String normalizeMediaType(String rawType, String mediaUrl) {
        if (!isBlank(rawType)) {
            return "VIDEO".equals(rawType.trim().toUpperCase(Locale.ROOT)) ? "VIDEO" : "IMAGE";
        }
        if (!isBlank(mediaUrl)) {
            String url = mediaUrl.trim().toLowerCase(Locale.ROOT);
            if (url.startsWith("data:video") || url.endsWith(".mp4") || url.endsWith(".webm") || url.endsWith(".mov")) {
                return "VIDEO";
            }
        }
        return "IMAGE";
    }
}
