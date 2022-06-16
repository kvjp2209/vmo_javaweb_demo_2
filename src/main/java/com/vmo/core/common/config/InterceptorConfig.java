package com.vmo.core.common.config;

import com.vmo.core.common.TestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class InterceptorConfig extends WebMvcConfigurerAdapter {

    @Autowired
    TestInterceptor testInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // this interceptor will be applied to all URLs
        registry.addInterceptor(testInterceptor);
    }
}
