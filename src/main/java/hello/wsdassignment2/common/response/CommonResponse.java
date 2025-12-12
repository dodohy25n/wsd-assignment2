package hello.wsdassignment2.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "기본 응답 객체 (단건/에러)")
public class CommonResponse<T> {

    @Schema(description = "성공 여부", example = "true")
    private final boolean success;

    @Schema(description = "응답 코드", example = "200")
    private final String code;

    @Schema(description = "응답 메시지", example = "Success")
    private final String message;

    @Schema(description = "응답 데이터 (성공 시 포함)")
    private final T data;

    @Schema(hidden = true)
    private final Object errors;

    @Schema(hidden = true)
    private final LocalDateTime timestamp;

    @Schema(hidden = true)
    private final String path;

    // 상속을 위해 protected로 변경
    protected CommonResponse(boolean success, String code, String message, T data, Object errors, String path) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.errors = errors;
        this.timestamp = (!success) ? LocalDateTime.now() : null;
        this.path = path;
    }

    // 단건 성공 응답
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(true, "200", "Success", data, null, null);
    }

    // 에러 응답
    public static CommonResponse<Void> error(String code, String message, String path) {
        return new CommonResponse<>(false, code, message, null, null, path);
    }

    // 상세 에러 응답
    public static <E> CommonResponse<Void> error(String code, String message, E errors, String path) {
        return new CommonResponse<>(false, code, message, null, errors, path);
    }
}