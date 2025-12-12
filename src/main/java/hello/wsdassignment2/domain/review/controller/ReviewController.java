package hello.wsdassignment2.domain.review.controller;

import hello.wsdassignment2.common.response.CommonResponse;
import hello.wsdassignment2.common.response.PagedResponse;
import hello.wsdassignment2.domain.review.dto.ReviewCreateRequest;
import hello.wsdassignment2.domain.review.dto.ReviewResponse;
import hello.wsdassignment2.domain.review.dto.ReviewUpdateRequest;
import hello.wsdassignment2.domain.review.entity.Review;
import hello.wsdassignment2.domain.review.service.ReviewService;
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
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import hello.wsdassignment2.common.response.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Review", description = "리뷰 관련 API")
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 등록", description = "새로운 리뷰를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "리뷰 등록 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "입력값 유효성 검사 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 책",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<CommonResponse<Long>> createReview(
            @Parameter(description = "리뷰 생성 요청 정보", required = true) @Valid @RequestBody ReviewCreateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long reviewId = reviewService.createReview(userDetails.getUser().getId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommonResponse.success(reviewId));
    }

    @Operation(summary = "리뷰 단건 조회", description = "ID로 특정 리뷰를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리뷰",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{reviewId}")
    public ResponseEntity<CommonResponse<ReviewResponse>> getReview(
            @Parameter(description = "조회할 리뷰 ID", required = true) @PathVariable Long reviewId
    ) {
        Review review = reviewService.getReview(reviewId);
        return ResponseEntity.ok(CommonResponse.success(ReviewResponse.from(review)));
    }

    @Operation(summary = "전체 리뷰 목록 조회", description = "페이지네이션을 사용하여 모든 리뷰 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 목록 조회 성공", useReturnTypeSchema = true)
    })
    @GetMapping
    public ResponseEntity<PagedResponse<ReviewResponse>> getAllReviews(
                                                                        @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Review> reviewPage = reviewService.getAllReviews(pageable);
        Page<ReviewResponse> responsePage = reviewPage.map(ReviewResponse::from);

        return ResponseEntity.ok(PagedResponse.success(responsePage));
    }

    @Operation(summary = "리뷰 수정", description = "기존 리뷰를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 수정 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "입력값 유효성 검사 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "리뷰 수정 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리뷰",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{reviewId}")
    public ResponseEntity<CommonResponse<Void>> updateReview(
            @Parameter(description = "수정할 리뷰 ID", required = true) @PathVariable Long reviewId,
            @Parameter(description = "리뷰 수정 요청 정보", required = true) @Valid @RequestBody ReviewUpdateRequest request
    ) {
        reviewService.updateReview(reviewId, request);
        return ResponseEntity.ok(CommonResponse.success(null));
    }

    @Operation(summary = "리뷰 Soft Delete", description = "리뷰를 논리적으로 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 삭제 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "리뷰 삭제 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리뷰",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<CommonResponse<Void>> softDeleteReview(
            @Parameter(description = "삭제할 리뷰 ID", required = true) @PathVariable Long reviewId
    ) {
        reviewService.softDeleteReview(reviewId);
        return ResponseEntity.ok(CommonResponse.success(null));
    }

    @Operation(summary = "리뷰 Hard Delete ", description = "리뷰를 물리적으로 삭제합니다. ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 완전 삭제 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리뷰",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{reviewId}/hard")
    public ResponseEntity<CommonResponse<Void>> hardDeleteReview(
            @Parameter(description = "완전 삭제할 리뷰 ID", required = true) @PathVariable Long reviewId
    ) {
        reviewService.hardDeleteReview(reviewId);
        return ResponseEntity.ok(CommonResponse.success(null));
    }
}