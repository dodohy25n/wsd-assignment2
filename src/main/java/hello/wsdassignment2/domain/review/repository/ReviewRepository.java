package hello.wsdassignment2.domain.review.repository;

import hello.wsdassignment2.domain.book.dto.BookStatResponse;
import hello.wsdassignment2.domain.book.entity.Book;
import hello.wsdassignment2.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findAllByDeletedAtIsNull(Pageable pageable);

    Page<Review> findAllByBookAndDeletedAtIsNull(Book book, Pageable pageable);

    @Query("SELECT new hello.wsdassignment2.domain.book.dto.BookStatResponse(COALESCE(AVG(r.rating), 0), COUNT(r)) " +
            "FROM Review r WHERE r.book.id = :bookId AND r.deletedAt IS NULL")
    BookStatResponse findStatByBookId(@Param("bookId") Long bookId);}