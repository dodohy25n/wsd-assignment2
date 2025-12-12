package hello.wsdassignment2.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Schema(description = "주문 수정 요청")
@Getter
@Setter
@NoArgsConstructor
public class OrderUpdateRequest {

    @Schema(description = "주문에 추가할 상품 목록")
    @Valid
    private List<ItemToAdd> itemsToAdd;

    @Schema(description = "주문에서 수량을 변경할 상품 목록")
    @Valid
    private List<ItemToUpdate> itemsToUpdate;

    @Schema(description = "주문에서 삭제할 상품의 ID 목록")
    private List<Long> orderItemIdsToDelete;

    @Schema(description = "추가할 상품 정보")
    @Getter
    @NoArgsConstructor
    public static class ItemToAdd {
        @Schema(description = "상품(책) ID", example = "2")
        @NotNull(message = "상품 ID를 입력해주세요.")
        private Long bookId;

        @Schema(description = "주문 수량", example = "3")
        @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
        private int count;
    }

    @Schema(description = "수량 변경 상품 정보")
    @Getter
    @NoArgsConstructor
    public static class ItemToUpdate {
        @Schema(description = "주문 상품 ID", example = "1")
        @NotNull(message = "주문 상품 ID를 입력해주세요.")
        private Long orderItemId;

        @Schema(description = "새로운 주문 수량", example = "5")
        @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
        private int count;
    }
}
