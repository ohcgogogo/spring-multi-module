package com.example.appbatch.quartz.config;

import com.example.appbatch.quartz.QuartzJobListener;
import com.example.appbatch.quartz.QuartzSchedule;
import com.example.appbatch.quartz.QuartzTriggerListener;
import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class QuartzConfig {
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(
            DataSource dataSource,
            QuartzProperties quartzProperties,
            PlatformTransactionManager transactionManager,
            ApplicationContext applicationContext,
            QuartzJobListener quartzJobListener,
            QuartzTriggerListener quartzTriggerListener
    ) {
        QuartzJobFactory quartzJobFactory = new QuartzJobFactory();
        quartzJobFactory.setApplicationContext(applicationContext);

        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        Properties properties = new Properties();
        properties.putAll(quartzProperties.getProperties());
        schedulerFactoryBean.setJobFactory(quartzJobFactory);
        schedulerFactoryBean.setApplicationContext(applicationContext);
        schedulerFactoryBean.setDataSource(dataSource);
        schedulerFactoryBean.setQuartzProperties(properties);
        schedulerFactoryBean.setTransactionManager(transactionManager);
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setGlobalJobListeners(quartzJobListener);
        schedulerFactoryBean.setGlobalTriggerListeners(quartzTriggerListener);
        return schedulerFactoryBean;
    }

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }

    @Bean(initMethod="init", destroyMethod="destroy")
    public QuartzSchedule quartzSchedule() {
        return new QuartzSchedule();
    }

    @Bean
    public SmartLifecycle gracefulShutdownHookForQuartz(SchedulerFactoryBean schedulerFactoryBean) {
        return new SmartLifecycle() {
            private boolean isRunning = false;
            private final Logger log = LoggerFactory.getLogger(this.getClass());

            @Override
            public boolean isAutoStartup() {
                return true;
            }

            @Override
            public void stop(Runnable callback) {
                stop();
                log.info("Spring container is shutting down.");
                callback.run();
            }

            @Override
            public void start() {
                log.info("Quartz Graceful Shutdown Hook started.");
                isRunning = true;
            }

            @Override
            public void stop() {
                isRunning = false;
                try {
                    log.info("Quartz Graceful Shutdown... ");
                    schedulerFactoryBean.destroy();
                } catch (SchedulerException e) {
                    try {
                        log.info(
                                "Error shutting down Quartz: " + e.getMessage(), e);
                        schedulerFactoryBean.getScheduler().shutdown(false);
                    } catch (SchedulerException ex) {
                        log.error("Unable to shutdown the Quartz scheduler.", ex);
                    }
                }
            }

            @Override
            public boolean isRunning() {
                return isRunning;
            }

            @Override
            public int getPhase() {
                // 시작할 때, 가장 낮은 단계의 객체들이 먼저 시작한다. 정지할 때는, 반대의 순서를 따른다.
                // 그러므로 Integer.MIN_VALUE를 반환하는 getPhase() 메소드를 가진 SmartLifecycle을 구현한 객체는 가장 먼저 시작하고 가장 나중에 정지할 것이다.
                // 그 반대로 Integer.MAX_VALUE 값을 가진 단계에서는 객체가 가장 늦게 시작하고 가장 빨리 멈춘다
                return Integer.MAX_VALUE;
            }
        };
    }

}
