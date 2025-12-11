package hello.wsdassignment2.domain.book.service;

import hello.wsdassignment2.common.exception.CustomException;
import hello.wsdassignment2.common.exception.ErrorCode;
import hello.wsdassignment2.domain.book.dto.BookCreateRequest;
import hello.wsdassignment2.domain.book.dto.BookUpdateRequest;
import hello.wsdassignment2.domain.book.entity.Book;
import hello.wsdassignment2.domain.book.repository.BookRepository;
import hello.wsdassignment2.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    // 책 등록
    @Transactional
    public Long createBook(BookCreateRequest request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE, "이미 등록된 ISBN 입니다.");
        }

        Book book = Book.create(
                request.getTitle(),
                request.getSummary(),
                request.getIsbn(),
                request.getPrice(),
                request.getStockQuantity()
        );

        return bookRepository.save(book).getId();
    }

    // 책 단건 조회 (삭제된 책 제외)
    public Book getBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 책입니다."));

        if (book.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "삭제된 책입니다.");
        }
        return book;
    }

    // 책 목록 조회
    public Page<Book> getAllBooks(Pageable pageable) {
        // deletedAt이 null인 데이터만 페이징 조회
        return bookRepository.findAllByDeletedAtIsNull(pageable);
    }

    // 책 정보 수정
    @Transactional
    public void updateBook(Long bookId, BookUpdateRequest request) {
        Book book = getBook(bookId); // 내부에서 deleted check 함
        book.updateInfo(request.getTitle(), request.getSummary(), request.getPrice(), request.getStockQuantity());
    }

    // Soft Delete
    @Transactional
    public void softDeleteBook(Long bookId) {
        Book book = getBook(bookId); // 내부에서 deleted check 함
        book.softDelete();
    }

    // Hard Delete
    @Transactional
    public void hardDeleteBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 책입니다."));
        bookRepository.delete(book);
    }
}