package hello.wsdassignment2.domain.review.dto;

import hello.wsdassignment2.domain.review.entity.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "리뷰 응답")
@Getter
public class ReviewResponse {
    @Schema(description = "리뷰 ID", example = "1")
    private final Long id;
    @Schema(description = "사용자 ID", example = "1")
    private final Long userId;
    @Schema(description = "사용자 이름", example = "도현")
    private final String name;
    @Schema(description = "책 ID", example = "1")
    private final Long bookId;
    @Schema(description = "책 제목", example = "JPA 정복")
    private final String bookTitle;
    @Schema(description = "별점", example = "5")
    private final Integer rating;
    @Schema(description = "리뷰 내용", example = "아주 유익한 책입니다.")
    private final String content;
    @Schema(description = "생성일")
    private final LocalDateTime createdAt;
    @Schema(description = "수정일")
    private final LocalDateTime updatedAt;

    @Builder
    private ReviewResponse(Long id, Long userId, String name, Long bookId, String bookTitle, Integer rating, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.rating = rating;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ReviewResponse from(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .name(review.getUser().getName())
                .bookId(review.getBook().getId())
                .bookTitle(review.getBook().getTitle())
                .rating(review.getRating())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
