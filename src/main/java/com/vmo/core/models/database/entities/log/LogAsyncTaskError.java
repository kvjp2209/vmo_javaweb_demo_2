package com.vmo.core.models.database.entities.log;

import lombok.Data;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.joda.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
//@Document(collection = "log_async_task_error")
public class LogAsyncTaskError {
    @Id @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String service;
    private String method;
    private String exceptionName;
    private String stacktrace;
    @CreatedDate
    @Generated(GenerationTime.INSERT)
    private LocalDateTime createdTime = LocalDateTime.now();
}
