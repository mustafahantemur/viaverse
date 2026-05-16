package app.viaverse.identity.consent.infrastructure.adapter.out.persistence.mapper;

import app.viaverse.identity.consent.application.port.out.ConsentRecordRepository;
import app.viaverse.identity.consent.domain.ConsentCategory;
import app.viaverse.identity.consent.infrastructure.adapter.out.persistence.entity.ConsentRecordJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = ConsentCategory.class)
public interface ConsentRecordJpaMapper {

    @Mapping(target = "consentType", source = "type")
    @Mapping(target = "consentCategory", expression = "java(ConsentCategory.valueOf(record.category()))")
    @Mapping(target = "now", source = "recordedAt")
    ConsentRecordJpaEntity toEntity(ConsentRecordRepository.Record record);
}
