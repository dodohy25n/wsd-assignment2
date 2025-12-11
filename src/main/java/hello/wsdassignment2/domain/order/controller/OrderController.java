package hello.wsdassignment2.domain.order.controller;

import hello.wsdassignment2.common.response.ApiResponse;
import hello.wsdassignment2.domain.order.dto.OrderRequest;
import hello.wsdassignment2.domain.order.dto.OrderResponse;
import hello.wsdassignment2.domain.order.entity.Order;
import hello.wsdassignment2.domain.order.service.OrderService;
import hello.wsdassignment2.security.details.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Order", description = "주문 관련 API")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "주문 등록", description = "새로운 주문을 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createOrder(
            @Parameter(description = "주문 생성 요청 정보", required = true) @Valid @RequestBody OrderRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long orderId = orderService.createOrder(userDetails.getUser().getId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(orderId));
    }

    @Operation(summary = "주문 단건 조회", description = "ID로 특정 주문을 조회합니다.")
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @Parameter(description = "조회할 주문 ID", required = true) @PathVariable Long orderId) {
        Order order = orderService.getOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success(OrderResponse.from(order)));
    }

    @Operation(summary = "주문 목록 조회", description = "페이지네이션을 사용하여 모든 주문 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Order> orderPage = orderService.getAllOrders(pageable);
        Page<OrderResponse> responsePage = orderPage.map(OrderResponse::from);
        return ResponseEntity.ok(ApiResponse.successPage(responsePage));
    }

    @Operation(summary = "주문 취소", description = "ID로 특정 주문을 취소합니다.")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(
            @Parameter(description = "취소할 주문 ID", required = true) @PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
