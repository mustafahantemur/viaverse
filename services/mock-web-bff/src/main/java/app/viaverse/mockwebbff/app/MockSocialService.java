package app.viaverse.mockwebbff.app;

import app.viaverse.mockwebbff.app.AppDtos.AnnouncementIncidentView;
import app.viaverse.mockwebbff.app.AppDtos.CreateCommentRequest;
import app.viaverse.mockwebbff.app.AppDtos.CreatePostRequest;
import app.viaverse.mockwebbff.app.AppDtos.FeedItemView;
import app.viaverse.mockwebbff.app.AppDtos.HashtagSuggestionView;
import app.viaverse.mockwebbff.app.AppDtos.MockPhotoView;
import app.viaverse.mockwebbff.app.AppDtos.PostCommentView;
import app.viaverse.mockwebbff.app.AppDtos.SponsoredAdView;
import app.viaverse.mockwebbff.app.AppDtos.UpdatePostRequest;
import app.viaverse.mockwebbff.app.AppDtos.UserView;
import app.viaverse.mockwebbff.app.seed.SocialSeed;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import org.springframework.stereotype.Service;

@Service
public class MockSocialService extends MockDomainService {

    public MockSocialService(MockAppRepository repository) {
        super(repository);
    }

    public synchronized List<FeedItemView> feed(String type) {
        return state().feedItems().stream()
            .filter(feedFilter(type))
            .sorted(Comparator.comparing(FeedItemView::createdAt).reversed())
            .toList();
    }

    public synchronized FeedItemView createPost(CreatePostRequest request) {
        requireText(request == null ? null : request.body(), "body");
        MockAppState state = state();
        UserView user = currentUser(state);
        String type = normalizeContentType(request.type());
        String now = now();
        String id = "feed-" + UUID.randomUUID();
        FeedItemView item = new FeedItemView(
            id, type, typeLabel(type),
            fallback(request.title(), defaultTitle(type)),
            request.body().trim(),
            user.displayName(), user.activeCapabilityLabel(),
            fallback(request.locationScope(), user.locationLabel()),
            fallback(request.categoryId(), "local-help"),
            now, 0, false, 0, 0, false, typeLabel(type), null,
            normalizeHashtags(request.hashtags(), request.body()),
            fallback(request.mediaUrl(), null),
            normalizeMediaType(request.mediaType(), request.mediaUrl())
        );
        state.feedItems().add(item);
        repository.save(state);
        return item;
    }

    public synchronized FeedItemView updatePost(String postId, UpdatePostRequest request) {
        requireText(request == null ? null : request.body(), "body");
        MockAppState state = state();
        FeedItemView current = findFeedItem(state, postId);
        UserView user = currentUser(state);
        if (!current.authorName().equals(user.displayName())) {
            throw badRequest("Only the current user's mock posts can be edited");
        }
        String type = normalizeContentType(fallback(request.type(), current.type()));
        FeedItemView updated = new FeedItemView(
            current.id(), type, typeLabel(type),
            fallback(request.title(), current.title()),
            request.body().trim(),
            current.authorName(), current.authorType(),
            fallback(request.locationScope(), current.locationLabel()),
            fallback(request.categoryId(), current.categoryId()),
            current.createdAt(), current.likeCount(), current.liked(),
            current.commentCount(), current.shareCount(), current.saved(),
            typeLabel(type), current.relatedRequestId(),
            normalizeHashtags(request.hashtags(), request.body()),
            fallback(request.mediaUrl(), current.mediaUrl()),
            normalizeMediaType(fallback(request.mediaType(), current.mediaType()), fallback(request.mediaUrl(), current.mediaUrl()))
        );
        replaceFeedItem(state, updated);
        repository.save(state);
        return updated;
    }

    public synchronized FeedItemView likePost(String postId) {
        MockAppState state = state();
        FeedItemView current = findFeedItem(state, postId);
        boolean liked = !current.liked();
        int likeCount = Math.max(0, current.likeCount() + (liked ? 1 : -1));
        FeedItemView updated = withSocialState(current, likeCount, liked, current.commentCount(), current.shareCount(), current.saved());
        replaceFeedItem(state, updated);
        repository.save(state);
        return updated;
    }

    public synchronized FeedItemView savePost(String postId) {
        MockAppState state = state();
        FeedItemView current = findFeedItem(state, postId);
        FeedItemView updated = withSocialState(current, current.likeCount(), current.liked(), current.commentCount(), current.shareCount(), !current.saved());
        replaceFeedItem(state, updated);
        repository.save(state);
        return updated;
    }

    public synchronized FeedItemView sharePost(String postId) {
        MockAppState state = state();
        FeedItemView current = findFeedItem(state, postId);
        FeedItemView updated = withSocialState(current, current.likeCount(), current.liked(), current.commentCount(), current.shareCount() + 1, current.saved());
        replaceFeedItem(state, updated);
        repository.save(state);
        return updated;
    }

    public synchronized List<PostCommentView> comments(String postId) {
        return state().postComments().stream()
            .filter(comment -> comment.postId().equals(postId))
            .sorted(Comparator.comparing(PostCommentView::createdAt))
            .toList();
    }

    public synchronized PostCommentView createComment(String postId, CreateCommentRequest request) {
        requireText(request == null ? null : request.body(), "body");
        MockAppState state = state();
        FeedItemView current = findFeedItem(state, postId);
        UserView user = currentUser(state);
        PostCommentView comment = new PostCommentView(
            "comment-" + UUID.randomUUID(), postId,
            user.id(), user.displayName(),
            request.body().trim(), now()
        );
        state.postComments().add(comment);
        replaceFeedItem(state, withSocialState(current, current.likeCount(), current.liked(), current.commentCount() + 1, current.shareCount(), current.saved()));
        repository.save(state);
        return comment;
    }

    public synchronized List<HashtagSuggestionView> hashtagSuggestions(String query) {
        String normalizedQuery = cleanHashtag(query);
        Map<String, Integer> counts = new HashMap<>();
        Map<String, String> samples = new HashMap<>();
        for (FeedItemView item : state().feedItems()) {
            for (String tag : item.hashtags()) {
                if (normalizedQuery.isEmpty() || tag.contains(normalizedQuery)) {
                    counts.merge(tag, 1, Integer::sum);
                    samples.putIfAbsent(tag, item.title());
                }
            }
        }
        return counts.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
            .limit(10)
            .map(entry -> new HashtagSuggestionView(entry.getKey(), entry.getValue(), samples.get(entry.getKey())))
            .toList();
    }

    public synchronized List<MockPhotoView> mockPhotos(String query) {
        String normalized = query == null ? "" : query.trim().toLowerCase(Locale.forLanguageTag("tr-TR"));
        return SocialSeed.mockPhotos().stream()
            .filter(photo -> normalized.isEmpty()
                || photo.alt().toLowerCase(Locale.forLanguageTag("tr-TR")).contains(normalized)
                || photo.tags().stream().anyMatch(tag -> tag.contains(normalized)))
            .toList();
    }

    public synchronized List<SponsoredAdView> sponsoredAds(String surface) {
        return SocialSeed.sponsoredAds();
    }

    public synchronized List<FeedItemView> events() {
        return feed("EVENT");
    }

    public synchronized List<FeedItemView> announcements() {
        return feed("ANNOUNCEMENT");
    }

    public synchronized List<AnnouncementIncidentView> announcementIncidents() {
        return SocialSeed.announcementIncidents();
    }

    private Predicate<FeedItemView> feedFilter(String type) {
        if (isBlank(type) || "ALL".equalsIgnoreCase(type)) return item -> true;
        String normalized = type.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "SOCIAL" -> item -> !List.of("REQUEST", "OPPORTUNITY").contains(item.type());
            case "POST", "POSTS" -> item -> "POST".equals(item.type()) || "INFO".equals(item.type());
            case "HELP", "REQUEST" -> item -> "REQUEST".equals(item.type()) || "OPPORTUNITY".equals(item.type());
            case "ALERT" -> item -> "TRAFFIC".equals(item.type()) || "UTILITY".equals(item.type());
            default -> item -> normalized.equals(item.type());
        };
    }

    private String normalizeContentType(String rawType) {
        if (isBlank(rawType)) return "POST";
        String normalized = rawType.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "ANNOUNCEMENT", "EVENT", "REQUEST", "OPPORTUNITY", "INFO", "TRAFFIC", "UTILITY" -> normalized;
            default -> "POST";
        };
    }

    private FeedItemView withSocialState(FeedItemView item, int likeCount, boolean liked, int commentCount, int shareCount, boolean saved) {
        return new FeedItemView(
            item.id(), item.type(), item.typeLabel(), item.title(), item.body(),
            item.authorName(), item.authorType(), item.locationLabel(), item.categoryId(),
            item.createdAt(), likeCount, liked, commentCount, shareCount, saved,
            item.highlight(), item.relatedRequestId(), item.hashtags(), item.mediaUrl(), item.mediaType()
        );
    }

    private FeedItemView findFeedItem(MockAppState state, String postId) {
        return state.feedItems().stream()
            .filter(item -> item.id().equals(postId))
            .findFirst()
            .orElseThrow(() -> notFound("Post not found"));
    }

    private void replaceFeedItem(MockAppState state, FeedItemView updated) {
        for (int i = 0; i < state.feedItems().size(); i++) {
            if (state.feedItems().get(i).id().equals(updated.id())) {
                state.feedItems().set(i, updated);
                return;
            }
        }
        throw notFound("Post not found");
    }

    private String typeLabel(String type) {
        return switch (type) {
            case "ANNOUNCEMENT" -> "Duyuru";
            case "EVENT" -> "Etkinlik";
            case "TRAFFIC" -> "Trafik";
            case "UTILITY" -> "Kesinti";
            case "INFO" -> "Bilgi";
            case "REQUEST" -> "Talep";
            case "OPPORTUNITY" -> "Fırsat";
            default -> "Paylaşım";
        };
    }

    private String defaultTitle(String type) {
        return switch (type) {
            case "ANNOUNCEMENT" -> "Yeni duyuru";
            case "EVENT" -> "Yeni etkinlik";
            case "TRAFFIC" -> "Yeni ulaşım bilgisi";
            case "UTILITY" -> "Yeni kesinti bilgisi";
            case "INFO" -> "Yeni bilgi paylaşımı";
            case "REQUEST" -> "Yeni talep";
            default -> "Yeni paylaşım";
        };
    }
}
