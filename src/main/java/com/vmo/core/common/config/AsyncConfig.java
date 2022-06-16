//package com.vmo.core.common.config;
//
//import com.vmo.core.common.CommonConstants;
//import com.vmo.core.common.logging.async.AsyncTaskLogger;
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.config.BeanDefinition;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.context.annotation.Role;
//import org.springframework.scheduling.annotation.AsyncConfigurer;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
//import java.util.concurrent.Executor;
//
//@Configuration
//@ConfigurationProperties(prefix = CommonConstants.CONFIG_ASYNC)
//@EnableAsync//(proxyTargetClass = false)
//@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
//public class AsyncConfig implements AsyncConfigurer {
//    @Getter @Setter
//    private Integer poolSize;
//    @Getter @Setter
//    private int maxQueue = Short.MAX_VALUE;
//
//    private ThreadPoolTaskExecutor taskExecutor;
//
//    @Autowired
//    @Lazy
//    private AsyncTaskLogger asyncTaskLogger;
//
//    @Override
//    public Executor getAsyncExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(poolSize != null ? poolSize : Runtime.getRuntime().availableProcessors() * 5); //default = x5 CPU threads (not CPU cores)
//        executor.setMaxPoolSize(executor.getPoolSize() * 2);
//        executor.setQueueCapacity(maxQueue);
//        executor.setThreadNamePrefix("Async@ThreadPool-");
//        executor.initialize();
//
//        taskExecutor = executor;
//        return executor;
//    }
//
//    @Override
//    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
//        return asyncTaskLogger;
//    }
//
//
//}
