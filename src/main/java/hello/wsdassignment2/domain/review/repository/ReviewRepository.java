package hello.wsdassignment2.domain.review.repository;

import hello.wsdassignment2.domain.book.entity.Book;
import hello.wsdassignment2.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBook(Book book); // 특정 책 리뷰 조회
}
