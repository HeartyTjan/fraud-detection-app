package com.interswitch.fraudtransactionapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class AuditBaseEntity {
    @CreatedDate
    @Column(nullable = false, updatable = false, columnDefinition = "datetime2")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false, columnDefinition = "datetime2")
    private LocalDateTime updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;

    @Column(nullable = false)
    private boolean deleted = false;

}
