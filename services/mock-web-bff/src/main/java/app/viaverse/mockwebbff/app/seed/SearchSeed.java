package app.viaverse.mockwebbff.app.seed;

import app.viaverse.mockwebbff.app.AppDtos.SavedSearchView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class SearchSeed {

    private SearchSeed() {
    }

    public static ArrayList<SavedSearchView> savedSearches() {
        return new ArrayList<>(List.of(
            new SavedSearchView(
                "saved-search-electric",
                "user-standard",
                "feed",
                "Kadıköy kesinti ve ulaşım",
                Map.of("query", "#kadikoy", "type", "UTILITY", "radiusKm", "5"),
                SeedClock.minutesAgo(460)
            ),
            new SavedSearchView(
                "saved-search-cleaning",
                "user-standard",
                "services",
                "4.7+ temizlik profilleri",
                Map.of("categoryId", "cleaning", "minRating", "4.7", "radiusKm", "8"),
                SeedClock.minutesAgo(840)
            )
        ));
    }
}
