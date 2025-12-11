package hello.wsdassignment2.domain.review.controller;

import hello.wsdassignment2.common.response.ApiResponse;
import hello.wsdassignment2.domain.review.dto.ReviewCreateRequest;
import hello.wsdassignment2.domain.review.dto.ReviewResponse;
import hello.wsdassignment2.domain.review.dto.ReviewUpdateRequest;
import hello.wsdassignment2.domain.review.entity.Review;
import hello.wsdassignment2.domain.review.service.ReviewService;
import hello.wsdassignment2.security.details.CustomUserDetails;
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

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 등록
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createReview(
            @Valid @RequestBody ReviewCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long reviewId = reviewService.createReview(userDetails.getUser().getId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(reviewId));
    }

    // 리뷰 단건 조회
    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReview(@PathVariable Long reviewId) {
        Review review = reviewService.getReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success(ReviewResponse.from(review)));
    }

    // 전체 리뷰 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getAllReviews(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Review> reviewPage = reviewService.getAllReviews(pageable);
        Page<ReviewResponse> responsePage = reviewPage.map(ReviewResponse::from);

        return ResponseEntity.ok(ApiResponse.successPage(responsePage));
    }

    // 리뷰 수정
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> updateReview(@PathVariable Long reviewId,
                                                        @Valid @RequestBody ReviewUpdateRequest request) {
        reviewService.updateReview(reviewId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 리뷰 Soft Delete
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> softDeleteReview(@PathVariable Long reviewId) {
        reviewService.softDeleteReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 리뷰 Hard Delete
    @DeleteMapping("/{reviewId}/hard")
    public ResponseEntity<ApiResponse<Void>> hardDeleteReview(@PathVariable Long reviewId) {
        reviewService.hardDeleteReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
