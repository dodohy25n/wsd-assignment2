package hello.wsdassignment2.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드는 응답 JSON에서 제외
public class ApiResponse<T> {

    // 공통 필드
    private final boolean success;
    private final String code;
    private final String message;

    // 데이터 필드 (실패 시 null)
    private final T data;

    // 페이지네이션 필드 (목록 조회 아닐 시 null)
    private final PageMetadata page;

    // 실패 필드 (성공 시 null)
    private final Object errors; // 에러 상세
    private final LocalDateTime timestamp;
    private final String path;

    private ApiResponse(boolean success, String code, String message, T data, PageMetadata page, Object errors, String path) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.page = page;
        this.errors = errors;
        this.timestamp = (!success) ? LocalDateTime.now() : null; // 에러일 때만 시간 찍기
        this.path = path;
    }

    // 단건 성공 응답
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "200", "Success", data, null, null, null);
    }

    // 페이지네이션 성공 응답
    public static <T> ApiResponse<List<T>> success(Page<T> pageData) {
        PageMetadata metadata = new PageMetadata(pageData);
        return new ApiResponse<>(true, "200", "Success", pageData.getContent(), metadata, null, null);
    }
    // 에러 응답
    public static ApiResponse<Void> error(String code, String message, String path) {
        return new ApiResponse<>(false, code, message, null, null, null, path);
    }

    // 상세 에러 응답 (유효성 검사)
    public static <E> ApiResponse<Void> error(String code, String message, E errors, String path) {
        return new ApiResponse<>(false, code, message, null, null, errors, path);
    }

    // 페이지네이션 메타데이터
    @Getter
    public static class PageMetadata {
        private final int pageNumber;
        private final int size;
        private final long totalElements;
        private final int totalPages;

        public PageMetadata(Page<?> page) {
            this.pageNumber = page.getNumber() + 1;
            this.size = page.getSize();
            this.totalElements = page.getTotalElements();
            this.totalPages = page.getTotalPages();
        }
    }
}