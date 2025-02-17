package com.example.businessservice.config;

import com.example.businessservice.config.async.MDCCopyTaskDecorator;
import com.example.businessservice.config.async.exception.AsyncExceptionHandler;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    @Bean(name = "defaultExecutor")
    public Executor asyncThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setTaskDecorator(new MDCCopyTaskDecorator());// 이곳에서 복제가 이뤄집니다. (thread가 생성되는 case의 일부일 뿐으로 모든 thread가 생성되는 case에 동일한 조치가 되어야함)
        return taskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncExceptionHandler(); // 추가
    }
}
