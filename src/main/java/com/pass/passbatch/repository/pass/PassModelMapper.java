package com.pass.passbatch.repository.pass;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * BulkPassEntity 객체를 PassEntity 객체로 변환하는 메서드
 * bulkPassEntity: 변환할 BulkPassEntity 객체
 * userId: PassEntity 객체의 userId 필드에 설정될 값
 * @Mapping 어노테이션을 사용하여 필드 매핑 설정
 * target: PassEntity 객체의 필드명
 * source: BulkPassEntity 객체의 필드명 또는 메서드 이름
 * qualifiedByName: 메서드 이름을 참조하는 경우 사용
 * remainingCount 필드는 bulkPassEntity.count 값으로 설정
 * status 필드는 defaultStatus 메서드를 통해 설정
 * status(BulkPassStatus status):
 *
 * BulkPassEntity의 status 필드에 관계없이 PassEntity의 status 필드를 READY로 설정하는 메서드
 * @Named 어노테이션을 사용하여 메서드 이름을 지정
 */
// ReportingPolicy.IGNORE: 일치하지 않은 필드를 무시합니다.
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PassModelMapper {
    PassModelMapper INSTANCE = Mappers.getMapper(PassModelMapper.class);

    // 필드명이 같지 않거나 custom하게 매핑해주기 위해서는 @Mapping을 추가해주면 됩니다.
    @Mapping(target = "status", qualifiedByName = "defaultStatus")
    @Mapping(target = "remainingCount", source = "bulkPassEntity.count")
    PassEntity toPassEntity(BulkPassEntity bulkPassEntity, String userId);

    // BulkPassStatus와 관계 없이 PassStatus값을 설정합니다.
    @Named("defaultStatus")
    default PassStatus status(BulkPassStatus status) {
        return PassStatus.READY;
    }

}
