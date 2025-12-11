package hello.wsdassignment2.domain.user.controller;

import hello.wsdassignment2.common.util.CookieUtil;
import hello.wsdassignment2.domain.user.dto.AuthTokens;
import hello.wsdassignment2.domain.user.dto.LoginRequest;
import hello.wsdassignment2.domain.user.dto.LoginResponse;
import hello.wsdassignment2.domain.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Auth", description = "사용자 인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @Operation(summary = "로그인", description = "사용자 이메일과 비밀번호로 로그인합니다.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Parameter(description = "로그인 요청 정보", required = true) @Valid @RequestBody LoginRequest request) {
        AuthTokens authTokens = authService.login(request);

        ResponseCookie refreshCookie = cookieUtil.createRefreshTokenCookie(authTokens.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(LoginResponse.of(authTokens.getAccessToken(), authTokens.getExpiresIn()));
    }

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰으로 새로운 액세스 토큰을 발급받습니다.")
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @Parameter(hidden = true) @CookieValue(value = "refreshToken", required = false) String refreshToken
    ) {
        AuthTokens authTokens = authService.refresh(refreshToken);

        // RRT 적용
        ResponseCookie refreshCookie = cookieUtil.createRefreshTokenCookie(authTokens.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(LoginResponse.of(authTokens.getAccessToken(), authTokens.getExpiresIn()));
    }

    @Operation(summary = "로그아웃", description = "사용자 로그아웃을 처리하고 리프레시 토큰을 무효화합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Parameter(hidden = true) @CookieValue(value = "refreshToken", required = false) String refreshToken
    ) {
        authService.logout(refreshToken);

        ResponseCookie deleteCookie = cookieUtil.createExpiredCookie("refreshToken");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }
}
