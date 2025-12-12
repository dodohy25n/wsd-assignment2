package hello.wsdassignment2.domain.book.service;

import hello.wsdassignment2.common.exception.CustomException;
import hello.wsdassignment2.domain.book.dto.BookCreateRequest;
import hello.wsdassignment2.domain.book.dto.BookSearchRequest;
import hello.wsdassignment2.domain.book.dto.BookUpdateRequest;
import hello.wsdassignment2.domain.book.entity.Book;
import hello.wsdassignment2.domain.book.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Test
    @DisplayName("책 등록 성공")
    void createBook_Success() {
        // given
        BookCreateRequest request = createBookRequest();
        Book savedBook = createBookEntity();
        ReflectionTestUtils.setField(savedBook, "id", 1L);

        given(bookRepository.existsByIsbn(request.getIsbn())).willReturn(false);
        given(bookRepository.save(any(Book.class))).willReturn(savedBook);

        // when
        Long bookId = bookService.createBook(request);

        // then
        assertThat(bookId).isEqualTo(1L);
    }

    @Test
    @DisplayName("책 등록 실패: ISBN 중복")
    void createBook_Fail_DuplicateIsbn() {
        // given
        BookCreateRequest request = createBookRequest();
        given(bookRepository.existsByIsbn(request.getIsbn())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> bookService.createBook(request))
                .isInstanceOf(CustomException.class)
                .extracting("detail")
                .isEqualTo("이미 등록된 ISBN 입니다.");
    }

    @Test
    @DisplayName("책 조회 성공")
    void getBook_Success() {
        // given
        Long bookId = 1L;
        Book book = createBookEntity();
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

        // when
        Book result = bookService.getBook(bookId);

        // then
        assertThat(result.getTitle()).isEqualTo(book.getTitle());
    }

    @Test
    @DisplayName("책 조회 실패: 존재하지 않음")
    void getBook_Fail_NotFound() {
        // given
        Long bookId = 99L;
        given(bookRepository.findById(bookId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookService.getBook(bookId))
                .isInstanceOf(CustomException.class)
                .extracting("detail")
                .isEqualTo("존재하지 않는 책입니다.");
    }

    @Test
    @DisplayName("책 목록 조회 성공: 검색 조건 없음 (전체 조회)")
    void getAllBooks_Norequest() {
        // given
        BookSearchRequest request = new BookSearchRequest(); // 빈 조건
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> books = List.of(createBookEntity());
        Page<Book> bookPage = new PageImpl<>(books);

        given(bookRepository.searchBooks(request, pageRequest)).willReturn(bookPage);

        // when
        Page<Book> result = bookService.getAllBooks(request, pageRequest);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(bookRepository, times(1)).searchBooks(request, pageRequest);
    }

    @Test
    @DisplayName("책 목록 조회 성공: 검색어(keyword) 포함")
    void getAllBooks_WithKeyword() {
        // given
        BookSearchRequest request = new BookSearchRequest();
        request.setKeyword("Test"); // 검색어 설정

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> books = List.of(createBookEntity());
        Page<Book> bookPage = new PageImpl<>(books);

        // Mock: 해당 조건으로 호출되면 결과를 반환하도록 설정
        given(bookRepository.searchBooks(request, pageRequest)).willReturn(bookPage);

        // when
        Page<Book> result = bookService.getAllBooks(request, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Title");
        verify(bookRepository).searchBooks(request, pageRequest);
    }

    @Test
    @DisplayName("책 목록 조회 성공: 가격 범위(minPrice, maxPrice) 포함")
    void getAllBooks_WithPriceRange() {
        // given
        BookSearchRequest request = new BookSearchRequest();
        request.setMinPrice(BigDecimal.valueOf(5000));
        request.setMaxPrice(BigDecimal.valueOf(15000));

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> books = List.of(createBookEntity());
        Page<Book> bookPage = new PageImpl<>(books);

        given(bookRepository.searchBooks(request, pageRequest)).willReturn(bookPage);

        // when
        Page<Book> result = bookService.getAllBooks(request, pageRequest);

        // then
        assertThat(result.getContent()).isNotEmpty();
        verify(bookRepository).searchBooks(request, pageRequest);
    }

    @Test
    @DisplayName("책 목록 조회 성공: 결과 없음 (Empty Page)")
    void getAllBooks_NoResult() {
        // given
        BookSearchRequest request = new BookSearchRequest();
        request.setKeyword("없는 책 제목"); // 결과가 없을 법한 검색어

        PageRequest pageRequest = PageRequest.of(0, 10);
        // 빈 페이지 반환 Mocking
        Page<Book> emptyPage = new PageImpl<>(Collections.emptyList());

        given(bookRepository.searchBooks(request, pageRequest)).willReturn(emptyPage);

        // when
        Page<Book> result = bookService.getAllBooks(request, pageRequest);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(bookRepository).searchBooks(request, pageRequest);
    }

    @Test
    @DisplayName("책 수정 성공")
    void updateBook_Success() {
        // given
        Long bookId = 1L;
        Book book = createBookEntity();
        BookUpdateRequest updateRequest = createUpdateRequest();

        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

        // when
        bookService.updateBook(bookId, updateRequest);

        // then
        assertThat(book.getTitle()).isEqualTo(updateRequest.getTitle());
        assertThat(book.getPrice()).isEqualTo(updateRequest.getPrice());
    }

    @Test
    @DisplayName("책 수정 실패: 존재하지 않음")
    void updateBook_Fail_NotFound() {
        // given
        Long bookId = 99L;
        BookUpdateRequest updateRequest = createUpdateRequest();
        given(bookRepository.findById(bookId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookService.updateBook(bookId, updateRequest))
                .isInstanceOf(CustomException.class)
                .extracting("detail")
                .isEqualTo("존재하지 않는 책입니다.");
    }

    @Test
    @DisplayName("Soft Delete 성공")
    void softDeleteBook_Success() {
        // given
        Long bookId = 1L;
        Book book = createBookEntity();
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

        // when
        bookService.softDeleteBook(bookId);

        // then
        assertThat(book.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("Soft Delete 실패: 존재하지 않음")
    void softDeleteBook_Fail_NotFound() {
        // given
        Long bookId = 99L;
        given(bookRepository.findById(bookId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookService.softDeleteBook(bookId))
                .isInstanceOf(CustomException.class)
                .extracting("detail")
                .isEqualTo("존재하지 않는 책입니다.");
    }

    @Test
    @DisplayName("Hard Delete 성공")
    void hardDeleteBook_Success() {
        // given
        Long bookId = 1L;
        Book book = createBookEntity();
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

        // when
        bookService.hardDeleteBook(bookId);

        // then
        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    @DisplayName("Hard Delete 실패: 존재하지 않음")
    void hardDeleteBook_Fail_NotFound() {
        // given
        Long bookId = 99L;
        given(bookRepository.findById(bookId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookService.hardDeleteBook(bookId))
                .isInstanceOf(CustomException.class)
                .extracting("detail")
                .isEqualTo("존재하지 않는 책입니다.");
    }

    private Book createBookEntity() {
        return Book.create(
                "Test Title",
                "Test Summary",
                "123-123-123",
                BigDecimal.valueOf(10000),
                100
        );
    }

    private BookCreateRequest createBookRequest() {
        BookCreateRequest req = new BookCreateRequest();
        ReflectionTestUtils.setField(req, "title", "New Book");
        ReflectionTestUtils.setField(req, "isbn", "123-123-123");
        ReflectionTestUtils.setField(req, "price", BigDecimal.valueOf(20000));
        ReflectionTestUtils.setField(req, "stockQuantity", 50);
        return req;
    }

    private BookUpdateRequest createUpdateRequest() {
        BookUpdateRequest req = new BookUpdateRequest();
        ReflectionTestUtils.setField(req, "title", "Updated");
        ReflectionTestUtils.setField(req, "price", BigDecimal.valueOf(15000));
        return req;
    }
}