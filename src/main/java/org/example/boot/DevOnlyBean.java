package org.example.boot;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** Q30. dev 프로필에서만 등록되는 빈 */
@Profile("dev")
@Component
public class DevOnlyBean {
}
