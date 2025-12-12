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
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "존재하지 않는 사용자입니다."));

        Order order = Order.create(user);

        for (OrderRequest.OrderItemDTO itemDto : request.getItems()) {
            Book book = bookRepository.findById(itemDto.getBookId())
                    .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 책입니다."));
            book.removeStock(itemDto.getCount());
            OrderItem orderItem = OrderItem.create(book, itemDto.getCount());
            order.addOrderItem(orderItem);
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

        if (!order.isOwnedBy(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN, "주문을 수정할 권한이 없습니다.");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new CustomException(ErrorCode.STATE_CONFLICT, "대기중인 주문만 수정할 수 있습니다.");
        }

        if (request.getOrderItemIdsToDelete() != null) {
            for (Long orderItemId : request.getOrderItemIdsToDelete()) {
                OrderItem orderItem = order.findOrderItemById(orderItemId);
                orderItem.getBook().addStock(orderItem.getQuantity());
                order.removeOrderItem(orderItem);
            }
        }

        if (request.getItemsToUpdate() != null) {
            for (OrderUpdateRequest.ItemToUpdate itemUpdate : request.getItemsToUpdate()) {
                OrderItem orderItem = order.findOrderItemById(itemUpdate.getOrderItemId());
                int quantityDiff = itemUpdate.getCount() - orderItem.getQuantity();

                if (quantityDiff > 0) {
                    orderItem.getBook().removeStock(quantityDiff);
                } else {
                    orderItem.getBook().addStock(-quantityDiff);
                }
                orderItem.updateQuantity(itemUpdate.getCount());
            }
        }

        if (request.getItemsToAdd() != null) {
            for (OrderUpdateRequest.ItemToAdd itemAdd : request.getItemsToAdd()) {
                Book book = bookRepository.findById(itemAdd.getBookId())
                        .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 책입니다."));
                book.removeStock(itemAdd.getCount());
                OrderItem newOrderItem = OrderItem.create(book, itemAdd.getCount());
                order.addOrderItem(newOrderItem);
            }
        }

        order.calculateTotalPrice();
    }
}