package com.pass.passbatch.job.notification;

import com.pass.passbatch.repository.booking.BookingEntity;
import com.pass.passbatch.repository.booking.BookingStatus;
import com.pass.passbatch.repository.notification.NotificationEntity;
import com.pass.passbatch.repository.notification.NotificationEvent;
import com.pass.passbatch.repository.notification.NotificationModelMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 예약 수업 10분 전에 사용자에게 알림 메시지를 발송하는 Job 을 정의
 */
@Configuration
public class SendNotificationBeforeClassJobConfig {
    private final int CHUNK_SIZE = 10;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final SendNotificationItemWriter sendNotificationItemWriter;

    public SendNotificationBeforeClassJobConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, EntityManagerFactory entityManagerFactory, SendNotificationItemWriter sendNotificationItemWriter) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.entityManagerFactory = entityManagerFactory;
        this.sendNotificationItemWriter = sendNotificationItemWriter;
    }

    /**
     * Job 이름을 "sendNotificationBeforeClassJob"으로 설정
     * 2개의 Step 으로 구성
     * addNotificationStep: 예약 정보를 읽어 알림 메시지 생성
     * sendNotificationStep: 생성된 알림 메시지 발송
     * @return
     */
    @Bean
    public Job sendNotificationBeforeClassJob() {
        return this.jobBuilderFactory.get("sendNotificationBeforeClassJob")
                .start(addNotificationStep())
                .next(sendNotificationStep())
                .build();
    }

    /**
     * Step 이름을 "addNotificationStep"으로 설정
     * Chunk size 를 10 으로 설정 (10건씩 처리)
     * reader, processor, writer 를 설정
     * 예약 상태가 "준비중"이며, 시작일시가 10분 후인 예약 정보를 조회하여 알림 메시지 생성
     * @return
     */
    @Bean
    public Step addNotificationStep() {
        return this.stepBuilderFactory.get("addNotificationStep")
                .<BookingEntity, NotificationEntity>chunk(CHUNK_SIZE)
                .reader(addNotificationItemReader())
                .processor(addNotificationItemProcessor())
                .writer(addNotificationItemWriter())
                .build();

    }

    /**
     * JpaPagingItemReader: JPA에서 사용하는 페이징 기법입니다.
     * 쿼리 당 pageSize만큼 가져오며 다른 PagingItemReader와 마찬가지로 Thread-safe 합니다.
     * 한번에 10건 (CHUNK_SIZE) 씩 조회
     */
    @Bean
    public JpaPagingItemReader<BookingEntity> addNotificationItemReader() {
        return new JpaPagingItemReaderBuilder<BookingEntity>()
                .name("addNotificationItemReader")
                .entityManagerFactory(entityManagerFactory)
                // pageSize: 한 번에 조회할 row 수
                .pageSize(CHUNK_SIZE)
                // 상태(status)가 준비중이며, 시작일시(startedAt)이 10분 후 시작하는 예약이 알람 대상이 됩니다.
                .queryString("select b from BookingEntity b join fetch b.userEntity where b.status = :status and b.startedAt <= :startedAt order by b.bookingSeq")
                .parameterValues(Map.of("status", BookingStatus.READY, "startedAt", LocalDateTime.now().plusMinutes(10)))
                .build();
    }

    /**
     * 조회된 예약 정보를 기반으로 알림 메시지 Entity 생성
     * @return
     */
    @Bean
    public ItemProcessor<BookingEntity, NotificationEntity> addNotificationItemProcessor() {
        return bookingEntity -> NotificationModelMapper.INSTANCE.toNotificationEntity(bookingEntity, NotificationEvent.BEFORE_CLASS);
    }

    /**
     * 생성된 알림 메시지 Entity 를 저장
     * @return
     */
    @Bean
    public JpaItemWriter<NotificationEntity> addNotificationItemWriter() {
        return new JpaItemWriterBuilder<NotificationEntity>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    /**
     * reader는 synchrosized로 순차적으로 실행되지만 writer는 multi-thread 로 동작합니다.
     * Step 이름을 "sendNotificationStep"으로 설정
     * Chunk size 를 10 으로 설정 (10건씩 처리)
     * reader, writer 를 설정
     * 발송되지 않은 수업 전 알림 메시지를 대상으로 발송
     */
    @Bean
    public Step sendNotificationStep() {
        return this.stepBuilderFactory.get("sendNotificationStep")
                .<NotificationEntity, NotificationEntity>chunk(CHUNK_SIZE)
                .reader(sendNotificationItemReader())
                .writer(sendNotificationItemWriter)
                .taskExecutor(new SimpleAsyncTaskExecutor()) // 가장 간단한 멀티쓰레드 TaskExecutor를 선언하였습니다.
                .build();
    }

    /**
     * SynchronizedItemStreamReader: multi-thread 환경에서 reader와 writer는 thread-safe 해야합니다.
     * Cursor 기법의 ItemReader는 thread-safe하지 않아 Paging 기법을 사용하거나 synchronized 를 선언하여 순차적으로 수행해야합니다.
     * 원본 reader 는 JpaCursorItemReader 이지만 멀티 스레드 환경에서 thread-safe 하도록 SynchronizedItemStreamReader 로 감싸
     */
    @Bean
    public SynchronizedItemStreamReader<NotificationEntity> sendNotificationItemReader() {
        JpaCursorItemReader<NotificationEntity> itemReader = new JpaCursorItemReaderBuilder<NotificationEntity>()
                .name("sendNotificationItemReader")
                .entityManagerFactory(entityManagerFactory)
                // 이벤트(event)가 수업 전이며, 발송 여부(sent)가 미발송인 알람이 조회 대상이 됩니다.
                .queryString("select n from NotificationEntity n where n.event = :event and n.sent = :sent")
                .parameterValues(Map.of("event", NotificationEvent.BEFORE_CLASS, "sent", false))
                .build();

        return new SynchronizedItemStreamReaderBuilder<NotificationEntity>()
                .delegate(itemReader)
                .build();

    }

}
