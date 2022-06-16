package com.vmo.core.common.config.annotation;

//import com.vmo.core.common.config.AsyncConfig;
import com.vmo.core.common.config.CommonConfig;
import com.vmo.core.common.config.SlackWebhookConfig;
import com.vmo.core.common.config.WebConfig;
import com.vmo.core.common.config.docs.ApiDocConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({
        CommonConfig.class,
        WebConfig.class,
//        AsyncConfig.class,
        ApiDocConfig.class,
        SlackWebhookConfig.class
})
public @interface EnableBaseConfig {
    String application() ;
}
