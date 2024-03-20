package io.nicheblog.dreamdiary;

import io.nicheblog.NicheblogBasePackage;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * DreamdiaryApplication
 * <pre>
 *  Spring Boot Application Runner
 * </pre>
 *
 * @author nichefish
 */
@SpringBootApplication(scanBasePackageClasses = { NicheblogBasePackage.class })
@EnableCaching
@EnableScheduling
@Log4j2
public class DreamdiaryApplication {

    /**
     * main
     */
    public static void main(final String[] args) {
        SpringApplication.run(DreamdiaryApplication.class, args);
    }
}
