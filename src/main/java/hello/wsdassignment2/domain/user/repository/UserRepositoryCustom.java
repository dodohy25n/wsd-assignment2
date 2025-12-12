package hello.wsdassignment2.domain.user.repository;

import hello.wsdassignment2.domain.admin.dto.UserSearchRequest;
import hello.wsdassignment2.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {
    Page<User> searchUsers(UserSearchRequest request, Pageable pageable);
}
