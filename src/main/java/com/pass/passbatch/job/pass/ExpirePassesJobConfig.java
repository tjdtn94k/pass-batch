package com.pass.passbatch.job.pass;

import com.pass.passbatch.repository.pass.PassEntity;
import com.pass.passbatch.repository.pass.PassStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Configuration: Spring 설정 클래스임을 명시
 * @EnableBatchProcessing: Spring Batch 기능 활성화
 */
@Configuration
public class ExpirePassesJobConfig {

    /**
     *  * JobBuilderFactory: Job 생성을 위한 빌더
     *  * StepBuilderFactory: Step 생성을 위한 빌더
     *  * EntityManagerFactory: JPA 엔티티 관리자 생성을 위한 팩토리
     *  * expirePassesJob(): Job 설정 메서드
     *  * Job 이름을 "expirePassesJob"으로 설정
     *  * expirePassesStep() 을 시작 Step으로 설정
     *  * expirePassesStep(): Step 설정 메서드
     *  * Step 이름을 "expirePassesStep"으로 설정
     *  * Chunk size를 1로 설정 (1건씩 처리)
     *  * reader, processor, writer 를 설정
     *  * expirePassesItemReader(): Reader 설정 메서드
     *  * JpaCursorItemReader 사용
     *  * 만료된 패스를 조회하는 쿼리 설정 (상태: 진행중, 종료일시: 현재 시점 이전)
     *  * expirePassesItemProcessor(): Processor 설정 메서드
     *  * 패스 상태를 "만료"로 변경
     *  * 만료 시간을 현재 시점으로 설정
     *  * expirePassesItemWriter(): Writer 설정 메서드
     *  * JpaItemWriter 사용
     *  * EntityManagerFactory 를 설정
     */
    private final int CHUNK_SIZE = 1;

    // @EnableBatchProcessing로 인해 Bean으로 제공된 JobBuilderFactory, StepBuilderFactory
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    public ExpirePassesJobConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, EntityManagerFactory entityManagerFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * 이 Job에 대한 흐름
     * 1. Job 설정
     * 2. Step 설정
     * 3. 만료된 pass(이용권) 칮기
     * 4. pass(이용권) 상태 변경
     * 5. pass(이용한) 변경한 상태값 저장
     * @return
     */
    @Bean
    public Job expirePassesJob() {
        return this.jobBuilderFactory.get("expirePassesJob")
                .start(expirePassesStep())
                .build();
    }

    @Bean
    public Step expirePassesStep() {
        return this.stepBuilderFactory.get("expirePassesStep")
                .<PassEntity, PassEntity>chunk(CHUNK_SIZE)
                .reader(expirePassesItemReader())
                .processor(expirePassesItemProcessor())
                .writer(expirePassesItemWriter())
                .build();

    }

    /**
     * JpaCursorItemReader: JpaPagingItemReader만 지원하다가 Spring 4.3에서 추가되었습니다.
     * 페이징 기법보다 보다 높은 성능으로, 데이터 변경에 무관한 무결성 조회가 가능합니다.
     */
    @Bean
    @StepScope
    public JpaCursorItemReader<PassEntity> expirePassesItemReader() {
        return new JpaCursorItemReaderBuilder<PassEntity>()
                .name("expirePassesItemReader")
                .entityManagerFactory(entityManagerFactory)
                // 상태(status)가 진행중이며, 종료일시(endedAt)이 현재 시점보다 과거일 경우 만료 대상이 됩니다.
                .queryString("select p from PassEntity p where p.status = :status and p.endedAt <= :endedAt")
                .parameterValues(Map.of("status", PassStatus.PROGRESSED, "endedAt", LocalDateTime.now()))
                .build();
    }

    @Bean
    public ItemProcessor<PassEntity, PassEntity> expirePassesItemProcessor() {
        return passEntity -> {
            passEntity.setStatus(PassStatus.EXPIRED);
            passEntity.setExpiredAt(LocalDateTime.now());
            return passEntity;
        };
    }

    /**
     * JpaItemWriter: JPA의 영속성 관리를 위해 EntityManager를 필수로 설정해줘야 합니다.
     */
    @Bean
    public JpaItemWriter<PassEntity> expirePassesItemWriter() {
        return new JpaItemWriterBuilder<PassEntity>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
