package hello.wsdassignment2.domain.order.dto;

import hello.wsdassignment2.domain.order.entity.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Schema(description = "주문 상품 정보 응답")
@Getter
public class OrderItemResponse {

    @Schema(description = "주문 상품 ID")
    private final Long orderItemId;

    @Schema(description = "책 ID")
    private final Long bookId;

    @Schema(description = "책 제목")
    private final String bookTitle;

    @Schema(description = "주문 시점 가격")
    private final BigDecimal priceAtOrder;

    @Schema(description = "수량")
    private final int quantity;

    @Builder
    private OrderItemResponse(Long orderItemId, Long bookId, String bookTitle, int quantity, BigDecimal priceAtOrder) {
        this.orderItemId = orderItemId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.quantity = quantity;
        this.priceAtOrder = priceAtOrder;
    }

    public static OrderItemResponse from(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .orderItemId(orderItem.getId())
                .bookId(orderItem.getBook().getId())
                .bookTitle(orderItem.getBook().getTitle())
                .quantity(orderItem.getQuantity())
                .priceAtOrder(orderItem.getPriceAtOrder())
                .build();
    }
}
