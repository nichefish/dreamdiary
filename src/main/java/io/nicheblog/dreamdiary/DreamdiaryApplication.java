package io.nicheblog.dreamdiary;

import io.github.cdimascio.dotenv.Dotenv;
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
 *  Spring Boot Application Runner
 * </pre>
 * 테스트 의존성 주입 문제로 메인 클래스는 최대한 의존성 없이 유지하기
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
     * 애플리케이션 메인main 함수
     */
    public static void main(final String[] args) {
        // 프로필에 따른 .env 로드 :: 메소드 분리
        loadDotEnvProperties();
        // 시스템 구동
        SpringApplication.run(DreamdiaryApplication.class, args);
    }

    /**
     * 프로필에 따른 dotEnv 설정 :: 메소드 분리
     */
    private static void loadDotEnvProperties() {
        try {
            // 기본 .env 프로퍼티 로드
            setDotEnvPropertiesByFileNm(".env");
            // 프로필 기반 .env.${profile} 프로퍼티 로드
            String profile = System.getProperty("spring.profiles.active", "local");
            String profileEnvFileNm = ".env." + profile;
            setDotEnvPropertiesByFileNm(profileEnvFileNm);
        } catch (Exception e) {
            log.error("Failed to load .env file for profile: {}", System.getProperty("spring.profiles.active"), e);
        }
    }

    /**
     * 프로퍼티 파일 읽어서 시스템 환경 변수 추가 :: 메소드 분리
     */
    private static void setDotEnvPropertiesByFileNm(String fileName) {
        Dotenv dotenv = Dotenv.configure().filename(fileName).load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        log.info("Loaded {} file successfully.", fileName);
    }
}
