package org.example.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    /** Q38. 인증 성공 후 Authentication 은 SecurityContextHolder(ThreadLocal 기반)에 저장된다 */
    @GetMapping("/me")
    public String me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @GetMapping("/admin/hello")
    public String admin() {
        return "hello admin";
    }
}
