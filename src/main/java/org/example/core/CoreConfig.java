package org.example.core;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Q5. @Bean: 소스를 고칠 수 없는 외부 라이브러리 클래스(java.time.Clock)나
 * 생성 과정을 직접 제어해야 할 때 @Configuration 안에서 수동 등록한다.
 */
@Configuration
public class CoreConfig {

    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("Asia/Seoul"));
    }
}
