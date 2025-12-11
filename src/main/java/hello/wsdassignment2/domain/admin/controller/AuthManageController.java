package hello.wsdassignment2.domain.admin.controller;

import hello.wsdassignment2.domain.admin.dto.RefreshTokenResponse;
import hello.wsdassignment2.domain.admin.service.AuthManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
