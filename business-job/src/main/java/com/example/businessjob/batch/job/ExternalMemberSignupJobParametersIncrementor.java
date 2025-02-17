package com.example.businessjob.batch.job;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExternalMemberSignupJobParametersIncrementor implements JobParametersIncrementer {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    @Override
    public JobParameters getNext(JobParameters parameters) {
        LocalDateTime localDateTime =  LocalDateTime.now();
        String from = localDateTime.format(formatter);
        String to = localDateTime.minusDays(1).format(formatter);
        return new JobParametersBuilder()
                .addString("from", from)
                .addString("to", to)
                .toJobParameters();
    }
}
