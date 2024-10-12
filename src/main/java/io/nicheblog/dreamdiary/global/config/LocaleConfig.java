package io.nicheblog.dreamdiary.global.config;

import io.nicheblog.dreamdiary.global.Constant;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * LocaleConfig
 * <pre>
 *  LocalDateTime을 서울로 지정함.
 * </pre>
 *
 * @author nichefish
 */
@Configuration
public class LocaleConfig {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone(Constant.LOC_SEOUL));
    }
}
