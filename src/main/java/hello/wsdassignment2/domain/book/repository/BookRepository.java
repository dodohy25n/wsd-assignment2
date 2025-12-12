package hello.wsdassignment2.domain.book.repository;

import hello.wsdassignment2.domain.book.entity.Book;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom {    boolean existsByIsbn(String isbn);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Book b where b.id in :ids")
    List<Book> findAllByIdForUpdate(@Param("ids") Set<Long> ids);

}
