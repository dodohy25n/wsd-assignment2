package hello.wsdassignment2.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

/*
* 스웨거용!! 실제 로직에서 사용하지 않음
* */
@Getter
@Schema(description = "에러 응답 객체")
public class ErrorResponse {

    @Schema(description = "성공 여부", example = "false") // 무조건 false로 고정
    private final boolean success = false;

    @Schema(description = "에러 코드", example = "RESOURCE_NOT_FOUND")
    private String code;

    @Schema(description = "에러 메시지", example = "존재하지 않는 리소스입니다.")
    private String message;

    @Schema(description = "에러 상세 정보")
    private Object errors;

    @Schema(description = "응답 시간", example = "2025-12-11T20:32:39")
    private LocalDateTime timestamp;

    @Schema(description = "요청 경로", example = "/api/books/999")
    private String path;
}