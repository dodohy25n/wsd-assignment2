package hello.wsdassignment2.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.wsdassignment2.common.exception.CustomException;
import hello.wsdassignment2.common.exception.ErrorCode;
import hello.wsdassignment2.common.response.CommonResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error("Authentication Error: ", authException);

        Object exception = request.getAttribute("exception");
        if (exception instanceof CustomException) {
            handleCustomException((CustomException) exception, response, request.getRequestURI());
        } else {
            handleAuthenticationException(response, request.getRequestURI());
        }
    }

    private void handleCustomException(CustomException e, HttpServletResponse response, String requestURI) throws IOException {
        ErrorCode errorCode = e.getErrorCode();
        CommonResponse<Void> errorResponse = CommonResponse.error(errorCode.getCode(), errorCode.getMessage(), requestURI);

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }

    private void handleAuthenticationException(HttpServletResponse response, String requestURI) throws IOException {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        CommonResponse<Void> errorResponse = CommonResponse.error(errorCode.getCode(), errorCode.getMessage(), requestURI);

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
