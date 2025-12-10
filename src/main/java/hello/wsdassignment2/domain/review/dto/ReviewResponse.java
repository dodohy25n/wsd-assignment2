package hello.wsdassignment2.domain.review.dto;

import hello.wsdassignment2.domain.review.entity.Review;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewResponse {
    private final Long id;
    private final Long userId;
    private final String userNickname;
    private final Long bookId;
    private final String bookTitle;
    private final Integer rating;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Builder
    private ReviewResponse(Long id, Long userId, String userNickname, Long bookId, String bookTitle, Integer rating, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.userNickname = userNickname;
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
                .bookId(review.getBook().getId())
                .bookTitle(review.getBook().getTitle())
                .rating(review.getRating())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
