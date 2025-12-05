package hello.wsdassignment2.domain.book.repository;

import hello.wsdassignment2.domain.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn);

    Page<Book> findAllByDeletedAtIsNull(Pageable pageable);
}
