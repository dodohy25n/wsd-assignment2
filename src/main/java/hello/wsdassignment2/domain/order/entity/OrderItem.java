package hello.wsdassignment2.domain.order.entity;

import hello.wsdassignment2.domain.book.entity.Book;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private BigDecimal priceAtOrder; // 주문 시점의 가격 저장 (가격 변동 대비)

    public static OrderItem create(Book book, int quantity) {
        return OrderItem.builder()
                .book(book)
                .quantity(quantity)
                .priceAtOrder(book.getPrice())
                .build();
    }

    protected void setOrder(Order order) {
        this.order = order;
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }
}