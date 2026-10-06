package io.nicheblog.dreamdiary.infrastructure.web.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * ValidationConfig
 * <pre>
 *  Bean Validation 메시지를 애플리케이션 메시지 번들(messages/messages)에서 해석하도록 연결한다.
 *  DTO 제약의 {@code message = "{journal.day.date.format}"} 같은 placeholder 가 messages_ko.properties 값으로 치환된다.
 *  Spring MVC 의 mvcValidator 는 컨텍스트의 단일 {@link javax.validation.Validator} 빈을 감싸 쓰므로
 *  (WebMvcAutoConfiguration.EnableWebMvcConfiguration#mvcValidator → ValidatorAdapter) {@code @Valid} 바인딩도 이 빈을 사용한다.
 *  변경 전: 존재하지 않는 {@code {msg.journal.day.*}} 키가 해석되지 않아 응답에 placeholder 원문이 노출됨.
 * </pre>
 *
 * @author nichefish
 */
@Configuration
public class ValidationConfig {

    /**
     * 메시지 번들을 쓰는 기본 Validator.
     * (BeanPostProcessor 초기화 순서 영향을 줄이기 위해 static 으로 선언한다.)
     *
     * @param messageSource 애플리케이션 메시지 소스
     * @return {@link LocalValidatorFactoryBean}
     */
    @Bean
    public static LocalValidatorFactoryBean validator(final MessageSource messageSource) {
        final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(messageSource);
        return validator;
    }
}
