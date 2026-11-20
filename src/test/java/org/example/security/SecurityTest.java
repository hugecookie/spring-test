package org.example.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 8. Spring Security (Q37, Q38)
 * httpBasic() 은 실제 인증 흐름을 탄다:
 * BasicAuthenticationFilter → AuthenticationManager(ProviderManager) → DaoAuthenticationProvider
 * → UserDetailsService 조회 + PasswordEncoder 비교 → SecurityContextHolder 저장 → 인가 필터
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityTest {

    @Autowired MockMvc mockMvc;

    @Test
    @DisplayName("Q38. 인증 실패 → 401")
    void unauthenticated_401() throws Exception {
        mockMvc.perform(get("/me")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/me").with(httpBasic("user", "wrong"))).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Q38. 인증 성공 → SecurityContextHolder 에서 사용자 이름을 꺼낼 수 있다")
    void authenticated_200() throws Exception {
        mockMvc.perform(get("/me").with(httpBasic("user", "password")))
                .andExpect(status().isOk())
                .andExpect(content().string("user"));
    }

    @Test
    @DisplayName("Q38. 인증은 됐지만 권한 없음 → 403 / 권한 있음 → 200")
    void authorization() throws Exception {
        mockMvc.perform(get("/admin/hello").with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/admin/hello").with(httpBasic("admin", "password")))
                .andExpect(status().isOk());
    }
}
