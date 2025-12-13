package hello.wsdassignment2.common.config;

import hello.wsdassignment2.common.response.ErrorResponse;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI openAPI() {
                Info info = new Info()
                                .title("WSD-Assignment2 API")
                                .version("v1.0.0")
                                .description("WSD-Assignment2 API 문서");

                // Security 스키마 설정
                SecurityScheme bearerAuth = new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization");

                SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");
                return new OpenAPI()
                                .components(new Components().addSecuritySchemes("bearerAuth", bearerAuth))
                                .addSecurityItem(securityRequirement)
                                .info(info);

        }
}
