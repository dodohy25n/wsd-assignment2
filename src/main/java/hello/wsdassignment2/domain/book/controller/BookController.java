package hello.wsdassignment2.domain.book.controller;

import hello.wsdassignment2.common.response.CommonResponse;
import hello.wsdassignment2.common.response.ErrorResponse;
import hello.wsdassignment2.common.response.PagedResponse;
import hello.wsdassignment2.domain.book.dto.*;
import hello.wsdassignment2.domain.book.entity.Book;
import hello.wsdassignment2.domain.book.service.BookService;
import hello.wsdassignment2.domain.review.dto.ReviewResponse;
import hello.wsdassignment2.domain.review.entity.Review;
import hello.wsdassignment2.domain.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Book", description = "책 관련 API")
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

        private final BookService bookService;
        private final ReviewService reviewService;

        @Operation(summary = "책 등록 ", description = "새로운 책을 등록합니다. ")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "책 등록 성공", useReturnTypeSchema = true),
                        @ApiResponse(responseCode = "400", description = "입력값 유효성 검사 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "접근 권한 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @PostMapping
        public ResponseEntity<CommonResponse<Long>> createBook(
                        @Parameter(description = "책 생성 요청 정보", required = true) @Valid @RequestBody BookCreateRequest request) {
                Long bookId = bookService.createBook(request);
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(CommonResponse.success(bookId));
        }

        @Operation(summary = "책 단건 조회", description = "ID로 특정 책을 조회합니다.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "책 조회 성공", useReturnTypeSchema = true),
                        @ApiResponse(responseCode = "404", description = "존재하지 않는 책", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @GetMapping("/{bookId}")
        public ResponseEntity<CommonResponse<BookResponse>> getBook(
                        @Parameter(description = "조회할 책 ID", required = true) @PathVariable Long bookId) {
                Book book = bookService.getBook(bookId);
                return ResponseEntity.ok(CommonResponse.success(BookResponse.from(book)));
        }

        @Operation(summary = "책 목록 조회 (검색/필터)", description = "페이지네이션과 검색 조건을 사용하여 책 목록을 조회합니다.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "책 목록 조회 성공", useReturnTypeSchema = true)
        })
        @GetMapping
        public ResponseEntity<PagedResponse<BookResponse>> getAllBooks(
                        // @ModelAttribute로 쿼리 파라미터를 DTO에 바인딩
                        @ParameterObject @ModelAttribute BookSearchRequest request,
                        @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
                Page<Book> bookPage = bookService.getAllBooks(request, pageable);
                Page<BookResponse> responsePage = bookPage.map(BookResponse::from);

                return ResponseEntity.ok(PagedResponse.success(responsePage));
        }

        @Operation(summary = "책 수정 ", description = "기존 책 정보를 수정합니다. ")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "책 수정 성공", useReturnTypeSchema = true),
                        @ApiResponse(responseCode = "400", description = "입력값 유효성 검사 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "접근 권한 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "존재하지 않는 책", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @PutMapping("/{bookId}")
        public ResponseEntity<CommonResponse<Void>> updateBook(
                        @Parameter(description = "수정할 책 ID", required = true) @PathVariable Long bookId,
                        @Parameter(description = "책 수정 요청 정보", required = true) @Valid @RequestBody BookUpdateRequest request) {
                bookService.updateBook(bookId, request);
                return ResponseEntity.ok(CommonResponse.success(null));
        }

        @Operation(summary = "책 Soft Delete ", description = "책을 논리적으로 삭제합니다. ")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "책 논리적 삭제 성공", useReturnTypeSchema = true),
                        @ApiResponse(responseCode = "403", description = "접근 권한 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "존재하지 않는 책", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @DeleteMapping("/{bookId}")
        public ResponseEntity<CommonResponse<Void>> softDeleteBook(
                        @Parameter(description = "삭제할 책 ID", required = true) @PathVariable Long bookId) {
                bookService.softDeleteBook(bookId);
                return ResponseEntity.ok(CommonResponse.success(null));
        }

        @Operation(summary = "책 Hard Delete ", description = "책을 물리적으로 삭제합니다. ")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "책 물리적 삭제 성공", useReturnTypeSchema = true),
                        @ApiResponse(responseCode = "403", description = "접근 권한 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "존재하지 않는 책", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @DeleteMapping("/{bookId}/hard")
        public ResponseEntity<CommonResponse<Void>> hardDeleteBook(
                        @Parameter(description = "완전 삭제할 책 ID", required = true) @PathVariable Long bookId) {
                bookService.hardDeleteBook(bookId);
                return ResponseEntity.ok(CommonResponse.success(null));
        }

        @Operation(summary = "특정 책의 리뷰 목록 조회", description = "특정 책에 달린 모든 리뷰 목록을 조회합니다.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "리뷰 목록 조회 성공", useReturnTypeSchema = true),
                        @ApiResponse(responseCode = "404", description = "존재하지 않는 책", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @GetMapping("/{bookId}/reviews")
        public ResponseEntity<PagedResponse<ReviewResponse>> getAllReviewsByBook(
                        @Parameter(description = "리뷰를 조회할 책 ID", required = true) @PathVariable Long bookId,
                        @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
                Page<Review> reviewPage = reviewService.getAllReviewsByBook(bookId, pageable);
                Page<ReviewResponse> responsePage = reviewPage.map(ReviewResponse::from);

                return ResponseEntity.ok(PagedResponse.success(responsePage));
        }

        @Operation(summary = "특정 책의 리뷰 통계 조회", description = "특정 책의 평균 평점과 리뷰 개수를 조회합니다.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true),
                        @ApiResponse(responseCode = "404", description = "존재하지 않는 책", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
        })
        @GetMapping("/{bookId}/stats")
        public ResponseEntity<CommonResponse<BookStatResponse>> getBookStats(
                        @Parameter(description = "책 ID", required = true) @PathVariable Long bookId) {

                BookStatResponse stats = reviewService.getBookStat(bookId);

                return ResponseEntity.ok(CommonResponse.success(stats));
        }
}