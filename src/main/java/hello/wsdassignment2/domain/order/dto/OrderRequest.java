package hello.wsdassignment2.domain.order.dto;

import lombok.Getter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderRequest {

    @NotEmpty(message = "주문할 상품은 최소 1개 이상이어야 합니다.")
    @Valid // 리스트 내부의 객체들도 검증
    private List<OrderItemDTO> items;

    @Getter
    @NoArgsConstructor
    public static class OrderItemDTO {
        @NotNull(message = "책 ID는 필수입니다.")
        private Long bookId;

        @Min(value = 1, message = "수량은 최소 1개 이상이어야 합니다.")
        private int count;
    }
}