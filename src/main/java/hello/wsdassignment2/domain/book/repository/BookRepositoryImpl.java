package hello.wsdassignment2.domain.book.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.wsdassignment2.domain.book.dto.BookSearchRequest;
import hello.wsdassignment2.domain.book.entity.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

import static hello.wsdassignment2.domain.book.entity.QBook.book;
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Book> searchBooks(BookSearchRequest request, Pageable pageable) {

        // 1. 컨텐츠 조회 쿼리
        List<Book> content = queryFactory
                .selectFrom(book)
                .where(
                        isNotDeleted(), // Soft Delete 제외
                        keywordContains(request.getKeyword()), // 키워드 검색
                        priceBetween(request.getMinPrice(), request.getMaxPrice()) // 가격 범위
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(book.createdAt.desc()) // 기본 정렬 (필요 시 Pageable의 sort 적용 로직 추가 가능)
                .fetch();

        // 2. 카운트 쿼리 (최적화: 컨텐츠가 없으면 실행 안 함 등의 로직을 PageableExecutionUtils가 처리)
        JPAQuery<Long> countQuery = queryFactory
                .select(book.count())
                .from(book)
                .where(
                        isNotDeleted(),
                        keywordContains(request.getKeyword()),
                        priceBetween(request.getMinPrice(), request.getMaxPrice())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // --- 동적 쿼리 조건 메서드들 (BooleanExpression) ---

    private BooleanExpression isNotDeleted() {
        return book.deletedAt.isNull();
    }

    private BooleanExpression keywordContains(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        // 제목 OR 줄거리 OR ISBN 에 포함되는지 검사
        return book.title.containsIgnoreCase(keyword)
                .or(book.summary.containsIgnoreCase(keyword))
                .or(book.isbn.containsIgnoreCase(keyword));
    }

    private BooleanExpression priceBetween(BigDecimal min, BigDecimal max) {
        if (min == null && max == null) return null;

        if (min != null && max != null) {
            return book.price.between(min, max);
        }
        if (min != null) {
            return book.price.goe(min); // greater or equal
        }
        return book.price.loe(max); // less or equal
    }
}