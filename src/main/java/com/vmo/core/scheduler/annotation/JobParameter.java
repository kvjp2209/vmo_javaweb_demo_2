package com.vmo.core.scheduler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
//@Repeatable(value = JobSetting.class)
public @interface JobParameter {
    String name();
    String stringValue();
    long longValue();
}
