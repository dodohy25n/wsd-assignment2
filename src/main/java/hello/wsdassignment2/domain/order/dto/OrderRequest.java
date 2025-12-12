package hello.wsdassignment2.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "주문 등록 요청")
@Getter
@Setter
@NoArgsConstructor
public class OrderRequest {

    @Schema(description = "주문할 상품 목록")
    @NotEmpty(message = "주문할 상품은 최소 1개 이상이어야 합니다.")
    @Valid // 리스트 내부의 객체들도 검증
    private List<OrderItemDTO> items;

    @Schema(description = "주문 상품 정보")
    @Getter
    @Setter
    @NoArgsConstructor
    public static class OrderItemDTO {
        @Schema(description = "책 ID", example = "1")
        @NotNull(message = "책 ID는 필수입니다.")
        private Long bookId;

        @Schema(description = "수량", example = "2")
        @Min(value = 1, message = "수량은 최소 1개 이상이어야 합니다.")
        private int count;
    }
}