package hello.wsdassignment2.domain.order.dto;

import hello.wsdassignment2.domain.order.entity.OrderItem;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class OrderItemResponse {
    private final Long bookId;
    private final String bookTitle;
    private final int quantity;
    private final BigDecimal priceAtOrder;

    @Builder
    private OrderItemResponse(Long bookId, String bookTitle, int quantity, BigDecimal priceAtOrder) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.quantity = quantity;
        this.priceAtOrder = priceAtOrder;
    }

    public static OrderItemResponse from(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .bookId(orderItem.getBook().getId())
                .bookTitle(orderItem.getBook().getTitle())
                .quantity(orderItem.getQuantity())
                .priceAtOrder(orderItem.getPriceAtOrder())
                .build();
    }
}
