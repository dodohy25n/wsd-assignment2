package hello.wsdassignment2.domain.order.service;

import hello.wsdassignment2.common.exception.CustomException;
import hello.wsdassignment2.domain.book.entity.Book;
import hello.wsdassignment2.domain.book.repository.BookRepository;
import hello.wsdassignment2.domain.order.dto.OrderRequest;
import hello.wsdassignment2.domain.order.entity.Order;
import hello.wsdassignment2.domain.order.entity.OrderStatus;
import hello.wsdassignment2.domain.order.repository.OrderRepository;
import hello.wsdassignment2.domain.user.entity.Role;
import hello.wsdassignment2.domain.user.entity.User;
import hello.wsdassignment2.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    private User user;
    private Book book;
    private Order order;

    @BeforeEach
    void setUp() {
        user = createUserEntity();
        ReflectionTestUtils.setField(user, "id", 1L);

        book = createBookEntity();
        ReflectionTestUtils.setField(book, "id", 1L);

        order = createOrderEntity(user, book);
        ReflectionTestUtils.setField(order, "id", 1L);
    }

    @Test
    @DisplayName("주문 등록 성공")
    void createOrder_Success() {
        // given
        OrderRequest request = createOrderRequest(user.getId(), book.getId(), 5);
        int initialStock = book.getStockQuantity();

        given(userRepository.findById(request.getUserId())).willReturn(Optional.of(user));
        given(bookRepository.findById(request.getBookId())).willReturn(Optional.of(book));
        given(orderRepository.save(any(Order.class))).willReturn(order);

        // when
        Long orderId = orderService.createOrder(request);

        // then
        assertThat(orderId).isEqualTo(order.getId());
        assertThat(book.getStockQuantity()).isEqualTo(initialStock - request.getCount());
    }

    @Test
    @DisplayName("주문 등록 실패: 사용자가 존재하지 않음")
    void createOrder_Fail_UserNotFound() {
        // given
        OrderRequest request = createOrderRequest(99L, book.getId(), 5);
        given(userRepository.findById(request.getUserId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(CustomException.class)
                .extracting("detail") // detail 필드를 꺼내서 검증
                .isEqualTo("존재하지 않는 사용자입니다.");
    }

    @Test
    @DisplayName("주문 등록 실패: 책이 존재하지 않음")
    void createOrder_Fail_BookNotFound() {
        // given
        OrderRequest request = createOrderRequest(user.getId(), 99L, 5);
        given(userRepository.findById(request.getUserId())).willReturn(Optional.of(user));
        given(bookRepository.findById(request.getBookId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(CustomException.class)
                .extracting("detail")
                .isEqualTo("존재하지 않는 책입니다.");
    }

    @Test
    @DisplayName("주문 등록 실패: 재고 부족")
    void createOrder_Fail_OutOfStock() {
        // given
        OrderRequest request = createOrderRequest(user.getId(), book.getId(), 200); // More than stock
        given(userRepository.findById(request.getUserId())).willReturn(Optional.of(user));
        given(bookRepository.findById(request.getBookId())).willReturn(Optional.of(book));

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(CustomException.class)
                .extracting("detail")
                .isEqualTo("재고가 부족합니다.");
    }

    @Test
    @DisplayName("주문 단건 조회 성공")
    void getOrder_Success() {
        // given
        Long orderId = 1L;
        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        // when
        Order foundOrder = orderService.getOrder(orderId);

        // then
        assertThat(foundOrder).isEqualTo(order);
    }

    @Test
    @DisplayName("주문 단건 조회 실패: 존재하지 않는 주문")
    void getOrder_Fail_NotFound() {
        // given
        Long orderId = 99L;
        given(orderRepository.findById(orderId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.getOrder(orderId))
                .isInstanceOf(CustomException.class)
                .extracting("detail")
                .isEqualTo("존재하지 않는 주문입니다.");
    }

    @Test
    @DisplayName("주문 목록 조회 성공")
    void getAllOrders_Success() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Order> orders = List.of(order);
        Page<Order> orderPage = new PageImpl<>(orders, pageRequest, orders.size());

        given(orderRepository.findAll(pageRequest)).willReturn(orderPage);

        // when
        Page<Order> result = orderService.getAllOrders(pageRequest);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(order);
    }

    @Test
    @DisplayName("주문 취소 성공")
    void deleteOrder_Success() {
        // given
        Long orderId = 1L;
        int initialStock = book.getStockQuantity();
        int orderQuantity = order.getOrderItems().get(0).getQuantity();
        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        // when
        orderService.deleteOrder(orderId);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(book.getStockQuantity()).isEqualTo(initialStock + orderQuantity);
    }

    @Test
    @DisplayName("주문 취소 실패: 이미 배송됨")
    void deleteOrder_Fail_AlreadyShipped() {
        // given
        Long orderId = 1L;
        order.setStatus(OrderStatus.SHIPPED);
        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> orderService.deleteOrder(orderId))
                .isInstanceOf(CustomException.class)
                .extracting("detail")
                .isEqualTo("이미 배송된 상품은 취소가 불가능합니다.");
    }

    @Test
    @DisplayName("주문 취소 실패: 이미 취소됨")
    void deleteOrder_Fail_AlreadyCancelled() {
        // given
        Long orderId = 1L;
        order.setStatus(OrderStatus.CANCELLED);
        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> orderService.deleteOrder(orderId))
                .isInstanceOf(CustomException.class)
                .extracting("detail")
                .isEqualTo("이미 취소된 주문입니다.");
    }


    private User createUserEntity() {
        return User.builder()
                .email("test@example.com")
                .password("password")
                .role(Role.ROLE_USER)
                .build();
    }

    private Book createBookEntity() {
        return Book.builder()
                .title("Test Book")
                .summary("A book for testing.")
                .isbn("978-0-06-112008-4")
                .price(new BigDecimal("19.99"))
                .stockQuantity(100)
                .build();
    }

    private Order createOrderEntity(User user, Book book) {
        Order newOrder = Order.builder()
                .user(user)
                .status(OrderStatus.PAID)
                .build();
        newOrder.addOrderItem(
                hello.wsdassignment2.domain.order.entity.OrderItem.builder()
                        .book(book)
                        .quantity(10)
                        .priceAtOrder(book.getPrice())
                        .build()
        );
        return newOrder;
    }

    private OrderRequest createOrderRequest(Long userId, Long bookId, int count) {
        OrderRequest request = new OrderRequest();
        ReflectionTestUtils.setField(request, "userId", userId);
        ReflectionTestUtils.setField(request, "bookId", bookId);
        ReflectionTestUtils.setField(request, "count", count);
        return request;
    }
}