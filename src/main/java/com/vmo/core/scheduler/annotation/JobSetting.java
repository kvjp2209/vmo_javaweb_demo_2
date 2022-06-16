package com.vmo.core.scheduler.annotation;

import com.vmo.core.models.database.types.job.JobRepeatType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JobSetting {
    /**
     * Should this job be enabled by default? Can be overridden by configuration from database
     */
    boolean enableByDefault() default false;

    /**
     * Ignore setting from any source, always disable the job
     */
//    boolean forceDisable() default false;

    /**
     * Override setting from database, enable the job. Useful for local debugging
     */
//    boolean overrideEnable() default false;

    JobRepeatType defaultTimeType();

    long defaultWaitSeconds() default 3600L;

    String defaultCron() default "";

//    JobParameter[] defaultParams() default {};

//    boolean defaultLogFail() default true;
}
