package org.example.flowday.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);       // 기본 스레드 풀 크기
        executor.setMaxPoolSize(50);        // 최대 스레드 풀 크기
        executor.setQueueCapacity(100);     // 대기 큐의 크기
        executor.setThreadNamePrefix("FlowDayExecutor-");       // 스레드 이름
        executor.initialize();
        return executor;
    }
}
