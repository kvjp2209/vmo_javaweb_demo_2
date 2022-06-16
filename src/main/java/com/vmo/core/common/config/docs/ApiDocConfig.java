package com.vmo.core.common.config.docs;

import com.vmo.core.common.CommonConstants;
import com.vmo.core.common.config.docs.springdoc.SpringDocConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Configuration
@ConfigurationProperties(prefix = CommonConstants.CONFIG_API_DOC)
@Data
@Import({
        SpringDocConfig.class
})
public class ApiDocConfig {
    private APIDocs type;
    private List<ApiSecurityMethod> securityMethods;
    private String baseApiUrl = "/";
    private String serverDescription;

    public void setSecurityMethods(List<ApiSecurityMethod> securityMethods) {
        if (securityMethods != null) {
            securityMethods = securityMethods.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        this.securityMethods = securityMethods;
    }
}
