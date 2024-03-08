package com.pass.passbatch.job.pass;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AddPassesTasklet 을 이용하여 패스를 생성하는 Job 을 정의
 * @Configuration: Spring 설정 클래스임을 명시
 * @EnableBatchProcessing: Spring Batch 기능 활성화 (필요하다면 클래스 선언부에 추가)
 * JobBuilderFactory: Job 생성을 위한 빌더
 * StepBuilderFactory: Step 생성을 위한 빌더
 */
@Configuration
public class AddPassesJobConfig {
    // @EnableBatchProcessing로 인해 Bean으로 제공된 JobBuilderFactory, StepBuilderFactory
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final AddPassesTasklet addPassesTasklet;

    public AddPassesJobConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, AddPassesTasklet addPassesTasklet) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.addPassesTasklet = addPassesTasklet;
    }

    /**
     * addPassesJob(): Job 설정 메서드
     * Job 이름을 "addPassesJob"으로 설정
     * addPassesStep() 을 시작 Step으로 설정
     * addPassesStep(): Step 설정 메서드
     * Step 이름을 "addPassesStep"으로 설정
     * addPassesTasklet 을 사용하는 단일 태스크 Step 설정
     * @return
     */
    @Bean
    public Job addPassesJob() {
        return this.jobBuilderFactory.get("addPassesJob")
                .start(addPassesStep())
                .build();
    }


    @Bean
    public Step addPassesStep() {
        return this.stepBuilderFactory.get("addPassesStep")
                .tasklet(addPassesTasklet)
                .build();
    }

}