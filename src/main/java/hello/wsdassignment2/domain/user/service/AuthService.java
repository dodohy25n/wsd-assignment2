package hello.wsdassignment2.domain.user.service;

import hello.wsdassignment2.common.exception.CustomException;
import hello.wsdassignment2.common.exception.ErrorCode;
import hello.wsdassignment2.domain.user.dto.AuthTokens;
import hello.wsdassignment2.domain.user.dto.LoginRequest;
import hello.wsdassignment2.domain.user.entity.User;
import hello.wsdassignment2.domain.user.repository.UserRepository;
import hello.wsdassignment2.security.details.CustomUserDetails;
import hello.wsdassignment2.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    @Transactional
    public AuthTokens login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
            User user = principal.getUser();

            return generateTokenResponse(user);
        } catch (BadCredentialsException e) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }
    }

    @Transactional
    public AuthTokens refresh(String refreshToken) {
        if (refreshToken == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        // 토큰 유효성 검사 (만료/유효하지 않음 모두 예외 발생)
        jwtTokenProvider.validateToken(refreshToken);

        // 토큰에서 UserId 추출
        Long userId = jwtTokenProvider.getUserId(refreshToken);

        // Redis 비교
        String storedToken = refreshTokenService.getByUserId(userId);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            refreshTokenService.delete(userId);
            throw new CustomException(ErrorCode.INVALID_TOKEN, "리프레시 토큰이 만료되었거나 일치하지 않습니다.");
        }

        // 유저 조회 (유일한 DB 조회)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 토큰 재발급 (Rotation)
        return generateTokenResponse(user);
    }

    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken != null) {
            try {
                jwtTokenProvider.validateToken(refreshToken);
                Long userId = jwtTokenProvider.getUserId(refreshToken);
                refreshTokenService.delete(userId);
            } catch (CustomException e) {
                log.info("로그아웃 시 토큰 유효성 검사 실패: {}", e.getMessage());
                // 유효하지 않은 토큰으로 로그아웃 시도 시, 아무것도 하지 않고 넘어감
            }
        }
    }

    private AuthTokens generateTokenResponse(User user) {

        String accessToken = jwtTokenProvider.createAccessToken(
                user.getId(), user.getUsername(), user.getRole().name()
        );

        String refreshToken = jwtTokenProvider.createRefreshToken(
                user.getId(), user.getUsername(), user.getRole().name()
        );

        refreshTokenService.save(user.getId(), refreshToken);

        return new AuthTokens(accessToken, refreshToken, jwtTokenProvider.getAccessTokenExpiresIn());
    }
}
