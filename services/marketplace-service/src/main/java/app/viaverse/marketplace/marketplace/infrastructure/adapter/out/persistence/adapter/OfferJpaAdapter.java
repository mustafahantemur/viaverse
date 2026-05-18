package app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.adapter;

import app.viaverse.marketplace.marketplace.application.port.out.OfferRepository;
import app.viaverse.marketplace.marketplace.domain.model.Offer;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.mapper.MarketplaceJpaMapper;
import app.viaverse.marketplace.marketplace.infrastructure.adapter.out.persistence.repository.OfferJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class OfferJpaAdapter implements OfferRepository {

    private final OfferJpaRepository repository;
    private final MarketplaceJpaMapper mapper;

    public OfferJpaAdapter(
            OfferJpaRepository repository,
            MarketplaceJpaMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Offer save(Offer offer) {
        return mapper.toDomain(repository.save(mapper.toEntity(offer)));
    }

    @Override
    public List<Offer> saveAll(List<Offer> offers) {
        return repository.saveAll(offers.stream().map(mapper::toEntity).toList()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Offer> findById(UUID offerId) {
        return repository.findById(offerId).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Offer> findByRequestIdAndProviderAccountId(UUID requestId, UUID providerAccountId) {
        return repository.findByRequestIdAndProviderAccountId(requestId, providerAccountId)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Offer> findAllByRequestId(UUID requestId) {
        return repository.findAllByRequestIdOrderByCreatedAtDesc(requestId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
