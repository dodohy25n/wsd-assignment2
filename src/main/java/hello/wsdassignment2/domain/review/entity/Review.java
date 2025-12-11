package hello.wsdassignment2.domain.review.entity;

import hello.wsdassignment2.common.entity.BaseEntity;
import hello.wsdassignment2.domain.book.entity.Book;
import hello.wsdassignment2.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(nullable = false)
    private Integer rating; // 별점 (1~5)

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private LocalDateTime deletedAt;

    public static Review create(User user, Book book, Integer rating, String content) {
        return Review.builder()
                .user(user)
                .book(book)
                .rating(rating)
                .content(content)
                .build();
    }

    public void updateInfo(Integer rating, String content) {
        this.rating = rating;
        this.content = content;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}