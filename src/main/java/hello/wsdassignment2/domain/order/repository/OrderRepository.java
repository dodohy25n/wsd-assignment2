package hello.wsdassignment2.domain.order.repository;

import hello.wsdassignment2.domain.order.entity.Order;
import hello.wsdassignment2.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user); // 내 주문 내역 조회
}