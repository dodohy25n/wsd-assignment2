package hello.wsdassignment2.domain.review.entity;

import hello.wsdassignment2.common.entity.BaseEntity;
import hello.wsdassignment2.domain.book.entity.Book;
import hello.wsdassignment2.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private int rating; // 1~5점

    @Builder
    public Review(User user, Book book, String content, int rating) {
        this.user = user;
        this.book = book;
        this.content = content;
        this.rating = rating;
    }

    // 리뷰 수정
    public void updateReview(String content, int rating) {
        this.content = content;
        this.rating = rating;
    }
}
