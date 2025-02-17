package com.example.appbatch.quartz;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class QuartzJob extends QuartzJobBean {
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private JobLocator jobLocator;

    @Autowired
    private JobExplorer jobExplorer;

    static final String JOB_NAME = "jobName";

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            JobDataMap dataMap = context.getJobDetail().getJobDataMap();
            JobParameters jobParameters = getJobParametersFromJobMap(dataMap);
            Job job = jobLocator.getJob(dataMap.get(JOB_NAME).toString());
            JobParametersBuilder jobParametersBuilder = new JobParametersBuilder(jobExplorer);
            jobParametersBuilder.addJobParameters(jobParameters);
            jobParametersBuilder.getNextJobParameters(job);
            jobParametersBuilder.addString("scheduler", getSchedulerName(context));
            jobLauncher.run(job, jobParametersBuilder.toJobParameters());
        } catch (Exception e) {
            e.printStackTrace();
            throw new JobExecutionException(e);
        }
    }

    private String getSchedulerName(JobExecutionContext context) throws Exception {
        return context.getScheduler().getSchedulerName()
                +" "+context.getJobDetail().getKey().getGroup()
                +" "+context.getJobDetail().getKey().getName();
    }

    private JobParameters getJobParametersFromJobMap(Map<String, Object> jobDataMap) {
        JobParametersBuilder builder = new JobParametersBuilder();

        for (Map.Entry<String, Object> entry : jobDataMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String && !key.equals(JOB_NAME)) {
                String values = (String)value;
//                Format formatter;
//                Date date = new Date();
//                formatter = new SimpleDateFormat("yyMMdd");
//                String today = formatter.format(date);
//                values = values.replaceAll("#DATE#", today);
                builder.addString(key, values);
            } else if (value instanceof Float || value instanceof Double) {
                builder.addDouble(key, ((Number) value).doubleValue());
            } else if (value instanceof Integer || value instanceof Long) {
                builder.addLong(key, ((Number) value).longValue());
            } else if (value instanceof Date) {
                builder.addDate(key, (Date) value);
            } else {
                // JobDataMap contains values which are not job parameters
                // (ignoring)
            }
        }

        //need unique job parameter to rerun the same job
        builder.addDate("run date", new Date());
        return builder.toJobParameters();
    }
}


