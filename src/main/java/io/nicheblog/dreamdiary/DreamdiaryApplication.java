package io.nicheblog.dreamdiary;

import io.nicheblog.NicheblogBasePackage;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * DreamdiaryApplication
 * <pre>
 *  Spring Boot Application Runner
 * </pre>
 * 테스트 의존성 주입 문제로 메인 클래스는 최대한 의존성 없이 유지하기
 *
 * @author nichefish
 */
@SpringBootApplication(scanBasePackageClasses = { NicheblogBasePackage.class })
@EnableScheduling
@EnableAsync
@Log4j2
public class DreamdiaryApplication {

    /**
     * main
     */
    public static void main(final String[] args) {
        SpringApplication.run(DreamdiaryApplication.class, args);
    }
}
