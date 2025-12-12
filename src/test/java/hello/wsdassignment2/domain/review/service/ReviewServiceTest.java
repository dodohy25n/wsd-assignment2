package hello.wsdassignment2.domain.review.service;

import hello.wsdassignment2.common.exception.CustomException;
import hello.wsdassignment2.domain.book.dto.BookStatResponse;
import hello.wsdassignment2.domain.book.entity.Book;
import hello.wsdassignment2.domain.book.repository.BookRepository;
import hello.wsdassignment2.domain.review.dto.ReviewCreateRequest;
import hello.wsdassignment2.domain.review.dto.ReviewUpdateRequest;
import hello.wsdassignment2.domain.review.entity.Review;
import hello.wsdassignment2.domain.review.repository.ReviewRepository;
import hello.wsdassignment2.domain.user.entity.User;
import hello.wsdassignment2.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    private User user;
    private Book book;
    private Review review;

    @BeforeEach
    void setUp() {
        user = createUserEntity();
        ReflectionTestUtils.setField(user, "id", 1L);

        book = createBookEntity();
        ReflectionTestUtils.setField(book, "id", 1L);

        review = createReviewEntity(user, book);
        ReflectionTestUtils.setField(review, "id", 1L);
    }

    @Test
    @DisplayName("리뷰 등록 성공")
    void createReview_Success() {
        // given
        ReviewCreateRequest request = createReviewCreateRequest(book.getId(), 5, "Amazing book!");
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(bookRepository.findById(request.getBookId())).willReturn(Optional.of(book));
        given(reviewRepository.save(any(Review.class))).willReturn(review);

        // when
        Long reviewId = reviewService.createReview(user.getId(), request);

        // then
        assertThat(reviewId).isEqualTo(review.getId());
    }

    @Test
    @DisplayName("리뷰 등록 실패: 사용자가 존재하지 않음")
    void createReview_Fail_UserNotFound() {
        // given
        Long nonExistentUserId = 99L;
        ReviewCreateRequest request = createReviewCreateRequest(book.getId(), 5, "Amazing book!");
        given(userRepository.findById(nonExistentUserId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(nonExistentUserId, request))
                .isInstanceOf(CustomException.class)
                .extracting("detail") // detail 필드 검증
                .isEqualTo("존재하지 않는 사용자입니다.");
    }

    @Test
    @DisplayName("리뷰 등록 실패: 책이 존재하지 않음")
    void createReview_Fail_BookNotFound() {
        // given
        ReviewCreateRequest request = createReviewCreateRequest(99L, 5, "Amazing book!");
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(bookRepository.findById(request.getBookId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(user.getId(), request))
                .isInstanceOf(CustomException.class)
                .extracting("detail")
                .isEqualTo("존재하지 않는 책입니다.");
    }

    @Test
    @DisplayName("리뷰 단건 조회 성공")
    void getReview_Success() {
        // given
        Long reviewId = review.getId();
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        // when
        Review foundReview = reviewService.getReview(reviewId);

        // then
        assertThat(foundReview).isEqualTo(review);
    }

    @Test
    @DisplayName("리뷰 단건 조회 실패: 존재하지 않는 리뷰")
    void getReview_Fail_NotFound() {
        // given
        Long reviewId = 99L;
        given(reviewRepository.findById(reviewId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.getReview(reviewId))
                .isInstanceOf(CustomException.class)
                .extracting("detail")
                .isEqualTo("존재하지 않는 리뷰입니다.");
    }

    @Test
    @DisplayName("리뷰 단건 조회 실패: 삭제된 리뷰")
    void getReview_Fail_Deleted() {
        // given
        Long reviewId = review.getId();
        review.softDelete();
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        // when & then
        assertThatThrownBy(() -> reviewService.getReview(reviewId))
                .isInstanceOf(CustomException.class)
                .extracting("detail")
                .isEqualTo("삭제된 리뷰입니다."); // [주의] Service 코드의 메시지와 정확히 일치해야 함
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void updateReview_Success() {
        // given
        Long reviewId = review.getId();
        ReviewUpdateRequest request = createReviewUpdateRequest(4, "Good book!");
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        // when
        reviewService.updateReview(reviewId, request);

        // then
        assertThat(review.getRating()).isEqualTo(request.getRating());
        assertThat(review.getContent()).isEqualTo(request.getContent());
    }

    @Test
    @DisplayName("특정 책의 리뷰 목록 조회 성공")
    void getAllReviewsByBook_Success() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Review> reviews = List.of(review);
        Page<Review> reviewPage = new PageImpl<>(reviews, pageRequest, reviews.size());

        given(bookRepository.findById(book.getId())).willReturn(Optional.of(book));
        given(reviewRepository.findAllByBookAndDeletedAtIsNull(book, pageRequest)).willReturn(reviewPage);

        // when
        Page<Review> result = reviewService.getAllReviewsByBook(book.getId(), pageRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(review);
    }

    @Test
    @DisplayName("리뷰 Soft Delete 성공")
    void softDeleteReview_Success() {
        // given
        Long reviewId = review.getId();
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        // when
        reviewService.softDeleteReview(reviewId);

        // then
        assertThat(review.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("리뷰 Hard Delete 성공")
    void hardDeleteReview_Success() {
        // given
        Long reviewId = review.getId();
        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

        // when
        reviewService.hardDeleteReview(reviewId);

        // then
        verify(reviewRepository).delete(review);
    }


    @Test
    @DisplayName("특정 책의 리뷰 통계 조회 성공")
    void getBookStat_Success() {
        // given
        Long bookId = book.getId();
        // Repository에서 반환할 Mock 객체 생성
        BookStatResponse mockStat = new BookStatResponse(4.5, 10L);

        // findById 대신 existsById 사용하도록 변경됨
        given(bookRepository.existsById(bookId)).willReturn(true);
        given(reviewRepository.findStatByBookId(bookId)).willReturn(mockStat);

        // when
        BookStatResponse result = reviewService.getBookStat(bookId);

        // then
        assertThat(result.getAverageRating()).isEqualTo(4.5);
        assertThat(result.getReviewCount()).isEqualTo(10L);
    }

    @Test
    @DisplayName("특정 책의 리뷰 통계 조회 실패: 존재하지 않는 책")
    void getBookStat_Fail_BookNotFound() {
        // given
        Long nonExistentBookId = 99L;
        // existsById가 false 반환
        given(bookRepository.existsById(nonExistentBookId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> reviewService.getBookStat(nonExistentBookId))
                .isInstanceOf(CustomException.class)
                .extracting("detail")
                .isEqualTo("존재하지 않는 책입니다.");
    }




    private User createUserEntity() {
        return User.create("testuser", "password", "test@example.com", "Test User");
    }

    private Book createBookEntity() {
        return Book.create(
                "Test Book",
                "A book for testing.",
                "978-0-06-112008-4",
                new BigDecimal("19.99"),
                100
        );
    }

    private Review createReviewEntity(User user, Book book) {
        return Review.create(user, book, 5, "Excellent book!");
    }

    private ReviewCreateRequest createReviewCreateRequest(Long bookId, Integer rating, String content) {
        ReviewCreateRequest request = new ReviewCreateRequest();
        ReflectionTestUtils.setField(request, "bookId", bookId);
        ReflectionTestUtils.setField(request, "rating", rating);
        ReflectionTestUtils.setField(request, "content", content);
        return request;
    }

    private ReviewUpdateRequest createReviewUpdateRequest(Integer rating, String content) {
        ReviewUpdateRequest request = new ReviewUpdateRequest();
        ReflectionTestUtils.setField(request, "rating", rating);
        ReflectionTestUtils.setField(request, "content", content);
        return request;
    }
}