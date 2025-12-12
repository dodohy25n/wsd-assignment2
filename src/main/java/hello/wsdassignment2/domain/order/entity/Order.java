package hello.wsdassignment2.domain.order.entity;


import hello.wsdassignment2.common.entity.BaseEntity;
import hello.wsdassignment2.common.exception.CustomException;
import hello.wsdassignment2.common.exception.ErrorCode;
import hello.wsdassignment2.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
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
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    public static Order create(User user) {
        return Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .totalPrice(BigDecimal.ZERO)
                .build();
    }

    // 주문에 상품 추가 (연관관계 편의 메서드)
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
        calculateTotalPrice();
    }

    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        calculateTotalPrice();
    }

    public OrderItem findOrderItemById(Long orderItemId) {
        return this.orderItems.stream()
                .filter(item -> item.getId().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "해당 주문에 존재하지 않는 상품입니다."));
    }

    public boolean isOwnedBy(Long userId) {
        return this.user.getId().equals(userId);
    }

    // 총 주문 금액 계산
    public void calculateTotalPrice() {
        this.totalPrice = orderItems.stream()
                .map(item -> item.getPriceAtOrder().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // 주문 취소
    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }
    
    public void updateStatus(OrderStatus newStatus) {
        if (this.status == OrderStatus.CANCELLED) {
            throw new CustomException(ErrorCode.STATE_CONFLICT, "이미 취소된 주문의 상태는 변경할 수 없습니다.");
        }
        if (this.status == OrderStatus.DELIVERED && newStatus != OrderStatus.DELIVERED) {
            throw new CustomException(ErrorCode.STATE_CONFLICT, "배송 완료된 주문의 상태는 변경할 수 없습니다.");
        }
        this.status = newStatus;
    }
}