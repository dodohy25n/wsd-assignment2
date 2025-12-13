package hello.wsdassignment2.domain.order.controller;

import hello.wsdassignment2.common.response.CommonResponse;
import hello.wsdassignment2.common.response.ErrorResponse;
import hello.wsdassignment2.common.response.PagedResponse;
import hello.wsdassignment2.domain.order.dto.OrderRequest;
import hello.wsdassignment2.domain.order.dto.OrderResponse;
import hello.wsdassignment2.domain.order.dto.OrderUpdateRequest;
import hello.wsdassignment2.domain.order.entity.Order;
import hello.wsdassignment2.domain.order.service.OrderService;
import hello.wsdassignment2.security.details.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Order", description = "주문 관련 API")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

        private final OrderService orderService;

        @Operation(summary = "주문 등록", description = "새로운 주문을 등록합니다.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "주문 등록 성공", useReturnTypeSchema = true),
                        @ApiResponse(responseCode = "400", description = "입력값 유효성 검사 실패 또는 재고 부족", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @PostMapping
        public ResponseEntity<CommonResponse<Long>> createOrder(
                        @Parameter(description = "주문 생성 요청 정보", required = true) @Valid @RequestBody OrderRequest request,
                        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
                Long orderId = orderService.createOrder(userDetails.getUser().getId(), request);
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(CommonResponse.success(orderId));
        }

        @Operation(summary = "주문 단건 조회", description = "ID로 특정 주문을 조회합니다.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "주문 조회 성공", useReturnTypeSchema = true),
                        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "존재하지 않는 주문", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @GetMapping("/{orderId}")
        public ResponseEntity<CommonResponse<OrderResponse>> getOrder(
                        @Parameter(description = "조회할 주문 ID", required = true) @PathVariable Long orderId) {
                Order order = orderService.getOrder(orderId);
                return ResponseEntity.ok(CommonResponse.success(OrderResponse.from(order)));
        }

        @Operation(summary = "주문 목록 조회 ", description = "주문 목록을 조회합니다. ")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "주문 목록 조회 성공", useReturnTypeSchema = true),
                        @ApiResponse(responseCode = "403", description = "접근 권한 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @GetMapping
        public ResponseEntity<PagedResponse<OrderResponse>> getAllOrders( // 반환 타입 변경
                        @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
                Page<Order> orderPage = orderService.getAllOrders(pageable);
                Page<OrderResponse> responsePage = orderPage.map(OrderResponse::from);

                return ResponseEntity.ok(PagedResponse.success(responsePage)); // PagedResponse 사용
        }

        @Operation(summary = "주문 취소", description = "ID로 특정 주문을 취소합니다.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "주문 취소 성공", useReturnTypeSchema = true),
                        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 또는 권한 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "존재하지 않는 주문", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "409", description = "취소할 수 없는 주문 상태 (예: 이미 배송중)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @DeleteMapping("/{orderId}")
        public ResponseEntity<CommonResponse<Void>> deleteOrder(
                        @Parameter(description = "취소할 주문 ID", required = true) @PathVariable Long orderId) {
                orderService.deleteOrder(orderId);
                return ResponseEntity.ok(CommonResponse.success(null));
        }

        @Operation(summary = "주문 수정", description = "대기중(PENDING)인 주문의 상품 수량 변경, 추가, 삭제를 처리합니다.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "주문 수정 성공", useReturnTypeSchema = true),
                        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "주문 수정 권한 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "존재하지 않는 주문/상품/책", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "409", description = "수정할 수 없는 주문 상태(대기중이 아님) 또는 재고 부족", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @PatchMapping("/{orderId}")
        public ResponseEntity<CommonResponse<Void>> updateOrder(
                        @Parameter(description = "수정할 주문 ID", required = true) @PathVariable Long orderId,
                        @Parameter(description = "주문 수정 요청 정보", required = true) @Valid @RequestBody OrderUpdateRequest request,
                        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
                orderService.updateOrder(userDetails.getUser().getId(), orderId, request);
                return ResponseEntity.ok(CommonResponse.success(null));
        }
}
