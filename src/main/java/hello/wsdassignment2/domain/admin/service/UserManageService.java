package hello.wsdassignment2.domain.admin.service;

import hello.wsdassignment2.common.exception.CustomException;
import hello.wsdassignment2.common.exception.ErrorCode;
import hello.wsdassignment2.domain.admin.dto.UserSearchRequest;
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
public class UserManageService {

    private final UserRepository userRepository;

    public Page<User> getAllUsers(UserSearchRequest request, Pageable pageable) {
        return userRepository.searchUsers(request, pageable);
    }

    public User getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 사용자입니다."));

        if (user.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "삭제된 사용자입니다.");
        }
        return user;
    }
}
