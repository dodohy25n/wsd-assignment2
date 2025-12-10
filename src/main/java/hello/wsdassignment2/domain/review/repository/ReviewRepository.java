package hello.wsdassignment2.domain.review.repository;

import hello.wsdassignment2.domain.book.entity.Book;
import hello.wsdassignment2.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findAllByDeletedAtIsNull(Pageable pageable);

    Page<Review> findAllByBookAndDeletedAtIsNull(Book book, Pageable pageable);
}