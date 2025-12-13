package hello.wsdassignment2.domain.user.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.wsdassignment2.domain.admin.dto.UserSearchRequest;
import hello.wsdassignment2.domain.user.entity.Role;
import hello.wsdassignment2.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static hello.wsdassignment2.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<User> searchUsers(UserSearchRequest request, Pageable pageable) {
        List<User> content = queryFactory
                .selectFrom(user)
                .where(
                        notDeleted(),
                        keywordContains(request.getKeyword()),
                        roleEq(request.getRole())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(user.createdAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(user.count())
                .from(user)
                .where(
                        notDeleted(),
                        keywordContains(request.getKeyword()),
                        roleEq(request.getRole())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression keywordContains(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        return user.username.containsIgnoreCase(keyword)
                .or(user.email.containsIgnoreCase(keyword));
    }

    private BooleanExpression roleEq(String role) {
        if (!StringUtils.hasText(role)) {
            return null;
        }
        try {
            return user.role.eq(Role.valueOf(role.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private BooleanExpression notDeleted() {
        return user.deletedAt.isNull();
    }
}
