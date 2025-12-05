package hello.wsdassignment2.domain.book.entity;

import hello.wsdassignment2.common.entity.BaseEntity;
import hello.wsdassignment2.common.exception.CustomException;
import hello.wsdassignment2.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private BigDecimal price; // 가격 계산 정확도를 위해 BigDecimal 사용

    @Column(nullable = false)
    private Integer stockQuantity;

    private LocalDateTime deletedAt;

    @Builder
    public Book(String title, String summary, String isbn, BigDecimal price, Integer stockQuantity) {
        this.title = title;
        this.summary = summary;
        this.isbn = isbn;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    // 정보 수정 (재고 포함)
    public void updateInfo(String title, String summary, BigDecimal price, Integer stockQuantity) {
        this.title = title;
        this.summary = summary;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    // 재고 증가
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    // 재고 감소 (주문 시 사용)
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            // ErrorCode.OUT_OF_STOCK 이 있다면 사용, 없다면 재고 부족 메시지 처리
            throw new CustomException(ErrorCode.OUT_OF_STOCK);
        }
        this.stockQuantity = restStock;
    }

    // Soft Delete 처리
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
