package hello.wsdassignment2.domain.review.service;

import hello.wsdassignment2.common.exception.CustomException;
import hello.wsdassignment2.common.exception.ErrorCode;
import hello.wsdassignment2.domain.book.entity.Book;
import hello.wsdassignment2.domain.book.repository.BookRepository;
import hello.wsdassignment2.domain.review.dto.ReviewCreateRequest;
import hello.wsdassignment2.domain.review.dto.ReviewUpdateRequest;
import hello.wsdassignment2.domain.review.entity.Review;
import hello.wsdassignment2.domain.review.repository.ReviewRepository;
import hello.wsdassignment2.domain.user.entity.User;
import hello.wsdassignment2.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    // 리뷰 등록
    @Transactional
    public Long createReview(Long userId, ReviewCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "존재하지 않는 사용자입니다."));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 책입니다."));

        Review review = Review.create(
                user,
                book,
                request.getRating(),
                request.getContent()
        );

        return reviewRepository.save(review).getId();
    }

    // 리뷰 단건 조회 (삭제된 리뷰 제외)
    public Review getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 리뷰입니다."));
        if (review.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "삭제된 리뷰입니다.");
        }
        return review;
    }

    // 리뷰 목록 조회
    public Page<Review> getAllReviews(Pageable pageable) {
        return reviewRepository.findAllByDeletedAtIsNull(pageable);
    }

    // 리뷰 정보 수정
    @Transactional
    public void updateReview(Long reviewId, ReviewUpdateRequest request) {
        Review review = getReview(reviewId);
        review.updateInfo(request.getRating(), request.getContent());
    }

    // 특정 책의 리뷰 전체 조회
    public Page<Review> getAllReviewsByBook(Long bookId, Pageable pageable) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 책입니다."));
        return reviewRepository.findAllByBookAndDeletedAtIsNull(book, pageable);
    }

    // 리뷰 Soft Delete
    @Transactional
    public void softDeleteReview(Long reviewId) {
        Review review = getReview(reviewId);
        review.softDelete();
    }

    // 리뷰 Hard Delete
    @Transactional
    public void hardDeleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 리뷰입니다."));
        reviewRepository.delete(review);
    }
}
