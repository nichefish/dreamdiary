package io.nicheblog.dreamdiary;

import io.nicheblog.NicheblogBasePackage;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * DreamdiaryApplication
 * <pre>
 *  Spring Boot Application Runner.
 *  (!테스트 의존성 주입 문제로, 메인 클래스는 최대한 간결하게 유지하는 것이 좋다.)
 * </pre>
 *
 * @author nichefish
 */
@SpringBootApplication(scanBasePackageClasses = { NicheblogBasePackage.class })
@EnableCaching
@EnableScheduling
@EnableAsync
@Log4j2
public class DreamdiaryApplication {

    /**
     * 애플리케이션의 진입점인 메인(main) 함수입니다.
     * @param args - 명령줄에서 전달된 인수
     */
    public static void main(final String[] args) {
        // 시스템 구동
        SpringApplication.run(DreamdiaryApplication.class, args);
    }

}
