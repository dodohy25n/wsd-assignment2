package hello.wsdassignment2.domain.order.service;

import hello.wsdassignment2.common.exception.CustomException;
import hello.wsdassignment2.common.exception.ErrorCode;
import hello.wsdassignment2.domain.book.entity.Book;
import hello.wsdassignment2.domain.book.repository.BookRepository;
import hello.wsdassignment2.domain.order.dto.OrderRequest;
import hello.wsdassignment2.domain.order.dto.OrderUpdateRequest;
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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Transactional
    public Long createOrder(Long userId, OrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 요청 내 동일 bookId 병합
        Map<Long, Integer> requestedCounts = request.getItems().stream()
                .collect(Collectors.toMap(
                        OrderRequest.OrderItemDTO::getBookId,
                        OrderRequest.OrderItemDTO::getCount,
                        Integer::sum
                ));

        // 책 일괄 조회 (비관적 락)
        Map<Long, Book> bookMap = bookRepository.findAllByIdForUpdate(requestedCounts.keySet()).stream()
                .collect(Collectors.toMap(Book::getId, Function.identity()));

        Order order = Order.create(user);

        for (Map.Entry<Long, Integer> entry : requestedCounts.entrySet()) {
            Book book = bookMap.get(entry.getKey());
            if (book == null) {
                throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 책입니다.");
            }

            book.removeStock(entry.getValue());
            order.addOrderItem(OrderItem.create(book, entry.getValue()));
        }

        return orderRepository.save(order).getId();
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 주문입니다."));
    }

    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

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

    @Transactional
    public void updateOrder(Long userId, Long orderId, OrderUpdateRequest request) {
        Order order = getOrder(orderId);
        validateOrderModification(order, userId);

        validateUpdateRequest(request);

        // 주문 아이템 캐싱
        Map<Long, OrderItem> itemById = order.getOrderItems().stream()
                .collect(Collectors.toMap(OrderItem::getId, Function.identity()));

        /* ---------- 삭제 ---------- */
        if (request.getOrderItemIdsToDelete() != null) {
            for (Long itemId : request.getOrderItemIdsToDelete()) {
                OrderItem item = getItemOrThrow(itemById, itemId);
                item.getBook().addStock(item.getQuantity());
                order.removeOrderItem(item);
            }
        }

        /* ---------- 수량 변경 ---------- */
        if (request.getItemsToUpdate() != null) {
            for (OrderUpdateRequest.ItemToUpdate dto : request.getItemsToUpdate()) {
                OrderItem item = getItemOrThrow(itemById, dto.getOrderItemId());
                int diff = dto.getCount() - item.getQuantity();

                if (diff > 0) item.getBook().removeStock(diff);
                if (diff < 0) item.getBook().addStock(-diff);

                item.updateQuantity(dto.getCount());
            }
        }

        /* ---------- 추가 / 병합 ---------- */
        if (request.getItemsToAdd() != null) {
            Map<Long, OrderItem> itemByBookId = order.getOrderItems().stream()
                    .collect(Collectors.toMap(
                            item -> item.getBook().getId(),
                            Function.identity()
                    ));

            Map<Long, Integer> addCounts = request.getItemsToAdd().stream()
                    .collect(Collectors.toMap(
                            OrderUpdateRequest.ItemToAdd::getBookId,
                            OrderUpdateRequest.ItemToAdd::getCount,
                            Integer::sum
                    ));

            Map<Long, Book> bookMap = bookRepository.findAllByIdForUpdate(addCounts.keySet()).stream()
                    .collect(Collectors.toMap(Book::getId, Function.identity()));

            for (Map.Entry<Long, Integer> entry : addCounts.entrySet()) {
                Book book = bookMap.get(entry.getKey());
                if (book == null) {
                    throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
                }

                book.removeStock(entry.getValue());

                if (itemByBookId.containsKey(book.getId())) {
                    itemByBookId.get(book.getId()).addQuantity(entry.getValue());
                } else {
                    order.addOrderItem(OrderItem.create(book, entry.getValue()));
                }
            }
        }

        order.calculateTotalPrice();
    }


    private void validateOrderModification(Order order, Long userId) {
        if (!order.isOwnedBy(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN, "주문을 수정할 권한이 없습니다.");
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new CustomException(ErrorCode.STATE_CONFLICT, "대기중인 주문만 수정할 수 있습니다.");
        }
    }

    private void validateUpdateRequest(OrderUpdateRequest request) {
        if ((request.getItemsToAdd() == null || request.getItemsToAdd().isEmpty()) &&
                (request.getItemsToUpdate() == null || request.getItemsToUpdate().isEmpty()) &&
                (request.getOrderItemIdsToDelete() == null || request.getOrderItemIdsToDelete().isEmpty())) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "변경 사항이 없습니다.");
        }

        if (request.getItemsToUpdate() != null && request.getOrderItemIdsToDelete() != null) {
            Set<Long> deleteIds = new HashSet<>(request.getOrderItemIdsToDelete());
            for (OrderUpdateRequest.ItemToUpdate dto : request.getItemsToUpdate()) {
                if (deleteIds.contains(dto.getOrderItemId())) {
                    throw new CustomException(ErrorCode.VALIDATION_FAILED, "삭제 대상은 수정할 수 없습니다.");
                }
            }
        }
    }

    private OrderItem getItemOrThrow(Map<Long, OrderItem> map, Long id) {
        OrderItem item = map.get(id);
        if (item == null) {
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        return item;
    }
}