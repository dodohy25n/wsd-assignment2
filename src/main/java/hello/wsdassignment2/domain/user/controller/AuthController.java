package hello.wsdassignment2.domain.user.controller;

import hello.wsdassignment2.common.response.CommonResponse;
import hello.wsdassignment2.common.util.CookieUtil;
import hello.wsdassignment2.domain.user.dto.AuthTokens;
import hello.wsdassignment2.domain.user.dto.LoginRequest;
import hello.wsdassignment2.domain.user.dto.LoginResponse;
import hello.wsdassignment2.domain.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import hello.wsdassignment2.common.response.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User Auth", description = "사용자 인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @Operation(summary = "로그인", description = "사용자 이메일과 비밀번호로 로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호가 일치하지 않음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<CommonResponse<LoginResponse>> login(
            @Parameter(description = "로그인 요청 정보", required = true) @Valid @RequestBody LoginRequest request) {
        AuthTokens authTokens = authService.login(request);

        ResponseCookie refreshCookie = cookieUtil.createRefreshTokenCookie(authTokens.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(CommonResponse.success(LoginResponse.of(authTokens.getAccessToken(), authTokens.getExpiresIn())));
    }

    @Operation(summary = "토큰 갱신", description = "리프레시 토큰으로 새로운 액세스 토큰을 발급받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 갱신 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/refresh")
    public ResponseEntity<CommonResponse<LoginResponse>> refresh(
            @Parameter(hidden = true) @CookieValue(value = "refreshToken", required = false) String refreshToken
    ) {
        AuthTokens authTokens = authService.refresh(refreshToken);

        // RRT 적용
        ResponseCookie refreshCookie = cookieUtil.createRefreshTokenCookie(authTokens.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(CommonResponse.success(LoginResponse.of(authTokens.getAccessToken(), authTokens.getExpiresIn())));
    }

    @Operation(summary = "로그아웃", description = "사용자 로그아웃을 처리하고 리프레시 토큰을 무효화합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<Void>> logout(
            @Parameter(hidden = true) @CookieValue(value = "refreshToken", required = false) String refreshToken
    ) {
        authService.logout(refreshToken);

        // 쿠키 만료 처리
        ResponseCookie deleteCookie = cookieUtil.createExpiredCookie("refreshToken");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(CommonResponse.success(null)); // void 타입이므로 null 반환
    }
}