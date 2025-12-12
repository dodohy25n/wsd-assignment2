package hello.wsdassignment2.domain.user.controller;

import hello.wsdassignment2.common.response.CommonResponse;
import hello.wsdassignment2.domain.user.dto.SignupRequest;
import hello.wsdassignment2.domain.user.service.UserService;
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
import hello.wsdassignment2.common.response.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "사용자 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "입력값 유효성 검사 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<CommonResponse<Long>> signup(
            @Parameter(description = "회원가입 요청 정보", required = true) @Valid @RequestBody SignupRequest request) {
        Long userId = userService.signup(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.success(userId));
    }
}
