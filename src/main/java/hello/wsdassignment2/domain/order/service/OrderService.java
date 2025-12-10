package hello.wsdassignment2.domain.order.service;

import hello.wsdassignment2.common.exception.CustomException;
import hello.wsdassignment2.common.exception.ErrorCode;
import hello.wsdassignment2.domain.book.entity.Book;
import hello.wsdassignment2.domain.book.repository.BookRepository;
import hello.wsdassignment2.domain.order.dto.OrderRequest;
import hello.wsdassignment2.domain.order.entity.Order;
import hello.wsdassignment2.domain.order.entity.OrderItem;
import hello.wsdassignment2.domain.order.entity.OrderStatus;
import hello.wsdassignment2.domain.order.repository.OrderRepository;
import hello.wsdassignment2.domain.user.entity.User;
import hello.wsdassignment2.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    // 주문 등록
    @Transactional
    public Long createOrder(OrderRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "존재하지 않는 사용자입니다."));

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .build();

        for (OrderRequest.OrderItemDTO itemDto : request.getItems()) {

            // 책 조회
            Book book = bookRepository.findById(itemDto.getBookId())
                    .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 책입니다."));

            // 재고 차감
            book.removeStock(itemDto.getCount());

            // 주문 아이템 생성
            OrderItem orderItem = OrderItem.builder()
                    .book(book)
                    .quantity(itemDto.getCount())
                    .priceAtOrder(book.getPrice())
                    .build();

            // 주문에 추가
            order.addOrderItem(orderItem);
        }

        return orderRepository.save(order).getId();
    }

    // 주문 단건 조회
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 주문입니다."));
    }

    // 주문 목록 조회
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    // 주문 취소
    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = getOrder(orderId);

        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new CustomException(ErrorCode.STATE_CONFLICT, "이미 배송된 상품은 취소가 불가능합니다.");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new CustomException(ErrorCode.STATE_CONFLICT, "이미 취소된 주문입니다.");
        }

        order.cancel();

        for (OrderItem orderItem : order.getOrderItems()) {
            orderItem.getBook().addStock(orderItem.getQuantity());
        }
    }
}
