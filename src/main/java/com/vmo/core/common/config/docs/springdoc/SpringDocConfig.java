package com.vmo.core.common.config.docs.springdoc;

import com.vmo.core.common.CommonConstants;
import com.vmo.core.common.config.docs.ApiDocConfig;
import com.vmo.core.common.config.docs.ApiSecurityMethod;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.SpringDocUtils;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;

@Configuration
@ConditionalOnProperty(
        prefix = CommonConstants.CONFIG_API_DOC,
        name = "type",
        havingValue = "SPRING_DOC_OPENAPI"
)
@ComponentScan("com.vmo.core.common.config.docs.springdoc")
public class SpringDocConfig {
    @Autowired
    private ApiDocConfig apiDocConfig;

    private final String SECURITY_SCHEMA_TOKEN = "bearer";
    private final String SECURITY_SCHEMA_TOKEN_NAME = "Token";
    private final String SECURITY_SCHEMA_BASIC = "basic";
    private final String SECURITY_SCHEMA_BASIC_NAME = "Basic Auth";

    @Bean
    public OpenAPI openAPI(ApiDocConfig apiDocConfig) {
        SpringDocUtils.getConfig().addRequestWrapperToIgnore(Pageable.class);

        OpenAPI openAPI = new OpenAPI().addServersItem(
                new Server()
//                        .url("/")
                        .url(apiDocConfig.getBaseApiUrl())
                        .description(StringUtils.defaultIfBlank(apiDocConfig.getServerDescription(), ""))
        );

        if (CollectionUtils.isNotEmpty(apiDocConfig.getSecurityMethods())) {
            Components components = new Components();

            for (ApiSecurityMethod securityMethod : apiDocConfig.getSecurityMethods()) {
                switch (securityMethod) {
                    case BASIC:
                        openAPI.addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEMA_BASIC_NAME));
                        components.addSecuritySchemes(SECURITY_SCHEMA_BASIC_NAME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .name(SECURITY_SCHEMA_BASIC_NAME)
                                .scheme(SECURITY_SCHEMA_BASIC)
                        );
                        break;
                    case BEARER:
                        openAPI.addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEMA_TOKEN_NAME));
                        components.addSecuritySchemes(SECURITY_SCHEMA_TOKEN_NAME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .name(SECURITY_SCHEMA_TOKEN_NAME)
                                .scheme(SECURITY_SCHEMA_TOKEN)
                        );
                        break;
                }
            }

            openAPI.components(components);
        }

        return openAPI;
    }

//    @Bean
    public OpenApiCustomiser customerGlobalHeaderOpenApiCustomiser() {
//        return openApi -> openApi.path("/foo",
//                new PathItem().get(new Operation().operationId("foo").responses(new ApiResponses()
//                        .addApiResponse("default",new ApiResponse().description("")
//                                .content(new Content().addMediaType("fatz", new MediaType()))))));

        return openApi -> openApi.getComponents();
    }
}
