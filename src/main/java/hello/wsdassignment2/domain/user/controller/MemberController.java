package hello.wsdassignment2.domain.user.controller;

import hello.wsdassignment2.common.util.CookieUtil;
import hello.wsdassignment2.domain.user.dto.AuthTokens;
import hello.wsdassignment2.domain.user.dto.LoginRequest;
import hello.wsdassignment2.domain.user.dto.LoginResponse;
import hello.wsdassignment2.domain.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class MemberController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        AuthTokens authTokens = authService.login(request);

        ResponseCookie refreshCookie = cookieUtil.createRefreshTokenCookie(authTokens.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(LoginResponse.of(authTokens.getAccessToken(), authTokens.getExpiresIn()));
    }

//    @PostMapping("/refresh")
//    public ResponseEntity<LoginResponse> refresh(
//            @CookieValue(value = "refreshToken", required = false) String refreshToken
//    ) {
//        AuthTokens authTokens = authService.refresh(refreshToken);
//
//        // RRT 적용
//        ResponseCookie refreshCookie = cookieUtil.createRefreshTokenCookie(authTokens.getRefreshToken());
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
//                .body(LoginResponse.of(authTokens.getAccessToken(), authTokens.getExpiresIn()));
//    }
//
//    @PostMapping("/logout")
//    public ResponseEntity<Void> logout(
//            @CookieValue(value = "refreshToken", required = false) String refreshToken
//    ) {
//        authService.logout(refreshToken);
//
//        ResponseCookie deleteCookie = cookieUtil.createExpiredCookie("refreshToken");
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
//                .build();
//    }
}
