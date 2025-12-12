package hello.wsdassignment2.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "페이지네이션 응답 객체 (목록 조회)")
public class PagedResponse<T> extends CommonResponse<List<T>> {

    @Schema(description = "페이지네이션 정보")
    private final PageMetadata page;

    private PagedResponse(List<T> data, PageMetadata page) {
        // 부모 생성자 호출 (성공 상태)
        super(true, "200", "Success", data, null, null);
        this.page = page;
    }

    // 페이지 성공 응답 팩토리 메서드
    public static <T> PagedResponse<T> success(Page<T> pageData) {
        return new PagedResponse<>(pageData.getContent(), new PageMetadata(pageData));
    }

    @Getter
    @Schema(description = "페이지네이션 메타데이터")
    public static class PageMetadata {
        @Schema(description = "현재 페이지 번호", example = "1")
        private final int pageNumber;
        @Schema(description = "페이지 크기", example = "10")
        private final int size;
        @Schema(description = "전체 요소 개수", example = "100")
        private final long totalElements;
        @Schema(description = "전체 페이지 수", example = "10")
        private final int totalPages;

        public PageMetadata(Page<?> page) {
            this.pageNumber = page.getNumber() + 1;
            this.size = page.getSize();
            this.totalElements = page.getTotalElements();
            this.totalPages = page.getTotalPages();
        }
    }
}