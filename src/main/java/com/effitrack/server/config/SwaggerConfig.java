package com.effitrack.server.config;

import com.effitrack.server.constant.StringConst;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(StringConst.SWAGGER_TITLE)
                        .version(StringConst.SWAGGER_VERSION)
                        .description(StringConst.SWAGGER_DESCRIPTION))
                .addSecurityItem(new SecurityRequirement().addList(StringConst.SWAGGER_AUTH_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(StringConst.SWAGGER_AUTH_SCHEME_NAME, createSecurityScheme()));
    }

    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .name(StringConst.SWAGGER_AUTH_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme(StringConst.SWAGGER_AUTH_SCHEME)
                .bearerFormat(StringConst.SWAGGER_AUTH_FORMAT);
    }
}
