package com.vmo.core.security;

import com.vmo.core.common.config.BaseEndpoint;
import com.vmo.core.common.error.ErrorCode;
import com.vmo.core.common.error.SystemException;
import com.vmo.core.security.jwt.JwtAuthenticationFilter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@ComponentScan("com.vmo.core.security")
public class SecurityAuthenticator extends WebSecurityConfigurerAdapter {
    private ApplicationContext applicationContext;
    @Autowired(required = false)
    @Lazy
    private List<BaseAuthenticationProcessingFilter> authFilters;

    public SecurityAuthenticator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public JwtAuthenticationFilter authenticationJwtTokenFilter() {
        return new JwtAuthenticationFilter();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("admin@gmail.com")
                .password("{noop}password")
                .roles("USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        super.configure(http);
        http
                .cors().and()
                .csrf().disable()
                .exceptionHandling()

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
//                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/api/**").authenticated()
//                .antMatchers( "/**").permitAll()
//                .antMatchers(BaseEndpoint.GENERAL_API_PATTERN, BaseEndpoint.PRIVATE_API_PATTERN).authenticated()
        ;

        if (CollectionUtils.isNotEmpty(authFilters)) {
            authFilters.forEach(f -> {
                try {
//                    f.setAuthenticationManager(super.authenticationManager());
                    http.addFilterBefore(f, UsernamePasswordAuthenticationFilter.class);
                } catch (Exception e) {
                    throw new SystemException(e, ErrorCode.UNCATEGORIZED_SERVER_ERROR, e.getMessage());
                }
            });
        }

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
