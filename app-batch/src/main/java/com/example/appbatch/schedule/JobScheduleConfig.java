//package com.example.appbatch.schedule;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.core.*;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
//import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
//import org.springframework.batch.core.repository.JobRestartException;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.UUID;
//
//@Component
//@RequiredArgsConstructor
//public class JobScheduleConfig {
//    private final JobLauncher jobLauncher;
//    private final Job memberSignupJob;
//
//    @Scheduled(cron = "0 0/1 * * * *")
//    public void memberSignupStart() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
//        JobParameters parameters = new JobParametersBuilder().addString(
//            "UUID", UUID.randomUUID().toString()
//        ).toJobParameters();
//        JobExecution execution = jobLauncher.run(memberSignupJob, parameters);
//    }
//}
