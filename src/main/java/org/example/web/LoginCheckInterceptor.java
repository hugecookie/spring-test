package org.example.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.common.BusinessException;
import org.example.common.ErrorCode;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Q26. Interceptor: Spring MVC 기능. DispatcherServlet 과 컨트롤러 "사이"에서 동작한다.
 * handler(어떤 컨트롤러 메서드가 호출될지)를 알 수 있고, 여기서 던진 예외는 @ControllerAdvice 가 처리한다.
 */
public class LoginCheckInterceptor implements HandlerInterceptor {

    public static final String LOGIN_USER = "loginUser";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String user = request.getHeader("X-USER");
        if (user == null || user.isBlank()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        if (handler instanceof HandlerMethod hm) {
            response.setHeader("X-Handler", hm.getMethod().getName()); // 핸들러 정보 접근 가능
        }
        request.setAttribute(LOGIN_USER, user);
        return true;
    }
}
