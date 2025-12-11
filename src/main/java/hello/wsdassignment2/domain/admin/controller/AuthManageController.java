package hello.wsdassignment2.domain.admin.controller;

import hello.wsdassignment2.common.exception.CustomException;
import hello.wsdassignment2.common.exception.ErrorCode;
import hello.wsdassignment2.domain.admin.dto.RefreshTokenResponse;
import hello.wsdassignment2.domain.admin.service.AuthManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AuthManageController {
    private final AuthManageService authManageService;

    // 전체 리프레시 토큰 목록 조회
    @GetMapping("/tokens")
    public ResponseEntity<List<RefreshTokenResponse>> getAllRefreshTokens() {
        List<RefreshTokenResponse> tokens = authManageService.getAllTokens();
        return ResponseEntity.ok(tokens);
    }

    // 특정 유저 리프레시 토큰 조회
    @GetMapping("/tokens/{userId}")
    public ResponseEntity<RefreshTokenResponse> getRefreshToken(@PathVariable Long userId) {
        RefreshTokenResponse token = Optional.ofNullable(authManageService.getByUserId(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "해당 사용자의 리프레시 토큰을 찾을 수 없습니다."));
        return ResponseEntity.ok(token);
    }

    // 특정 유저 리프레시 토큰 삭제
    @DeleteMapping("/tokens/{userId}")
    public ResponseEntity<Void> deleteRefreshToken(@PathVariable Long userId) {
        authManageService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}
