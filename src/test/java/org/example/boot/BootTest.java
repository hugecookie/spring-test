package org.example.boot;

import static org.assertj.core.api.Assertions.assertThat;

import org.example.boot.autoconfig.GreetingAutoConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

/** 6. Spring Boot (Q28 ~ Q31) */
class BootTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(GreetingAutoConfiguration.class));

    @Configuration
    static class UserGreetingConfig {
        @Bean
        GreetingService myGreetingService() {
            return name -> "custom " + name;
        }
    }

    @Test
    @DisplayName("Q29. 개발자가 빈을 등록하지 않으면 자동 설정 빈이 등록된다")
    void q29_autoConfigApplied() {
        runner.run(context -> {
            assertThat(context).hasSingleBean(GreetingService.class);
            assertThat(context).hasBean("defaultGreetingService");
        });
    }

    @Test
    @DisplayName("Q29. @ConditionalOnMissingBean: 개발자가 등록한 빈이 항상 우선")
    void q29_userBeanWins() {
        runner.withUserConfiguration(UserGreetingConfig.class).run(context -> {
            assertThat(context).hasSingleBean(GreetingService.class);
            assertThat(context).doesNotHaveBean("defaultGreetingService");
            assertThat(context.getBean(GreetingService.class).greet("kim")).isEqualTo("custom kim");
        });
    }

    @Test
    @DisplayName("Q29. @ConditionalOnClass: 클래스패스에 없으면 자동 설정이 적용되지 않는다")
    void q29_conditionalOnClass() {
        runner.withClassLoader(new FilteredClassLoader(GreetingService.class))
                .run(context -> assertThat(context).doesNotHaveBean("defaultGreetingService"));
    }

    @Nested
    @SpringBootTest
    class DefaultProfile {
        @Autowired ApplicationContext ac;
        @Autowired GreetingService greetingService;

        @Test
        @DisplayName("Q28, Q29. 실제 애플리케이션에서 imports 파일로 자동 설정이 적용된다")
        void defaultProfile() {
            assertThat(greetingService.greet("kim")).isEqualTo("안녕하세요, kim");
            assertThat(ac.getBeansOfType(DevOnlyBean.class)).isEmpty();
        }
    }

    @Nested
    @SpringBootTest
    @ActiveProfiles("dev")
    class DevProfile {
        @Autowired ApplicationContext ac;
        @Autowired GreetingService greetingService;

        @Test
        @DisplayName("Q30. dev 프로필: application-dev.yml 이 기본값을 덮어쓰고 @Profile(\"dev\") 빈이 등록된다")
        void devProfile() {
            assertThat(greetingService.greet("kim")).isEqualTo("[DEV] 안녕하세요, kim");
            assertThat(ac.getBean(DevOnlyBean.class)).isNotNull();
        }
    }
}
