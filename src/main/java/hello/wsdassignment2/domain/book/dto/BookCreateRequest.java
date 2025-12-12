package hello.wsdassignment2.domain.book.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class BookCreateRequest {

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "줄거리를 입력해주세요.")
    private String summary;

    @NotBlank(message = "ISBN을 입력해주세요.")
    private String isbn;

    @NotNull(message = "가격을 입력해주세요.")
    @Positive(message = "가격은 양수여야 합니다.")
    private BigDecimal price;

    @NotNull(message = "재고 수량을 입력해주세요.")
    @PositiveOrZero(message = "재고 수량은 0 이상이어야 합니다.")
    private Integer stockQuantity;
}
