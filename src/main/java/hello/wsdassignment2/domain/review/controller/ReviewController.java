package hello.wsdassignment2.domain.review.controller;

import hello.wsdassignment2.common.response.ApiResponse;
import hello.wsdassignment2.domain.review.dto.ReviewCreateRequest;
import hello.wsdassignment2.domain.review.dto.ReviewResponse;
import hello.wsdassignment2.domain.review.dto.ReviewUpdateRequest;
import hello.wsdassignment2.domain.review.entity.Review;
import hello.wsdassignment2.domain.review.service.ReviewService;
import hello.wsdassignment2.security.details.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Review", description = "리뷰 관련 API")
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 등록", description = "새로운 리뷰를 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createReview(
            @Parameter(description = "리뷰 생성 요청 정보", required = true) @Valid @RequestBody ReviewCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long reviewId = reviewService.createReview(userDetails.getUser().getId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(reviewId));
    }

    @Operation(summary = "리뷰 단건 조회", description = "ID로 특정 리뷰를 조회합니다.")
    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReview(
            @Parameter(description = "조회할 리뷰 ID", required = true) @PathVariable Long reviewId
    ) {
        Review review = reviewService.getReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success(ReviewResponse.from(review)));
    }

    @Operation(summary = "전체 리뷰 목록 조회", description = "페이지네이션을 사용하여 모든 리뷰 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getAllReviews(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Review> reviewPage = reviewService.getAllReviews(pageable);
        Page<ReviewResponse> responsePage = reviewPage.map(ReviewResponse::from);

        return ResponseEntity.ok(ApiResponse.successPage(responsePage));
    }

    @Operation(summary = "리뷰 수정", description = "기존 리뷰를 수정합니다.")
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> updateReview(
            @Parameter(description = "수정할 리뷰 ID", required = true) @PathVariable Long reviewId,
            @Parameter(description = "리뷰 수정 요청 정보", required = true) @Valid @RequestBody ReviewUpdateRequest request
    ) {
        reviewService.updateReview(reviewId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "리뷰 Soft Delete", description = "리뷰를 논리적으로 삭제합니다.")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> softDeleteReview(
            @Parameter(description = "삭제할 리뷰 ID", required = true) @PathVariable Long reviewId
    ) {
        reviewService.softDeleteReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "리뷰 Hard Delete", description = "리뷰를 물리적으로 삭제합니다.")
    @DeleteMapping("/{reviewId}/hard")
    public ResponseEntity<ApiResponse<Void>> hardDeleteReview(
            @Parameter(description = "완전 삭제할 리뷰 ID", required = true) @PathVariable Long reviewId
    ) {
        reviewService.hardDeleteReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
