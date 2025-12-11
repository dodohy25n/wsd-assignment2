package hello.wsdassignment2.domain.book.controller;

import hello.wsdassignment2.common.response.ApiResponse;
import hello.wsdassignment2.domain.book.dto.BookCreateRequest;
import hello.wsdassignment2.domain.book.dto.BookResponse;
import hello.wsdassignment2.domain.book.dto.BookUpdateRequest;
import hello.wsdassignment2.domain.book.entity.Book;
import hello.wsdassignment2.domain.book.service.BookService;
import hello.wsdassignment2.domain.review.dto.ReviewResponse;
import hello.wsdassignment2.domain.review.entity.Review;
import hello.wsdassignment2.domain.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Book", description = "책 관련 API")
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final ReviewService reviewService;

    @Operation(summary = "책 등록", description = "새로운 책을 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createBook(
            @Parameter(description = "책 생성 요청 정보", required = true) @Valid @RequestBody BookCreateRequest request) {
        Long bookId = bookService.createBook(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(bookId));
    }

    @Operation(summary = "책 단건 조회", description = "ID로 특정 책을 조회합니다.")
    @GetMapping("/{bookId}")
    public ResponseEntity<ApiResponse<BookResponse>> getBook(
            @Parameter(description = "조회할 책 ID", required = true) @PathVariable Long bookId) {
        Book book = bookService.getBook(bookId);
        return ResponseEntity.ok(ApiResponse.success(BookResponse.from(book)));
    }

    @Operation(summary = "책 목록 조회", description = "페이지네이션을 사용하여 모든 책 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAllBooks(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Book> bookPage = bookService.getAllBooks(pageable);
        Page<BookResponse> responsePage = bookPage.map(BookResponse::from);

        return ResponseEntity.ok(ApiResponse.successPage(responsePage));
    }

    @Operation(summary = "책 수정", description = "기존 책 정보를 수정합니다.")
    @PutMapping("/{bookId}")
    public ResponseEntity<ApiResponse<Void>> updateBook(
            @Parameter(description = "수정할 책 ID", required = true) @PathVariable Long bookId,
            @Parameter(description = "책 수정 요청 정보", required = true) @Valid @RequestBody BookUpdateRequest request) {
        bookService.updateBook(bookId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "책 Soft Delete", description = "책을 논리적으로 삭제합니다.")
    @DeleteMapping("/{bookId}")
    public ResponseEntity<ApiResponse<Void>> softDeleteBook(
            @Parameter(description = "삭제할 책 ID", required = true) @PathVariable Long bookId) {
        bookService.softDeleteBook(bookId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "책 Hard Delete", description = "책을 물리적으로 삭제합니다.")
    @DeleteMapping("/{bookId}/hard")
    public ResponseEntity<ApiResponse<Void>> hardDeleteBook(
            @Parameter(description = "완전 삭제할 책 ID", required = true) @PathVariable Long bookId) {
        bookService.hardDeleteBook(bookId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "특정 책의 리뷰 목록 조회", description = "특정 책에 달린 모든 리뷰 목록을 조회합니다.")
    @GetMapping("/{bookId}/reviews")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getAllReviewsByBook(
            @Parameter(description = "리뷰를 조회할 책 ID", required = true) @PathVariable Long bookId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Review> reviewPage = reviewService.getAllReviewsByBook(bookId, pageable);
        Page<ReviewResponse> responsePage = reviewPage.map(ReviewResponse::from);
        return ResponseEntity.ok(ApiResponse.successPage(responsePage));
    }

}