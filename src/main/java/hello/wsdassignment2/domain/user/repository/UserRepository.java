package hello.wsdassignment2.domain.user.repository;

import hello.wsdassignment2.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // 로그인/주문 시 유저 찾기용
}
