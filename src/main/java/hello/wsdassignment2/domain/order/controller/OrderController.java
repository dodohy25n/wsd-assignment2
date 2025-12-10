package hello.wsdassignment2.domain.order.controller;

import hello.wsdassignment2.common.response.ApiResponse;
import hello.wsdassignment2.domain.order.dto.OrderRequest;
import hello.wsdassignment2.domain.order.dto.OrderResponse;
import hello.wsdassignment2.domain.order.entity.Order;
import hello.wsdassignment2.domain.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 등록
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createOrder(@Valid @RequestBody OrderRequest request) {
        Long orderId = orderService.createOrder(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(orderId));
    }

    // 주문 단건 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable Long orderId) {
        Order order = orderService.getOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success(OrderResponse.from(order)));
    }

    // 주문 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Order> orderPage = orderService.getAllOrders(pageable);
        Page<OrderResponse> responsePage = orderPage.map(OrderResponse::from);
        return ResponseEntity.ok(ApiResponse.successPage(responsePage));
    }

    // 주문 취소
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
