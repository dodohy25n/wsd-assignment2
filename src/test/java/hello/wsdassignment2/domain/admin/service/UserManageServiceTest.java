package hello.wsdassignment2.domain.admin.service;

import hello.wsdassignment2.common.exception.CustomException;
import hello.wsdassignment2.domain.admin.dto.UserSearchRequest;
import hello.wsdassignment2.domain.user.entity.Role;
import hello.wsdassignment2.domain.user.entity.User;
import hello.wsdassignment2.domain.user.repository.UserRepository;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserManageServiceTest {

    @InjectMocks
    private UserManageService userManageService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 단건 조회 성공")
    void getUser_Success() {
        // given
        Long userId = 1L;
        User user = createUserEntity();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when
        User result = userManageService.getUser(userId);

        // then
        assertThat(result.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    @DisplayName("사용자 단건 조회 실패: 존재하지 않음")
    void getUser_Fail_NotFound() {
        // given
        Long userId = 99L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userManageService.getUser(userId))
                .isInstanceOf(CustomException.class)
                .extracting("detail")
                .isEqualTo("존재하지 않는 사용자입니다.");
    }

    @Test
    @DisplayName("사용자 단건 조회 실패: 삭제된 사용자")
    void getUser_Fail_Deleted() {
        // given
        Long userId = 1L;
        User deletedUser = createUserEntity();
        ReflectionTestUtils.setField(deletedUser, "deletedAt", LocalDateTime.now());
        given(userRepository.findById(userId)).willReturn(Optional.of(deletedUser));

        // when & then
        assertThatThrownBy(() -> userManageService.getUser(userId))
                .isInstanceOf(CustomException.class)
                .extracting("detail")
                .isEqualTo("삭제된 사용자입니다.");
    }

    @Test
    @DisplayName("사용자 목록 조회 성공: 검색 조건 없음")
    void getAllUsers_NoRequest() {
        // given
        UserSearchRequest request = new UserSearchRequest();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<User> users = List.of(createUserEntity());
        Page<User> userPage = new PageImpl<>(users, pageRequest, users.size());

        given(userRepository.searchUsers(request, pageRequest)).willReturn(userPage);

        // when
        Page<User> result = userManageService.getAllUsers(request, pageRequest);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(userRepository, times(1)).searchUsers(request, pageRequest);
    }

    @Test
    @DisplayName("사용자 목록 조회 성공: 키워드(keyword) 포함")
    void getAllUsers_WithKeyword() {
        // given
        UserSearchRequest request = new UserSearchRequest();
        request.setKeyword("testuser");
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<User> users = List.of(createUserEntity());
        Page<User> userPage = new PageImpl<>(users, pageRequest, users.size());

        given(userRepository.searchUsers(request, pageRequest)).willReturn(userPage);

        // when
        Page<User> result = userManageService.getAllUsers(request, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("testuser");
        verify(userRepository).searchUsers(request, pageRequest);
    }

    @Test
    @DisplayName("사용자 목록 조회 성공: 역할(role) 필터 포함")
    void getAllUsers_WithRole() {
        // given
        UserSearchRequest request = new UserSearchRequest();
        request.setRole("ROLE_USER");
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<User> users = List.of(createUserEntity());
        Page<User> userPage = new PageImpl<>(users, pageRequest, users.size());

        given(userRepository.searchUsers(request, pageRequest)).willReturn(userPage);

        // when
        Page<User> result = userManageService.getAllUsers(request, pageRequest);

        // then
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getRole()).isEqualTo(Role.ROLE_USER);
        verify(userRepository).searchUsers(request, pageRequest);
    }

    @Test
    @DisplayName("사용자 목록 조회 성공: 결과 없음")
    void getAllUsers_NoResult() {
        // given
        UserSearchRequest request = new UserSearchRequest();
        request.setKeyword("nonexistent");
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<User> emptyPage = new PageImpl<>(Collections.emptyList(), pageRequest, 0);

        given(userRepository.searchUsers(request, pageRequest)).willReturn(emptyPage);

        // when
        Page<User> result = userManageService.getAllUsers(request, pageRequest);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(userRepository).searchUsers(request, pageRequest);
    }


    private User createUserEntity() {
        return User.builder()
                .id(1L)
                .username("testuser")
                .email("test@test.com")
                .password("password")
                .name("Test User")
                .role(Role.ROLE_USER)
                .build();
    }
}
