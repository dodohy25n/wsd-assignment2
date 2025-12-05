package hello.wsdassignment2.domain.book.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class BookUpdateRequest {
    private String title;
    private String summary;
    private BigDecimal price;
    private Integer stockQuantity; // 추가
}