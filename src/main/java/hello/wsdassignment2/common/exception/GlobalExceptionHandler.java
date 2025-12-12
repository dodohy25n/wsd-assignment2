package hello.wsdassignment2.common.exception;

import hello.wsdassignment2.common.response.CommonResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // 커스텀 예외 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CommonResponse<Void>> handleCustomException(CustomException e, HttpServletRequest request) {
        ErrorCode errorCode = e.getErrorCode();

        log.warn("[CustomException] {} - {} ({})", errorCode.getCode(), e.getMessage(), request.getRequestURI());

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(CommonResponse.error(
                        errorCode.getCode(),
                        errorCode.getMessage(),
                        e.getDetail(), // 상세 정보가 있으면 출력, 없으면 null
                        request.getRequestURI()
                ));
    }

    // @Valid 유효성 검사 실패 처리
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        // 필드별 에러 메세지를 Map에 담기
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

        log.warn("[Validation Failed] {} - {}", path, errors);

        // errors 맵을 함께 전달
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(CommonResponse.error(
                        errorCode.getCode(),
                        errorCode.getMessage(),
                        errors, // 상세 에러 정보 주입
                        path
                ));
    }

    // [추가 1] 인증 실패 (401) - AuthenticationException
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CommonResponse<Void>> handleAuthenticationException(AuthenticationException e, HttpServletRequest request) {
        log.warn("[Authentication Error] {} ({})", e.getMessage(), request.getRequestURI());

        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(CommonResponse.error(
                        errorCode.getCode(),
                        errorCode.getMessage(), // "인증이 필요합니다."
                        request.getRequestURI()
                ));
    }

    // [추가 2] 권한 없음 (403) - AccessDeniedException
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CommonResponse<Void>> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.error("[Access Denied] {} ({})", e.getMessage(), request.getRequestURI());

        ErrorCode errorCode = ErrorCode.FORBIDDEN;
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(CommonResponse.error(
                        errorCode.getCode(),
                        errorCode.getMessage(), // "접근 권한이 없습니다."
                        request.getRequestURI()
                ));
    }

    // 그 외 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleAllException(Exception e, HttpServletRequest request) {
        log.error("[Internal Server Error] Unhandled Exception occurred at {}", request.getRequestURI(), e);

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(CommonResponse.error(
                        errorCode.getCode(),
                        errorCode.getMessage(),
                        request.getRequestURI()
                ));
    }
}