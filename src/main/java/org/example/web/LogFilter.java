package org.example.web;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Q26. Filter: 서블릿 스펙. DispatcherServlet "앞"에서 동작한다.
 * 요청/응답 객체 자체를 다룰 수 있어 인코딩·CORS·보안(Spring Security 도 필터)에 쓴다.
 * 여기서 던진 예외는 @ControllerAdvice 가 잡지 못한다.
 */
public class LogFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        ((HttpServletResponse) response).setHeader("X-Filter", "passed");
        chain.doFilter(request, response); // 다음 필터 → ... → DispatcherServlet
    }

    @Override
    public void destroy() {

    }
}
