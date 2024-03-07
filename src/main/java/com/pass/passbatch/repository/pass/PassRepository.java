package com.pass.passbatch.repository.pass;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * passSeq에 해당하는 PassEntity의 remainingCount 필드를 remainingCount 값으로 업데이트하는 메서드
 * passSeq: 업데이트할 PassEntity의 passSeq 값
 * remainingCount: PassEntity의 remainingCount 필드에 설정될 값
 * UPDATE 쿼리를 사용하여 데이터베이스를 직접 업데이트
 */
public interface PassRepository extends JpaRepository<PassEntity, Integer> {
    @Transactional
    @Modifying
    @Query(value = "UPDATE PassEntity p" +
            "          SET p.remainingCount = :remainingCount," +
            "              p.modifiedAt = CURRENT_TIMESTAMP" +
            "        WHERE p.passSeq = :passSeq")
    int updateRemainingCount(Integer passSeq, Integer remainingCount);
}
