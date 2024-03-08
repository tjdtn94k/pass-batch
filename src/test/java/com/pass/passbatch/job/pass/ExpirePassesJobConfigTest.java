package com.pass.passbatch.job.pass;

import com.pass.passbatch.config.TestBatchConfig;
import com.pass.passbatch.repository.pass.PassEntity;
import com.pass.passbatch.repository.pass.PassRepository;
import com.pass.passbatch.repository.pass.PassStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @Slf4j: Lombok 어노테이션으로 로거 객체 생성
 * @SpringBatchTest: Spring Batch 테스트를 위한 어노테이션
 * @SpringBootTest: Spring Boot 통합 테스트를 위한 어노테이션
 * @ActiveProfiles("test"): 테스트 프로파일 활성화
 * @ContextConfiguration: 테스트에 사용할 설정 클래스 지정
 * @Autowired: 의존성 주입 (Spring Bean 자동으로 가져옴)
 */
@Slf4j
@SpringBatchTest
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {ExpirePassesJobConfig.class, TestBatchConfig.class})
public class ExpirePassesJobConfigTest {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private PassRepository passRepository;

    /**
     * given: 테스트 데이터 준비
     * addPassEntities(10) 메서드를 호출하여 만료된 패스 10건 생성
     * when: 테스트 로직 실행
     * jobLauncherTestUtils.launchJob() 메서드를 호출하여 배치 실행
     * 실행 결과 (JobExecution) 를 가져옴
     * then: 테스트 결과 검증
     * 종료 상태가 ExitStatus.COMPLETED 인지 확인 (성공적으로 완료되었는지)
     * 실행된 Job 의 이름이 "expirePassesJob" 인지 확인
     * @throws Exception
     */
    @Test
    public void test_expirePassesStep() throws Exception {
        // given
        addPassEntities(10);

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        JobInstance jobInstance = jobExecution.getJobInstance();

        // then
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
        assertEquals("expirePassesJob", jobInstance.getJobName());

    }

    private void addPassEntities(int size) {
        final LocalDateTime now = LocalDateTime.now();
        final Random random = new Random();

        List<PassEntity> passEntities = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            PassEntity passEntity = new PassEntity();
            passEntity.setPackageSeq(1);
            passEntity.setUserId("A" + 1000000 + i);
            passEntity.setStatus(PassStatus.PROGRESSED);
            passEntity.setRemainingCount(random.nextInt(11));
            passEntity.setStartedAt(now.minusDays(60));
            passEntity.setEndedAt(now.minusDays(1));
            passEntities.add(passEntity);

        }
        passRepository.saveAll(passEntities);

    }

}
