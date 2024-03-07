package com.pass.passbatch.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Configuration: Spring 설정 클래스임을 명시
 * @EnableJpaAuditing: JPA Auditing 기능 활성화
 * @EnableAutoConfiguration: Spring Boot 자동 설정 기능 활성화
 * @EnableBatchProcessing: Spring Batch 기능 활성화
 * @EntityScan("com.pass.passbatch.repository"): 엔터티 클래스가 있는 패키지 지정
 * @EnableJpaRepositories("com.pass.passbatch.repository"): JPA 리포지토리 클래스가 있는 패키지 지정
 * @EnableTransactionManagement: 트랜잭션 관리 기능 활성화
 */
@Configuration
@EnableJpaAuditing
@EnableAutoConfiguration
@EnableBatchProcessing
@EntityScan("com.pass.passbatch.repository")
@EnableJpaRepositories("com.pass.passbatch.repository")
@EnableTransactionManagement
public class TestBatchConfig {
}
