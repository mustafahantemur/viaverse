package app.viaverse.profile.profile.infrastructure.adapter.out.persistence.repository;

import app.viaverse.profile.profile.domain.enums.BusinessVerificationStatusEnum;
import app.viaverse.profile.profile.infrastructure.adapter.out.persistence.entity.BusinessProfileJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessProfileJpaRepository extends JpaRepository<BusinessProfileJpaEntity, UUID> {

    List<BusinessProfileJpaEntity> findAllByVerificationStatusOrderByUpdatedAtAsc(
            BusinessVerificationStatusEnum verificationStatus
    );
}
