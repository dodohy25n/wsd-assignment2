package hello.wsdassignment2.domain.review.entity;

import hello.wsdassignment2.common.entity.BaseEntity;
import hello.wsdassignment2.domain.book.entity.Book;
import hello.wsdassignment2.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Column(nullable = false)
    private Integer rating; // 별점 (1~5)

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private LocalDateTime deletedAt;

    @Builder
    public Review(User user, Book book, Integer rating, String content) {
        this.user = user;
        this.book = book;
        this.rating = rating;
        this.content = content;
    }

    public void updateInfo(Integer rating, String content) {
        this.rating = rating;
        this.content = content;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}