package com.vmo.core.controllers;

import com.vmo.core.common.config.docs.springdoc.ApiParameterPageable;
import com.vmo.core.models.database.entities.log.LogInboundApi;
import com.vmo.core.models.responses.PaginationResponse;
import com.vmo.core.services.log.LogInboundApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
//@ConditionalOnBean(LogInboundApiService.class)
public class InboundApiController {
//    @Autowired
//    private LogInboundApiService service;

    @GetMapping("/logs/inbound")
    @ApiParameterPageable
    public ResponseEntity<String> getLogs(Pageable pageable) {
        String test = "Hello World inbound !!!";
        System.out.println("Inside handle Method");
        return new ResponseEntity<>(test, HttpStatus.OK);
    }

}
