package org.example.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** 8. Q39. 토큰(JWT) 방식의 특징 */
class SimpleTokenProviderTest {

    private static final String SECRET = "my-secret-key";
    private final Instant now = Instant.parse("2026-01-01T00:00:00Z");
    private final SimpleTokenProvider provider =
            new SimpleTokenProvider(SECRET, Clock.fixed(now, ZoneOffset.UTC));

    @Test
    @DisplayName("서버는 아무것도 저장하지 않고 서명만 검증해 사용자를 알아낸다 (stateless)")
    void validToken() {
        String token = provider.create("kim", Duration.ofMinutes(30));
        assertThat(provider.validate(token)).contains("kim");
    }

    @Test
    @DisplayName("내용을 바꾸면 서명이 맞지 않아 거부된다")
    void tamperedToken() {
        String token = provider.create("kim", Duration.ofMinutes(30));
        String forgedPayload = java.util.Base64.getUrlEncoder().withoutPadding()
                .encodeToString("admin:9999999999".getBytes());
        String forged = forgedPayload + token.substring(token.indexOf('.'));
        assertThat(provider.validate(forged)).isEmpty();
    }

    @Test
    @DisplayName("만료 후에는 거부된다. 반대로 만료 전에는 서버가 무효화할 방법이 없다 → Access Token 은 짧게")
    void expiredToken() {
        String token = provider.create("kim", Duration.ofMinutes(30));
        SimpleTokenProvider later = new SimpleTokenProvider(SECRET,
                Clock.fixed(now.plus(Duration.ofMinutes(31)), ZoneOffset.UTC));
        assertThat(later.validate(token)).isEmpty();
    }
}
