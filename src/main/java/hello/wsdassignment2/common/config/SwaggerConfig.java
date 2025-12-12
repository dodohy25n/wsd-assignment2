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

        // SecurityRequirement 설정
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        // 공통 응답 컨텐츠 정의 (ErrorResponse 스키마 참조)
        Content errorContent = new Content().addMediaType("application/json",
                new MediaType().schema(new io.swagger.v3.oas.models.media.Schema<>().$ref("#/components/schemas/ErrorResponse")));

        // Components 설정: 500 에러와 ErrorResponse 스키마만 추가
        Components components = new Components()
                .addSecuritySchemes("bearerAuth", bearerAuth)
                // ErrorResponse 스키마 추가 (CommonResponse의 구조를 따름)
                .addSchemas("ErrorResponse", new io.swagger.v3.oas.models.media.Schema<ErrorResponse>()
                        .type("object")
                        .addProperty("code", new io.swagger.v3.oas.models.media.Schema<String>().type("string"))
                        .addProperty("message", new io.swagger.v3.oas.models.media.Schema<String>().type("string"))
                        .addProperty("detail", new io.swagger.v3.oas.models.media.Schema<Object>().type("object").nullable(true))
                        .addProperty("path", new io.swagger.v3.oas.models.media.Schema<String>().type("string"))
                        .addProperty("timestamp", new io.swagger.v3.oas.models.media.Schema<String>().type("string").format("date-time")))
                // 500 에러 응답만 Components에 추가
                .addResponses("500", new ApiResponse().description("Internal Server Error - 서버 내부 오류").content(
                        new Content().addMediaType("application/json",
                                new MediaType().schema(new io.swagger.v3.oas.models.media.Schema<>().$ref("#/components/schemas/ErrorResponse"))
                                        .example("{\"code\": \"INTERNAL_SERVER_ERROR\", \"message\": \"서버 내부 오류가 발생했습니다.\", \"path\": \"/api/some-path\", \"timestamp\": \"2025-12-12T10:00:00.000Z\"}"))));


        return new OpenAPI()
                .components(components)
                .addSecurityItem(securityRequirement)
                .info(info);
    }

    // OperationCustomizer를 사용하여 모든 API에 500 공통 응답을 추가
    @Bean
    public OperationCustomizer globalResponseCustomizer() {
        return (operation, handlerMethod) -> {
            ApiResponses responses = operation.getResponses();
            if (responses == null) {
                responses = new ApiResponses();
                operation.setResponses(responses);
            }

            // 500 응답만 추가 ($ref 사용)
            addApiResponseIfNotExists(responses, "500");

            return operation;
        };
    }

    // 이미 해당 응답 코드가 존재하지 않을 경우에만 추가하는 헬퍼 메소드
    private void addApiResponseIfNotExists(ApiResponses responses, String code) {
        if (!responses.containsKey(code)) {
            responses.addApiResponse(code, new ApiResponse().$ref("#/components/responses/" + code));
        }
    }
}
