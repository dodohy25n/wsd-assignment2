package hello.wsdassignment2.domain.admin.controller;

import hello.wsdassignment2.common.exception.CustomException;
import hello.wsdassignment2.common.exception.ErrorCode;
import hello.wsdassignment2.common.response.CommonResponse;
import hello.wsdassignment2.domain.admin.dto.RefreshTokenResponse;
import hello.wsdassignment2.domain.admin.service.AuthManageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import hello.wsdassignment2.common.response.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Admin Auth Management", description = "관리자용 인증 관리 API")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AuthManageController {
    private final AuthManageService authManageService;

    @Operation(summary = "전체 리프레시 토큰 목록 조회 ", description = "시스템에 저장된 모든 리프레시 토큰 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 목록 조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/tokens")
    public ResponseEntity<CommonResponse<List<RefreshTokenResponse>>> getAllRefreshTokens() {
        List<RefreshTokenResponse> tokens = authManageService.getAllTokens();
        return ResponseEntity.ok(CommonResponse.success(tokens));
    }

    @Operation(summary = "특정 유저 리프레시 토큰 조회 ", description = "사용자 ID로 특정 사용자의 리프레시 토큰을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/tokens/{userId}")
    public ResponseEntity<CommonResponse<RefreshTokenResponse>> getRefreshToken(
            @Parameter(description = "조회할 사용자의 ID", required = true) @PathVariable Long userId) {
        RefreshTokenResponse token = Optional.ofNullable(authManageService.getByUserId(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "해당 사용자의 리프레시 토큰을 찾을 수 없습니다."));
        return ResponseEntity.ok(CommonResponse.success(token));
    }

    @Operation(summary = "특정 유저 리프레시 토큰 삭제 ", description = "사용자 ID로 특정 사용자의 리프레시 토큰을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 삭제 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/tokens/{userId}")
    public ResponseEntity<CommonResponse<Void>> deleteRefreshToken(
            @Parameter(description = "삭제할 사용자의 ID", required = true) @PathVariable Long userId) {
        authManageService.delete(userId);
        return ResponseEntity.ok(CommonResponse.success(null));
    }
}