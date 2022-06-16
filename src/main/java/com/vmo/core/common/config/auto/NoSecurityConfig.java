package com.vmo.core.common.config.auto;

import com.vmo.core.security.SecurityAuthenticator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;

@ConditionalOnClass(SecurityContextHolder.class)
@ConditionalOnMissingBean(SecurityAuthenticator.class)
//@Configuration
@AutoConfigureAfter(SecurityAuthenticator.class)
@Order
public class NoSecurityConfig extends WebSecurityConfigurerAdapter {

    protected void configure(HttpSecurity http) throws Exception {
        http
//                .cors().disable()
                .csrf().disable()
                .authorizeRequests().antMatchers("/**").permitAll();
    }
}
