package org.example.boot.autoconfig;

import org.example.boot.GreetingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Q29. 직접 만든 자동 설정.
 * META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports 에 등록되어 있고,
 *  - @ConditionalOnClass       : 클래스패스에 GreetingService 가 있을 때만
 *  - @ConditionalOnMissingBean : 개발자가 GreetingService 빈을 직접 등록하지 않았을 때만
 * 적용된다. 그래서 개발자가 등록한 설정이 항상 우선한다.
 */
@AutoConfiguration
@ConditionalOnClass(GreetingService.class)
public class GreetingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public GreetingService defaultGreetingService(@Value("${app.greeting:안녕하세요}") String greeting) {
        return name -> greeting + ", " + name;
    }
}
