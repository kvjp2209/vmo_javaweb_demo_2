package com.vmo.core.models.database.entities;

import lombok.Data;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.joda.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Data
public class BaseEntity {
    @CreatedDate
    @Generated(GenerationTime.INSERT)
    private LocalDateTime createdTime;
    @LastModifiedDate
    @Generated(GenerationTime.ALWAYS)
    private LocalDateTime lastUpdate;
    //TODO refactor to isDeleted
    private boolean isDelete;
}
