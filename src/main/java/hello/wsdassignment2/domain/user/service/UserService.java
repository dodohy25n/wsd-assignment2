package hello.wsdassignment2.domain.user.service;

import hello.wsdassignment2.common.exception.CustomException;
import hello.wsdassignment2.common.exception.ErrorCode;
import hello.wsdassignment2.domain.user.dto.SignupRequest;
import hello.wsdassignment2.domain.user.dto.UserResponse;
import hello.wsdassignment2.domain.user.dto.UserUpdateRequest;
import hello.wsdassignment2.domain.user.entity.User;
import hello.wsdassignment2.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long signup(SignupRequest request) {

        if (userRepository.existsByUsername(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE, "이미 가입된 이메일입니다.");
        }

        User user = User.create(request.getEmail(), passwordEncoder.encode(request.getPassword()), request.getEmail(), request.getName());
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    public UserResponse getMyInfo(String username) {
        User user = findByUsernameOrThrow(username);
        return UserResponse.from(user);
    }

    @Transactional
    public Long updateMyInfo(String username, UserUpdateRequest request) {
        User user = findByUsernameOrThrow(username);

        String newPassword = (request.getPassword() != null) ? passwordEncoder.encode(request.getPassword()) : null;
        user.update(newPassword, request.getName());
        return user.getId();
    }

    @Transactional
    public void deleteUser(String username) {
        User user = findByUsernameOrThrow(username);
        userRepository.delete(user);
    }

    private User findByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "사용자를 찾을 수 없습니다."));
    }
}
