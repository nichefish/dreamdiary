package io.nicheblog.dreamdiary.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;

import java.util.concurrent.Executor;

/**
 * AsyncConfig
 *
 * @author nichefish
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 빈 생성
     * @return taskExecutor
     */
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("async-");
        executor.initialize();
        return new DelegatingSecurityContextExecutor(executor.getThreadPoolExecutor());
    }
}