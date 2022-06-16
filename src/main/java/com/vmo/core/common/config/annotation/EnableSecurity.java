package com.vmo.core.common.config.annotation;

import com.vmo.core.security.SecurityAuthenticator;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(SecurityAuthenticator.class)
public @interface EnableSecurity {
}
