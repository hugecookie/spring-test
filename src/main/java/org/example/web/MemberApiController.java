package org.example.web;

import jakarta.validation.Valid;
import org.example.member.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Q23. @RestController = @Controller + @ResponseBody → 객체를 JSON 으로 반환
 * Q24. @RequestMapping(클래스) + @GetMapping/@PostMapping(메서드, HTTP 메서드 지정 축약형)
 *      시작 시 HandlerMapping(RequestMappingHandlerMapping)이 이 매핑 정보를 등록해 둔다.
 */
@RestController
@RequestMapping("/api/members")
public class MemberApiController {

    private final MemberService memberService;

    public MemberApiController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse create(@Valid @RequestBody CreateMemberRequest request) { // 입력 검증
        return MemberResponse.from(memberService.register(request.name()));         // 로직은 Service 에
    }

    @GetMapping("/{id}")
    public MemberResponse get(@PathVariable Long id) {
        return MemberResponse.from(memberService.findById(id));
    }
}
