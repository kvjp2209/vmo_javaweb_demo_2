//package com.vmo.core.services.log;
//
//import com.vmo.core.managers.log.LogInboundApiManager;
//import com.vmo.core.models.database.entities.log.LogInboundApi;
//import com.vmo.core.models.responses.PaginationResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//
//@Service
//@ConditionalOnBean(LogInboundApiManager.class)
//public class LogInboundApiServiceImpl implements LogInboundApiService {
//    @Autowired
//    private LogInboundApiManager logInboundApiManager;
//
//    public PaginationResponse<LogInboundApi> getLogs(Pageable pageable) {
//        PaginationResponse<LogInboundApi> response = new PaginationResponse<>(
//                pageable.getPageNumber(), pageable.getPageSize()
//        );
//        response.setTotalItem(logInboundApiManager.countAll());
//        if (response.getTotalItem() > 0) {
//            response.setData(logInboundApiManager.getLogs(pageable));
//        }
//
//        return response;
//    }
//}
