package hello.wsdassignment2.domain.book.controller;

import hello.wsdassignment2.common.response.ApiResponse;
import hello.wsdassignment2.domain.book.dto.BookCreateRequest;
import hello.wsdassignment2.domain.book.dto.BookResponse;
import hello.wsdassignment2.domain.book.dto.BookUpdateRequest;
import hello.wsdassignment2.domain.book.entity.Book;
import hello.wsdassignment2.domain.book.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // 책 등록
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createBook(@Valid @RequestBody BookCreateRequest request) {
        Long bookId = bookService.createBook(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(bookId));
    }

    // 책 단건 조회
    @GetMapping("/{bookId}")
    public ResponseEntity<ApiResponse<BookResponse>> getBook(@PathVariable Long bookId) {
        Book book = bookService.getBook(bookId);
        return ResponseEntity.ok(ApiResponse.success(BookResponse.from(book)));
    }

    // 책 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAllBooks(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Book> bookPage = bookService.getAllBooks(pageable);
        Page<BookResponse> responsePage = bookPage.map(BookResponse::from);

        return ResponseEntity.ok(ApiResponse.successPage(responsePage));
    }

    // 책 수정
    @PutMapping("/{bookId}")
    public ResponseEntity<ApiResponse<Void>> updateBook(@PathVariable Long bookId,
                                                        @Valid @RequestBody BookUpdateRequest request) {
        bookService.updateBook(bookId, request);
        // 이제 null을 넣어도 success(T data)로 정확히 인식됨
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 책 Soft Delete
    @DeleteMapping("/{bookId}")
    public ResponseEntity<ApiResponse<Void>> softDeleteBook(@PathVariable Long bookId) {
        bookService.softDeleteBook(bookId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 책 Hard Delete
    @DeleteMapping("/{bookId}/hard")
    public ResponseEntity<ApiResponse<Void>> hardDeleteBook(@PathVariable Long bookId) {
        bookService.hardDeleteBook(bookId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}