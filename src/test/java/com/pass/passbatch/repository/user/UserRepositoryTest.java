package com.pass.passbatch.repository.user;

import com.pass.passbatch.config.TestBatchConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @DataJpaTest: Spring Data JPA 테스트를 위한 어노테이션
 * @ActiveProfiles("tc"): "tc"라는 프로파일 활성화 (테스트 환경 설정 등)
 * @ContextConfiguration(classes = {TestBatchConfig.class}):
 */
@DataJpaTest
@ActiveProfiles("tc")
@ContextConfiguration(classes = {TestBatchConfig.class}) // BaseEntity의 createdAt, modifiedAt을 위함
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void test_save() {
        // given
        UserEntity userEntity = new UserEntity();
        final String userId = "C100" + RandomStringUtils.randomNumeric(4);
        userEntity.setUserId(userId); // 유저 아이디 생성
        userEntity.setUserName("김철수");
        userEntity.setStatus(UserStatus.ACTIVE);
        userEntity.setPhone("01033334444");
        userEntity.setMeta(Map.of("uuid", "abcd1234")); // meta 필드에 uuid 정보 추가

        // when (테스트 로직 실행)
        userRepository.save(userEntity); // 사용자 정보 저장

        // then (결과 검증)
        Optional<UserEntity> optionalUserEntity = userRepository.findById(userId);
        assertTrue(optionalUserEntity.isPresent()); // 저장된 사용자 정보가 있는지 확인
    }
}
