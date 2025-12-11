package hello.wsdassignment2.domain.order.entity;


import hello.wsdassignment2.common.entity.BaseEntity;
import hello.wsdassignment2.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Builder
    public Order(User user, OrderStatus status) {
        this.user = user;
        this.status = status;
        this.totalPrice = BigDecimal.ZERO;
    }

    // 주문에 상품 추가 (연관관계 편의 메서드)
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
        calculateTotalPrice();
    }

    // 총 주문 금액 계산
    private void calculateTotalPrice() {
        this.totalPrice = orderItems.stream()
                .map(item -> item.getPriceAtOrder().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // 주문 취소
    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }
    
    // 주문 상태 변경
    public void setStatus(OrderStatus status) { this.status = status; }
}