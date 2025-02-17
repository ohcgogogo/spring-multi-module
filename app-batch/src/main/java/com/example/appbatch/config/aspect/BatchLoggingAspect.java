package com.example.appbatch.config.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.batch.core.Job;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class BatchLoggingAspect {
    public static final String BATCH_MDC_KEY = "batch";

    // 스프링 배치의 JobLauncher.run(..) 메소드 실행 시 아래의 코드가 수행됩니다.
    @Around("execution(* org.springframework.batch.core.launch.JobLauncher.run(..))")
    public Object putMDC(ProceedingJoinPoint joinPoint) throws Throwable {
        final Object[] args = joinPoint.getArgs();
        final Job job = (Job) args[0]; // 메소드의 첫 번째 인자는 Job의 인스턴스입니다.
        MDC.put(BATCH_MDC_KEY, job.getName());
        log.info("jobInfo : {}", job.getName());
        return joinPoint.proceed();
    }
}
