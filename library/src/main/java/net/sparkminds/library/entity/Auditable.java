package net.sparkminds.library.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {

	@Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private LocalDateTime createdAt;
    
    @CreatedBy
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @LastModifiedBy
    private String updatedBy;
}
