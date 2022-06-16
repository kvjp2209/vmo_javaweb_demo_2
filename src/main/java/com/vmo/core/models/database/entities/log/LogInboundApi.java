package com.vmo.core.models.database.entities.log;

import com.vmo.core.models.database.entities.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.joda.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.HttpMethod;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
//@Document(collection = "log_inbound_api")
public class LogInboundApi { //extends BaseEntity {
    @Id @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String service;
    private String path;
    private HttpMethod httpMethod;
    private String requestQuery;
    private String requestBody;
    private Integer responseHttpStatusCode;
    private String responseBody;
    private String exceptionName;
    private String stacktrace;
    private String page;
    @CreatedDate
    @Generated(GenerationTime.INSERT)
    private LocalDateTime createdTime;
}
