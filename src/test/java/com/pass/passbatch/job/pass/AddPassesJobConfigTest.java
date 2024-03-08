package com.pass.passbatch.job.pass;

import com.pass.passbatch.config.TestBatchConfig;
import com.pass.passbatch.repository.pass.BulkPassEntity;
import com.pass.passbatch.repository.pass.BulkPassRepository;
import com.pass.passbatch.repository.pass.BulkPassStatus;
import com.pass.passbatch.repository.user.UserGroupMappingEntity;
import com.pass.passbatch.repository.user.UserGroupMappingRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * AddPassesJobConfig의 테스트 클래스입니다.
 * @Slf4j: Lombok 어노테이션으로 로거 객체 생성
 * @SpringBatchTest: Spring Batch 테스트를 위한 어노테이션
 * @SpringBootTest: Spring Boot 통합 테스트를 위한 어노테이션
 * @ActiveProfiles("test"): 테스트 프로파일 활성화
 * @ContextConfiguration: 테스트에 사용할 설정 클래스 지정
 * @Autowired: 의존성 주입 (Spring Bean 자동으로 가져옴
 */
@Slf4j
@SpringBatchTest
@SpringBootTest
@ActiveProfiles("tc")
@ContextConfiguration(classes = {AddPassesJobConfig.class, TestBatchConfig.class, AddPassesTasklet.class})
public class AddPassesJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private BulkPassRepository bulkPassRepository;

    @Autowired
    private UserGroupMappingRepository userGroupMappingRepository;

    /**
     * addPassesJob 메서드를 테스트합니다.
     * 1. 대량 패스 엔터티를 추가합니다.
     * 2. 작업을 실행하고 JobExecution 객체를 얻습니다.
     * 3. 작업의 종료 상태와 이름을 확인합니다.
     *
     * @throws Exception 예외가 발생할 경우
     */
    @Test
    public void test_addPassesJob() throws Exception {
        // given
        addBulkPassEntity();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        JobInstance jobInstance = jobExecution.getJobInstance();

        // then
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
        assertEquals("addPassesJob", jobInstance.getJobName());
    }

    /**
     * 대량 패스 엔터티를 추가하는 메서드입니다.
     */
    private void addBulkPassEntity() {
        final LocalDateTime now = LocalDateTime.now();
        final String userGroupId = RandomStringUtils.randomAlphabetic(6);
        final String userId = "A100" + RandomStringUtils.randomNumeric(4);

        BulkPassEntity bulkPassEntity = new BulkPassEntity();
        bulkPassEntity.setPackageSeq(1);
        bulkPassEntity.setUserGroupId(userGroupId);
        bulkPassEntity.setStatus(BulkPassStatus.READY);
        bulkPassEntity.setCount(10);
        bulkPassEntity.setStartedAt(now);
        bulkPassEntity.setEndedAt(now.plusDays(60));

        bulkPassRepository.save(bulkPassEntity);

        UserGroupMappingEntity userGroupMappingEntity = new UserGroupMappingEntity();
        userGroupMappingEntity.setUserGroupId(userGroupId);
        userGroupMappingEntity.setUserId(userId);
        userGroupMappingEntity.setUserGroupName("그룹");
        userGroupMappingEntity.setDescription("그룹 설명");

        userGroupMappingRepository.save(userGroupMappingEntity);
    }
}

