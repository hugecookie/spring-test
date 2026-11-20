package org.example.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Q39. JWT 의 핵심 원리만 JDK 로 흉내 낸 학습용 토큰 (실무에서는 jjwt, spring-boot-starter-oauth2-resource-server 사용)
 *  토큰 = base64(payload) + "." + HMAC 서명
 *  - 서버는 상태를 저장하지 않고 "서명 검증 + 만료 확인"만 한다 → 확장에 유리
 *  - 대신 한 번 발급하면 만료 전까지 서버가 무효화하기 어렵다 → Access 는 짧게, Refresh 는 서버에 저장
 */
public class SimpleTokenProvider {

    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    private final byte[] secret;
    private final Clock clock;

    public SimpleTokenProvider(String secret, Clock clock) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.clock = clock;
    }

    public String create(String username, Duration ttl) {
        long expiresAt = clock.instant().plus(ttl).getEpochSecond();
        String payload = ENCODER.encodeToString((username + ":" + expiresAt).getBytes(StandardCharsets.UTF_8));
        return payload + "." + ENCODER.encodeToString(sign(payload));
    }

    /** 유효하면 username, 위조·만료면 empty */
    public Optional<String> validate(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 2) {
            return Optional.empty();
        }
        byte[] expected = sign(parts[0]);
        byte[] actual;
        try {
            actual = DECODER.decode(parts[1]);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
        if (!MessageDigest.isEqual(expected, actual)) { // 위조 검사 (상수 시간 비교)
            return Optional.empty();
        }
        String payload = new String(DECODER.decode(parts[0]), StandardCharsets.UTF_8);
        int idx = payload.lastIndexOf(':');
        long expiresAt = Long.parseLong(payload.substring(idx + 1));
        if (clock.instant().getEpochSecond() > expiresAt) { // 만료 검사
            return Optional.empty();
        }
        return Optional.of(payload.substring(0, idx));
    }

    private byte[] sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
