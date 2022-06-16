//package com.vmo.core.common.logging.async;
//
//import com.vmo.core.common.config.CommonConfig;
//import com.vmo.core.common.logging.BaseLogger;
//import com.vmo.core.models.database.entities.log.LogAsyncExclude;
//import com.vmo.core.models.database.entities.log.LogAsyncTaskError;
////import com.vmo.core.repositories.log.LogAsyncExcludeRepository;
//import com.zaxxer.hikari.HikariDataSource;
//import org.apache.commons.collections4.CollectionUtils;
//import org.apache.commons.lang3.exception.ExceptionUtils;
//import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.lang.reflect.Method;
//import java.util.List;
//
//@Component
//public class AsyncTaskLogger extends BaseLogger<LogAsyncTaskError> implements AsyncUncaughtExceptionHandler {
//    @Autowired
//    private CommonConfig commonConfig;
//    @Autowired(required = false)
//    private LogAsyncExcludeRepository logAsyncExcludeRepository;
//
//    private volatile List<LogAsyncExclude> excludedLogs; //cached configs
//
//    public AsyncTaskLogger(ApplicationContext applicationContext, @Autowired(required = false) HikariDataSource hikariDataSource) {
//        super(applicationContext, hikariDataSource);
//    }
//
//    @EventListener(ApplicationReadyEvent.class)
//    public void init() {
//        if (commonConfig.isLogAsyncError()) {
//            reloadConfig();
//        }
//    }
//
//    @Override
//    public void reloadConfig() {
//        try {
//            excludedLogs = logAsyncExcludeRepository.findAllExcludedLogs();
//            isLoaded = true;
//        } catch (Exception e) {
//            try {
//                handleUncaughtException(e, null);
//            } catch (Exception ignored) {
//                e.getStackTrace();
//            }
//        }
//    }
//
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public void handleExceptionInNewTransaction(
//            Throwable throwable, Method method, Object... obj
//    ) {
//        handleUncaughtException(throwable, method, obj);
//    }
//
//    @Override
//    public void handleUncaughtException(
//            Throwable throwable, Method method, Object... obj
//    ) {
////        System.out.println("Exception message - " + throwable.getMessage());
////        System.out.println("Method name - " + method.getName());
////        for (Object param : obj) {
////            System.out.println("Parameter value - " + param);
////        }
//
//        if (!commonConfig.isLogAsyncError()) return;
//
//        if (!isLoaded) {
//            reloadConfig();
//        }
//        if (!isLoaded) return;
//
//        LogAsyncTaskError log = new LogAsyncTaskError();
//        log.setService(commonConfig.getService());
//        log.setExceptionName(throwable.getClass().getSimpleName());
//        if (method != null) {
//            log.setMethod(method.getDeclaringClass().getName() + "." + method.getName());
//        }
//
//        boolean isExcluded = false;
//        if (CollectionUtils.isNotEmpty(excludedLogs)) {
//            for (LogAsyncExclude exclude : excludedLogs) {
//                if (exclude.getMethod() != null) {
//                    if (exclude.getMethod().equals(log.getMethod())) {
//                        isExcluded = true;
//                    }
//                }
//
//                if (exclude.getExceptionName() != null) {
//                    if (exclude.getExceptionName().equals(log.getExceptionName())) {
//                        isExcluded = true && isExcluded;
//                    } else {
//                        isExcluded = false;
//                    }
//                }
//
//                if (isExcluded) {
//                    break;
//                }
//            }
//        }
//
//        if (!isExcluded) {
//            log.setStacktrace(ExceptionUtils.getStackTrace(throwable));
//
//            addLog(log);
//        }
//    }
//}
