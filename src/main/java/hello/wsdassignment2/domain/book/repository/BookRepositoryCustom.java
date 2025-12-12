package hello.wsdassignment2.domain.book.repository;

import hello.wsdassignment2.domain.book.dto.BookSearchRequest;
import hello.wsdassignment2.domain.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookRepositoryCustom {
    Page<Book> searchBooks(BookSearchRequest request, Pageable pageable);
}
