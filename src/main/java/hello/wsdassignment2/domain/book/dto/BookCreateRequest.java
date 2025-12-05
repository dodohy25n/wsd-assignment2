package hello.wsdassignment2.domain.book.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class BookCreateRequest {
    private String title;
    private String summary;
    private String isbn;
    private BigDecimal price;
    private Integer stockQuantity;
}
