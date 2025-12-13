package hello.wsdassignment2.domain.user.controller;

import hello.wsdassignment2.common.response.CommonResponse;
import hello.wsdassignment2.common.response.ErrorResponse;
import hello.wsdassignment2.domain.user.dto.SignupRequest;
import hello.wsdassignment2.domain.user.dto.UserResponse;
import hello.wsdassignment2.domain.user.dto.UserUpdateRequest;
import hello.wsdassignment2.domain.user.service.UserService;
import hello.wsdassignment2.security.details.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "사용자 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

        private final UserService userService;

        @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "회원가입 성공", useReturnTypeSchema = true),
                        @ApiResponse(responseCode = "400", description = "입력값 유효성 검사 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @PostMapping
        public ResponseEntity<CommonResponse<Long>> signup(
                        @Parameter(description = "회원가입 요청 정보", required = true) @Valid @RequestBody SignupRequest request) {
                Long userId = userService.signup(request);
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(CommonResponse.success(userId));
        }

        @Operation(summary = "내 정보 조회", description = "현재 로그인된 사용자의 정보를 조회합니다.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true), // [수정]
                                                                                                               // 스키마 적용
                        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @GetMapping("/me")
        public ResponseEntity<CommonResponse<UserResponse>> getMyInfo(
                        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {
                UserResponse response = userService.getMyInfo(userDetails.getUsername());
                return ResponseEntity.ok(CommonResponse.success(response));
        }

        @Operation(summary = "내 정보 수정", description = "현재 로그인된 사용자의 정보를 수정합니다.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "수정 성공", useReturnTypeSchema = true), // [수정]
                                                                                                               // 201 ->
                                                                                                               // 200
                        @ApiResponse(responseCode = "400", description = "입력값 유효성 검사 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @PutMapping("/me")
        public ResponseEntity<CommonResponse<Long>> updateMyInfo(
                        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
                        @Parameter(description = "수정할 사용자 정보", required = true) @Valid @RequestBody UserUpdateRequest request) {
                Long userId = userService.updateMyInfo(userDetails.getUsername(), request);

                return ResponseEntity.ok(CommonResponse.success(userId));
        }

        @Operation(summary = "회원 탈퇴", description = "현재 로그인된 사용자를 탈퇴 처리합니다.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "탈퇴 성공", useReturnTypeSchema = true), // [수정]
                                                                                                               // 스키마 적용
                        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @DeleteMapping("/me")
        public ResponseEntity<CommonResponse<Void>> deleteUser(
                        @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails) {

                userService.deleteUser(userDetails.getUsername());

                return ResponseEntity.ok(CommonResponse.success(null));
        }
}