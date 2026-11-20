package org.example.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

/** Q26. LoginCheckInterceptor 가 앞에서 검사한 뒤 들어오는 API */
@RestController
public class SecureApiController {

    @GetMapping("/api/secure/hello")
    public String hello(@RequestAttribute(LoginCheckInterceptor.LOGIN_USER) String loginUser) {
        return "hello " + loginUser;
    }
}
