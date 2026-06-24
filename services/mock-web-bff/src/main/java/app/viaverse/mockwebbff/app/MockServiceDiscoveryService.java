package app.viaverse.mockwebbff.app;

import app.viaverse.mockwebbff.app.AppDtos.BusinessView;
import app.viaverse.mockwebbff.app.AppDtos.CreateSavedSearchRequest;
import app.viaverse.mockwebbff.app.AppDtos.ProviderView;
import app.viaverse.mockwebbff.app.AppDtos.SavedSearchView;
import app.viaverse.mockwebbff.app.AppDtos.ServiceCategoryView;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MockServiceDiscoveryService extends MockDomainService {

    public MockServiceDiscoveryService(MockAppRepository repository) {
        super(repository);
    }

    public synchronized List<ServiceCategoryView> categories() {
        return state().categories();
    }

    public synchronized List<ProviderView> providers() {
        return state().providers();
    }

    public synchronized ProviderView provider(String id) {
        return state().providers().stream()
            .filter(provider -> provider.id().equals(id))
            .findFirst()
            .orElseThrow(() -> notFound("Provider not found"));
    }

    public synchronized List<BusinessView> businesses() {
        return state().businesses();
    }

    public synchronized BusinessView business(String id) {
        return state().businesses().stream()
            .filter(business -> business.id().equals(id))
            .findFirst()
            .orElseThrow(() -> notFound("Business not found"));
    }

    public synchronized List<SavedSearchView> savedSearches(String surface) {
        MockAppState state = state();
        String currentUserId = state.currentUserId();
        String normalizedSurface = surface == null ? "" : surface.trim().toLowerCase(Locale.ROOT);
        return state.savedSearches().stream()
            .filter(search -> search.ownerId().equals(currentUserId))
            .filter(search -> normalizedSurface.isEmpty() || search.surface().equalsIgnoreCase(normalizedSurface))
            .sorted(Comparator.comparing(SavedSearchView::createdAt).reversed())
            .toList();
    }

    public synchronized SavedSearchView createSavedSearch(CreateSavedSearchRequest request) {
        requireText(request == null ? null : request.surface(), "surface");
        requireText(request.name(), "name");
        MockAppState state = state();
        SavedSearchView saved = new SavedSearchView(
            "saved-search-" + UUID.randomUUID(),
            state.currentUserId(),
            request.surface().trim().toLowerCase(Locale.ROOT),
            request.name().trim(),
            request.filters() == null ? Map.of() : request.filters(),
            now()
        );
        state.savedSearches().add(saved);
        repository.save(state);
        return saved;
    }
}
