package hello.wsdassignment2.domain.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "책 정보 수정 요청")
@Getter
@NoArgsConstructor
public class BookUpdateRequest {
    private String title;
    private String summary;

    @Positive(message = "가격은 양수여야 합니다.")
    private BigDecimal price;

    @PositiveOrZero(message = "재고 수량은 0 이상이어야 합니다.")
    private Integer stockQuantity; // 추가
}