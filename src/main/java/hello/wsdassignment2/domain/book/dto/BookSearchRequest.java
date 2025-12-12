package hello.wsdassignment2.domain.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
@Data
@Schema(description = "책 검색 요청")
public class BookSearchRequest {

    @Schema(description = "검색어 (제목, 내용, ISBN 포함)", example = "JPA")
    private String keyword;

    @Schema(description = "최소 가격", example = "10000")
    private BigDecimal minPrice;

    @Schema(description = "최대 가격", example = "50000")
    private BigDecimal maxPrice;
}
