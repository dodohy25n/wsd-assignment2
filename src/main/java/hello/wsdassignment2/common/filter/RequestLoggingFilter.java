package hello.wsdassignment2.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();

        // 요청 처리 (컨트롤러 실행)
        try {
            filterChain.doFilter(request, response);
        } finally {
            // 응답 처리 후 로그 남기기
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            String method = request.getMethod();
            String uri = request.getRequestURI();
            String query = (request.getQueryString() != null) ? "?" + request.getQueryString() : "";
            int status = response.getStatus();

            // [로그 포맷] METHOD URI?Query | STATUS | DURATIONms
            // 예: GET /api/books?page=0 | 200 | 15ms
            // 예: POST /api/auth/login | 401 | 8ms

            if (status >= 500) {
                log.error("{} {}{} | {} | {}ms", method, uri, query, status, duration);
            } else if (status >= 400) {
                log.warn("{} {}{} | {} | {}ms", method, uri, query, status, duration);
            } else {
                log.info("{} {}{} | {} | {}ms", method, uri, query, status, duration);
            }
        }
    }
}