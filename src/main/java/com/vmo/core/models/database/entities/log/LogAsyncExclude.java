package com.vmo.core.models.database.entities.log;

import lombok.Data;
//import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
//@Document(collection = "log_async_exclude")
public class LogAsyncExclude {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String service;
    private String method;
    private String exceptionName;
    private boolean isExcluded;
}
