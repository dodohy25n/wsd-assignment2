package hello.wsdassignment2.domain.user.service;

import hello.wsdassignment2.common.exception.CustomException;
import hello.wsdassignment2.domain.user.dto.SignupRequest;
import hello.wsdassignment2.domain.user.dto.UserResponse;
import hello.wsdassignment2.domain.user.dto.UserUpdateRequest;
import hello.wsdassignment2.domain.user.entity.User;
import hello.wsdassignment2.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void setUp() {
        user = createUserEntity();
        ReflectionTestUtils.setField(user, "id", 1L);
    }

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() {
        // given
        SignupRequest request = createSignupRequest("test@test.com", "password", "Test User");
        User savedUser = User.create(request.getEmail(), "encodedPassword", request.getEmail(), request.getName());
        ReflectionTestUtils.setField(savedUser, "id", 1L);

        given(userRepository.existsByUsername(request.getEmail())).willReturn(false);
        given(passwordEncoder.encode(request.getPassword())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(savedUser);

        // when
        Long userId = userService.signup(request);

        // then
        assertThat(userId).isEqualTo(savedUser.getId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 이메일")
    void signup_fail_duplicateEmail() {
        // given
        SignupRequest request = createSignupRequest("test@test.com", "password", "Test User");
        given(userRepository.existsByUsername(request.getEmail())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.signup(request))
                .isInstanceOf(CustomException.class)
                .extracting("detail")
                .isEqualTo("이미 가입된 이메일입니다.");
    }

    @Test
    @DisplayName("내 정보 조회 성공")
    void getMyInfo_success() {
        // given
        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));

        // when
        UserResponse response = userService.getMyInfo(user.getUsername());

        // then
        assertThat(response.getId()).isEqualTo(user.getId());
        assertThat(response.getEmail()).isEqualTo(user.getEmail());
        assertThat(response.getName()).isEqualTo(user.getName());
    }

    @Test
    @DisplayName("내 정보 조회 실패 - 사용자를 찾을 수 없음")
    void getMyInfo_fail_userNotFound() {
        // given
        String nonExistentUsername = "nonexistent@test.com";
        given(userRepository.findByUsername(nonExistentUsername)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getMyInfo(nonExistentUsername))
                .isInstanceOf(CustomException.class)
                .extracting("detail")
                .isEqualTo("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("내 정보 수정 성공 - 비밀번호와 이름 변경")
    void updateMyInfo_success_all() {
        // given
        UserUpdateRequest request = createUserUpdateRequest("newPassword", "New Name");
        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));
        given(passwordEncoder.encode(request.getPassword())).willReturn("newEncodedPassword");

        // when
        Long userId = userService.updateMyInfo(user.getUsername(), request);

        // then
        assertThat(userId).isEqualTo(user.getId());
        assertThat(user.getPassword()).isEqualTo("newEncodedPassword");
        assertThat(user.getName()).isEqualTo("New Name");
    }

    @Test
    @DisplayName("내 정보 수정 성공 - 이름만 변경")
    void updateMyInfo_success_nameOnly() {
        // given
        UserUpdateRequest request = createUserUpdateRequest(null, "New Name");
        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));

        // when
        Long userId = userService.updateMyInfo(user.getUsername(), request);

        // then
        assertThat(userId).isEqualTo(user.getId());
        assertThat(user.getPassword()).isEqualTo("encodedPassword"); // Password should not change
        assertThat(user.getName()).isEqualTo("New Name");
    }

    @Test
    @DisplayName("내 정보 수정 실패 - 사용자를 찾을 수 없음")
    void updateMyInfo_fail_userNotFound() {
        // given
        String nonExistentUsername = "nonexistent@test.com";
        UserUpdateRequest request = createUserUpdateRequest("newPassword", "New Name");
        given(userRepository.findByUsername(nonExistentUsername)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.updateMyInfo(nonExistentUsername, request))
                .isInstanceOf(CustomException.class)
                .extracting("detail")
                .isEqualTo("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void deleteUser_success() {
        // given
        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));

        // when
        userService.deleteUser(user.getUsername());

        // then
        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 사용자를 찾을 수 없음")
    void deleteUser_fail_userNotFound() {
        // given
        String nonExistentUsername = "nonexistent@test.com";
        given(userRepository.findByUsername(nonExistentUsername)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.deleteUser(nonExistentUsername))
                .isInstanceOf(CustomException.class)
                .extracting("detail")
                .isEqualTo("사용자를 찾을 수 없습니다.");
    }

    private User createUserEntity() {
        // In UserService, username is used as email.
        return User.create("test@test.com", "encodedPassword", "test@test.com", "Test User");
    }

    private SignupRequest createSignupRequest(String email, String password, String name) {
        return new SignupRequest(email, password, name);
    }

    private UserUpdateRequest createUserUpdateRequest(String password, String name) {
        return new UserUpdateRequest(password, name);
    }
}