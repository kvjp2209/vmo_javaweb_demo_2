//package com.vmo.core.managers.log;
//
//import com.vmo.core.models.database.entities.log.LogInboundApi;
//import com.vmo.core.repositories.log.LogInboundApiRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//@ConditionalOnProperty(
//        prefix = "core.common",
//        name = "log-api-error",
//        havingValue = "true"
//)
//public class LogInboundApiManager {
//    @Autowired
//    private LogInboundApiRepository logInboundApiRepository;
//
//    public int countAll() {
//        return logInboundApiRepository.countAll();
//    }
//
//    public List<LogInboundApi> getLogs(Pageable pageable) {
//        return logInboundApiRepository.getLogs(pageable);
//    }
//}
