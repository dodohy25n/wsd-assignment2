package hello.wsdassignment2.domain.book;

import hello.wsdassignment2.common.entity.BaseEntity;
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
@Table(name = "book")
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

    private LocalDateTime deletedAt;

    @Builder
    public Book(String title, String summary, String isbn, BigDecimal price) {
        this.title = title;
        this.summary = summary;
        this.isbn = isbn;
        this.price = price;
    }

    public void updateInfo(String title, String summary, BigDecimal price) {
        this.title = title;
        this.summary = summary;
        this.price = price;
    }
}
