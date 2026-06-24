package app.viaverse.content.post.application.usecase;

import app.viaverse.content.post.application.model.SocialFeedItem;
import app.viaverse.content.post.application.port.in.ListSocialFeedUseCase;
import app.viaverse.content.post.application.port.out.ContentInteractionRepository;
import app.viaverse.content.post.application.port.out.ContentPostRepository;
import app.viaverse.content.post.domain.enums.ContentPostTypeEnum;
import app.viaverse.content.post.domain.model.ContentPost;
import app.viaverse.web.logging.ObservedAction;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ListSocialFeedUseCaseImpl implements ListSocialFeedUseCase {
    private final ContentPostRepository postRepository;
    private final ContentInteractionRepository interactionRepository;
    private final Clock clock;

    public ListSocialFeedUseCaseImpl(
            ContentPostRepository postRepository,
            ContentInteractionRepository interactionRepository,
            Clock clock
    ) {
        this.postRepository = postRepository;
        this.interactionRepository = interactionRepository;
        this.clock = clock;
    }

    @Override
    @ObservedAction("content.feed.social.list")
    public List<SocialFeedItem> execute(Command command) {
        Set<UUID> suppressed = interactionRepository.findSuppressedPostIds(command.viewerAccountId());
        Instant now = clock.instant();
        // Candidate retrieval stays intentionally broader than final ranking:
        // city narrows the pool, while district remains a preference signal so
        // nearby but still relevant posts are not thrown away too early.
        return postRepository.findAllPublished(command.city(), null).stream()
                .filter(post -> !suppressed.contains(post.getId()))
                .map(post -> rank(post, command.city(), command.district(), now))
                .sorted(Comparator.comparingInt(SocialFeedItem::score).reversed()
                        .thenComparing(item -> item.post().getPublishedAt(), Comparator.reverseOrder()))
                .toList();
    }

    private SocialFeedItem rank(ContentPost post, String city, String district, Instant now) {
        int score = 0;
        String reason = "RECENCY";
        if (district != null && !district.isBlank() && district.equalsIgnoreCase(post.getDistrict())) {
            score += 60;
            reason = "SAME_DISTRICT";
        } else if (city != null && !city.isBlank() && city.equalsIgnoreCase(post.getCity())) {
            score += 30;
            reason = "SAME_CITY";
        }
        long ageHours = Math.max(0, Duration.between(post.getPublishedAt(), now).toHours());
        score += Math.max(0, 24 - (int) Math.min(ageHours, 24));
        if (post.getPostType() == ContentPostTypeEnum.EVENT
                && post.getEventStartsAt() != null
                && !post.getEventStartsAt().isBefore(now.minus(Duration.ofHours(2)))) {
            score += 20;
            reason = "UPCOMING_EVENT";
        }
        if (post.getPostType() == ContentPostTypeEnum.ANNOUNCEMENT) {
            score += 5;
        }
        return new SocialFeedItem(post, score, reason);
    }
}
