package com.pass.passbatch.repository;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * @MappedSuperclass
 * 엔티티 클래스가 다른 엔티티 클래스의 매핑된 수퍼클래스임을 나타냅니다.
 * 이 어노테이션이 있는 클래스는 테이블에 매핑되지 않지만, 하위 클래스에 필드를 상속할 수 있습니다.
 * @EntityListeners
 * 지정된 엔티티 리스너 클래스를 엔티티에 추가합니다.
 * 이 경우 AuditingEntityListener 클래스가 추가되어 엔티티가 생성되거나 업데이트될 때 createdAt 및 modifiedAt 필드를 자동으로 설정합니다.
 * @CreatedDate
 * 필드가 엔티티 생성 시 현재 날짜와 시간으로 자동으로 설정되도록 합니다.
 * @LastModifiedDate
 * 필드가 엔티티가 업데이트될 때마다 현재 날짜와 시간으로 자동으로 설정되도록 합니다.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    /**
     * 생성일시
     */
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    /**
     * 수정일시
     */
    @LastModifiedDate
    private LocalDateTime modifiedAt;
}
