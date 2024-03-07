package com.pass.passbatch.repository.pass;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PassModelMapperTest {

    @Test
    public void test_toPassEntity() {
        // given (데이터 준비)
        // 현재 날짜시각, 사용자 아이디 설정
        final LocalDateTime now = LocalDateTime.now();
        final String userId = "A1000000";

        // BulkPassEntity 객체 생성 및 데이터 설정
        BulkPassEntity bulkPassEntity = new BulkPassEntity();
        bulkPassEntity.setPackageSeq(1);
        bulkPassEntity.setUserGroupId("GROUP");
        bulkPassEntity.setStatus(BulkPassStatus.COMPLETED);
        bulkPassEntity.setCount(10);
        bulkPassEntity.setStartedAt(now.minusDays(60));
        bulkPassEntity.setEndedAt(now);

        // when (테스트 로직 실행)
        // PassModelMapper 를 통해 BulkPassEntity 를 PassEntity 객체로 변환
        final PassEntity passEntity = PassModelMapper.INSTANCE.toPassEntity(bulkPassEntity, userId);

        // then (결과 검증)
        // 변환된 PassEntity 객체 필드 값 검증
        assertEquals(1, passEntity.getPackageSeq());
        assertEquals(PassStatus.READY, passEntity.getStatus());
        assertEquals(10, passEntity.getRemainingCount());
        assertEquals(now.minusDays(60), passEntity.getStartedAt());
        assertEquals(now, passEntity.getEndedAt());
    }
}
