package app.viaverse.mockwebbff.app;

import app.viaverse.mockwebbff.app.seed.ActivitySeed;
import app.viaverse.mockwebbff.app.seed.CategorySeed;
import app.viaverse.mockwebbff.app.seed.FinanceSeed;
import app.viaverse.mockwebbff.app.seed.IdentitySeed;
import app.viaverse.mockwebbff.app.seed.MessagingSeed;
import app.viaverse.mockwebbff.app.seed.PersonaSeed;
import app.viaverse.mockwebbff.app.seed.ProfileSeed;
import app.viaverse.mockwebbff.app.seed.SearchSeed;
import app.viaverse.mockwebbff.app.seed.ServiceDiscoverySeed;
import app.viaverse.mockwebbff.app.seed.SocialSeed;
import app.viaverse.mockwebbff.app.seed.WorkSeed;

public final class SeedData {

    private SeedData() {
    }

    public static MockAppState initialState() {
        var users = PersonaSeed.users();
        var categories = CategorySeed.categories();
        var requests = WorkSeed.requests();
        var offers = WorkSeed.offers();

        return new MockAppState(
            "user-standard",
            IdentitySeed.accounts(),
            IdentitySeed.registrationDrafts(),
            users,
            ProfileSeed.profiles(users),
            ProfileSeed.settings(),
            categories,
            ServiceDiscoverySeed.providers(),
            ServiceDiscoverySeed.businesses(),
            SocialSeed.feedItems(),
            SocialSeed.comments(),
            requests,
            offers,
            MessagingSeed.conversations(),
            MessagingSeed.messages(),
            FinanceSeed.transactions(),
            ActivitySeed.notifications(),
            SearchSeed.savedSearches()
        );
    }
}
