package org.example.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 5. Spring MVC (Q23 ~ Q27)
 * Q25. 요청 흐름: Filter → DispatcherServlet → HandlerMapping → HandlerAdapter(+Interceptor.preHandle, ArgumentResolver)
 *      → Controller → HttpMessageConverter(JSON) → 응답
 */
@SpringBootTest
@AutoConfigureMockMvc
class WebMvcTest {

    @Autowired MockMvc mockMvc;

    @Test
    @DisplayName("Q23~Q25. @RestController 가 JSON 을 받고 JSON 을 돌려준다 (HttpMessageConverter)")
    void q23_restController() throws Exception {
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"kim\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("kim"));
    }

    @Test
    @DisplayName("Q24. @GetMapping 은 GET 만 매핑한다 → 다른 메서드는 405")
    void q24_requestMapping() throws Exception {
        mockMvc.perform(post("/api/members/1"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("Q26. Filter 는 /api/* 요청에 응답 헤더를 단다 (DispatcherServlet 앞)")
    void q26_filter() throws Exception {
        mockMvc.perform(get("/api/secure/hello").header("X-USER", "kim"))
                .andExpect(header().string("X-Filter", "passed"))
                .andExpect(header().string("X-Handler", "hello")) // Interceptor 는 핸들러 정보를 안다
                .andExpect(content().string("hello kim"));
    }

    @Test
    @DisplayName("Q26, Q27. Interceptor 에서 던진 예외도 @RestControllerAdvice 가 공통 형식으로 처리")
    void q26_interceptorException() throws Exception {
        mockMvc.perform(get("/api/secure/hello"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("A001"));
    }

    @Test
    @DisplayName("Q27. 비즈니스 예외 → 404 + 공통 에러 응답")
    void q27_businessException() throws Exception {
        mockMvc.perform(get("/api/members/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("M001"));
    }

    @Test
    @DisplayName("Q27. 검증 실패(@Valid) → 400 + 공통 에러 응답")
    void q27_validation() throws Exception {
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("C001"));
    }
}
