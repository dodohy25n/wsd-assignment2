package hello.wsdassignment2.domain.order.service;

import hello.wsdassignment2.common.exception.CustomException;
import hello.wsdassignment2.domain.book.entity.Book;
import hello.wsdassignment2.domain.book.repository.BookRepository;
import hello.wsdassignment2.domain.order.dto.OrderRequest;
import hello.wsdassignment2.domain.order.entity.Order;
import hello.wsdassignment2.domain.order.entity.OrderItem;
import hello.wsdassignment2.domain.order.entity.OrderStatus;
import hello.wsdassignment2.domain.order.repository.OrderRepository;
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
        int orderCount = 5;
        OrderRequest request = createOrderRequest(book.getId(), orderCount);
        int initialStock = book.getStockQuantity();
        OrderRequest.OrderItemDTO itemDto = request.getItems().get(0);

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(bookRepository.findById(itemDto.getBookId())).willReturn(Optional.of(book));
        given(orderRepository.save(any(Order.class))).willReturn(order);

        // when
        Long orderId = orderService.createOrder(user.getId(), request);

        // then
        assertThat(orderId).isEqualTo(order.getId());
        assertThat(book.getStockQuantity()).isEqualTo(initialStock - orderCount);
    }

    @Test
    @DisplayName("주문 등록 실패: 사용자가 존재하지 않음")
    void createOrder_Fail_UserNotFound() {
        // given
        Long nonExistentUserId = 99L;
        OrderRequest request = createOrderRequest(book.getId(), 5);
        given(userRepository.findById(nonExistentUserId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(nonExistentUserId, request))
                .isInstanceOf(CustomException.class)
                .extracting("detail") // detail 필드를 꺼내서 검증
                .isEqualTo("존재하지 않는 사용자입니다.");
    }

    @Test
    @DisplayName("주문 등록 실패: 책이 존재하지 않음")
    void createOrder_Fail_BookNotFound() {
        // given
        OrderRequest request = createOrderRequest(99L, 5);
        OrderRequest.OrderItemDTO itemDto = request.getItems().get(0);
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(bookRepository.findById(itemDto.getBookId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(user.getId(), request))
                .isInstanceOf(CustomException.class)
                .extracting("detail")
                .isEqualTo("존재하지 않는 책입니다.");
    }

    @Test
    @DisplayName("주문 등록 실패: 재고 부족")
    void createOrder_Fail_OutOfStock() {
        // given
        OrderRequest request = createOrderRequest(book.getId(), 200); // More than stock
        OrderRequest.OrderItemDTO itemDto = request.getItems().get(0);
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(bookRepository.findById(itemDto.getBookId())).willReturn(Optional.of(book));

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(user.getId(), request))
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
        order.updateStatus(OrderStatus.SHIPPED);
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
        order.updateStatus(OrderStatus.CANCELLED);
        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> orderService.deleteOrder(orderId))
                .isInstanceOf(CustomException.class)
                .extracting("detail")
                .isEqualTo("이미 취소된 주문입니다.");
    }


    private User createUserEntity() {
        return User.create("testuser", "password", "test@example.com", "Test User");
    }

    private Book createBookEntity() {
        return Book.create(
                "Test Book",
                "A book for testing.",
                "978-0-06-112008-4",
                new BigDecimal("19.99"),
                100
        );
    }

    private Order createOrderEntity(User user, Book book) {
        Order newOrder = Order.create(user);
        newOrder.updateStatus(OrderStatus.PAID);
        newOrder.addOrderItem(
                hello.wsdassignment2.domain.order.entity.OrderItem.create(book, 10)
        );
        return newOrder;
    }

    private OrderRequest createOrderRequest(Long bookId, int count) {
        OrderRequest request = new OrderRequest();

        OrderRequest.OrderItemDTO itemDto = new OrderRequest.OrderItemDTO();
        ReflectionTestUtils.setField(itemDto, "bookId", bookId);
        ReflectionTestUtils.setField(itemDto, "count", count);

        ReflectionTestUtils.setField(request, "items", List.of(itemDto));
        return request;
    }

    @Test
    @DisplayName("주문 수정 성공 - 상품 수량 변경")
    void updateOrder_Success_UpdateQuantity() {
        // given
        order.updateStatus(OrderStatus.PENDING); // 테스트를 위해 PENDING 상태로 설정
        OrderItem orderItem = order.getOrderItems().get(0);
        ReflectionTestUtils.setField(orderItem, "id", 1L);
        int originalQuantity = orderItem.getQuantity();
        int newQuantity = originalQuantity + 2;
        int stockChange = newQuantity - originalQuantity;

        hello.wsdassignment2.domain.order.dto.OrderUpdateRequest request = new hello.wsdassignment2.domain.order.dto.OrderUpdateRequest();
        hello.wsdassignment2.domain.order.dto.OrderUpdateRequest.ItemToUpdate itemToUpdate = new hello.wsdassignment2.domain.order.dto.OrderUpdateRequest.ItemToUpdate();
        ReflectionTestUtils.setField(itemToUpdate, "orderItemId", orderItem.getId());
        ReflectionTestUtils.setField(itemToUpdate, "count", newQuantity);
        ReflectionTestUtils.setField(request, "itemsToUpdate", List.of(itemToUpdate));

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));
        int initialStock = book.getStockQuantity();

        // when
        orderService.updateOrder(user.getId(), order.getId(), request);

        // then
        assertThat(orderItem.getQuantity()).isEqualTo(newQuantity);
        assertThat(book.getStockQuantity()).isEqualTo(initialStock - stockChange);
        assertThat(order.getTotalPrice()).isNotEqualTo(new BigDecimal("199.90")); // 19.99 * 10
    }

    @Test
    @DisplayName("주문 수정 성공 - 상품 삭제")
    void updateOrder_Success_DeleteItem() {
        // given
        order.updateStatus(OrderStatus.PENDING);
        OrderItem orderItemToDelete = order.getOrderItems().get(0);
        ReflectionTestUtils.setField(orderItemToDelete, "id", 1L);
        int initialBookStock = book.getStockQuantity();
        int deletedQuantity = orderItemToDelete.getQuantity();

        hello.wsdassignment2.domain.order.dto.OrderUpdateRequest request = new hello.wsdassignment2.domain.order.dto.OrderUpdateRequest();
        ReflectionTestUtils.setField(request, "orderItemIdsToDelete", List.of(orderItemToDelete.getId()));

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        // when
        orderService.updateOrder(user.getId(), order.getId(), request);

        // then
        assertThat(order.getOrderItems()).isEmpty();
        assertThat(book.getStockQuantity()).isEqualTo(initialBookStock + deletedQuantity);
        assertThat(order.getTotalPrice()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("주문 수정 성공 - 상품 추가")
    void updateOrder_Success_AddItem() {
        // given
        order.updateStatus(OrderStatus.PENDING);
        Book newBook = Book.create("New Book", "summary", "123", BigDecimal.TEN, 50);
        ReflectionTestUtils.setField(newBook, "id", 2L);
        int newBookOrderCount = 5;

        hello.wsdassignment2.domain.order.dto.OrderUpdateRequest request = new hello.wsdassignment2.domain.order.dto.OrderUpdateRequest();
        hello.wsdassignment2.domain.order.dto.OrderUpdateRequest.ItemToAdd itemToAdd = new hello.wsdassignment2.domain.order.dto.OrderUpdateRequest.ItemToAdd();
        ReflectionTestUtils.setField(itemToAdd, "bookId", newBook.getId());
        ReflectionTestUtils.setField(itemToAdd, "count", newBookOrderCount);
        ReflectionTestUtils.setField(request, "itemsToAdd", List.of(itemToAdd));

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));
        given(bookRepository.findById(newBook.getId())).willReturn(Optional.of(newBook));

        // when
        orderService.updateOrder(user.getId(), order.getId(), request);

        // then
        assertThat(order.getOrderItems()).hasSize(2);
        assertThat(newBook.getStockQuantity()).isEqualTo(50 - newBookOrderCount);
    }


    @Test
    @DisplayName("주문 수정 실패 - PENDING 상태가 아님")
    void updateOrder_Fail_NotPending() {
        // given
        order.updateStatus(OrderStatus.SHIPPED); // Not PENDING
        hello.wsdassignment2.domain.order.dto.OrderUpdateRequest request = new hello.wsdassignment2.domain.order.dto.OrderUpdateRequest();
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> orderService.updateOrder(user.getId(), order.getId(), request))
                .isInstanceOf(CustomException.class)
                .extracting("detail")
                .isEqualTo("대기중인 주문만 수정할 수 있습니다.");
    }

    @Test
    @DisplayName("주문 수정 실패 - 소유자가 아님")
    void updateOrder_Fail_NotOwner() {
        // given
        order.updateStatus(OrderStatus.PENDING);
        Long otherUserId = 99L;
        hello.wsdassignment2.domain.order.dto.OrderUpdateRequest request = new hello.wsdassignment2.domain.order.dto.OrderUpdateRequest();
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> orderService.updateOrder(otherUserId, order.getId(), request))
                .isInstanceOf(CustomException.class)
                .extracting("detail")
                .isEqualTo("주문을 수정할 권한이 없습니다.");
    }
}

