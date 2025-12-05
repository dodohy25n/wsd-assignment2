package hello.wsdassignment2.domain.book.dto;

import hello.wsdassignment2.domain.book.entity.Book;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class BookResponse {
    private final Long id;
    private final String title;
    private final String summary;
    private final String isbn;
    private final BigDecimal price;
    private final Integer stockQuantity;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Builder
    private BookResponse(Long id, String title, String summary, String isbn,
                         BigDecimal price, Integer stockQuantity,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.isbn = isbn;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Entity -> DTO 변환 메서드
    public static BookResponse from(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .summary(book.getSummary())
                .isbn(book.getIsbn())
                .price(book.getPrice())
                .stockQuantity(book.getStockQuantity())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }
}