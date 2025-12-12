package hello.wsdassignment2.domain.admin.controller;

import hello.wsdassignment2.common.response.CommonResponse;
import hello.wsdassignment2.common.response.ErrorResponse;
import hello.wsdassignment2.common.response.PagedResponse;
import hello.wsdassignment2.domain.admin.dto.UserSearchRequest;
import hello.wsdassignment2.domain.admin.service.UserManageService;
import hello.wsdassignment2.domain.user.dto.UserResponse;
import hello.wsdassignment2.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin User Management", description = "관리자용 사용자 관리 API")
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserManageController {

    private final UserManageService userManageService;

    @Operation(summary = "사용자 목록 조회 (검색/필터)", description = "페이지네이션과 검색 조건을 사용하여 사용자 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 목록 조회 성공", useReturnTypeSchema = true)
    })
    @GetMapping
    public ResponseEntity<PagedResponse<UserResponse>> getAllUsers(
            @ParameterObject @ModelAttribute UserSearchRequest request,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<User> userPage = userManageService.getAllUsers(request, pageable);
        Page<UserResponse> responsePage = userPage.map(UserResponse::new);

        return ResponseEntity.ok(PagedResponse.success(responsePage));
    }

    @Operation(summary = "사용자 단건 조회", description = "ID로 특정 사용자를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{userId}")
    public ResponseEntity<CommonResponse<UserResponse>> getUser(
            @Parameter(description = "조회할 사용자 ID", required = true) @PathVariable Long userId) {
        User user = userManageService.getUser(userId);
        return ResponseEntity.ok(CommonResponse.success(new UserResponse(user)));
    }
}
