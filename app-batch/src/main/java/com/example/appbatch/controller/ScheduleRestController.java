package com.example.appbatch.controller;

import com.example.appbatch.component.ClassLoaderUtil;
import com.example.appbatch.component.RemoteJobClassLoader;
import com.example.appbatch.quartz.QuartzJob;
import com.example.appbatch.quartz.QuartzSchedule;
import com.example.businessjob.batch.job.ExternalMemberSignupJobConfig;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.JobFactory;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Validated
@RestController
@RequestMapping("/batch/schedule")
@RequiredArgsConstructor
@Slf4j
public class ScheduleRestController {
    private final Scheduler scheduler;
    private final QuartzSchedule quartzSchedule;
    private final JobRegistry jobRegistry;

    // curl -X GET http://localhost:8081/batch/schedule/list -H 'Content-Type:application/json' -H 'Authorization: Bearer ' -d '{}'
    @GetMapping("/list")
    public ResponseEntity list() throws SchedulerException {
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), scheduler.getSchedulerName());

        // TODO 아래부분 객체지향적으로 변경해야함. (그리고 Cron설정값도 읽어서 표기해야함)
        List<Map<String, String>> schedulerList = new ArrayList<>();
        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                Map<String, String> triggerMap = new HashMap<>();
                String jobName = jobKey.getName();
                String jobGroup = jobKey.getGroup();
                triggerMap.put("jobName", jobName);
                triggerMap.put("jobGroup", jobGroup);
//                //get job's trigger
                List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                for(Trigger trigger : triggers) {
                    if(trigger instanceof CronTrigger) {
                        CronTrigger cronTrigger = (CronTrigger) trigger;
                        triggerMap.put("trigger", cronTrigger.toString());
                        triggerMap.put("cronExpression", cronTrigger.getCronExpression());
                    }

                }
                schedulerList.add(triggerMap);
            }
        }
        return ResponseEntity.ok(schedulerList);
    }

    // curl -X POST http://localhost:8081/batch/schedule/add -H 'Content-Type:application/json' -H 'Authorization: Bearer ' -d '{}'
    @PostMapping("/add")
    public void add() throws SchedulerException {
        //Job에 필요한 Parameter 생성
        Map paramsMap = new HashMap<>();
        //Job의 실행횟수 및 실행시간
        paramsMap.put("jobName", "externalMemberSignupJob");
        paramsMap.put("executeCount", 1);
        paramsMap.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        paramsMap.put("uuid", UUID.randomUUID().toString());

        //Job 생성 및 Scheduler에 등록
        quartzSchedule.addJob(QuartzJob.class, "externalMemberSignupJob", "TestGroup","externalMemberSignupJob 입니다", paramsMap, "0 0/1 * * * ?");
    }

    // curl -X POST http://localhost:8081/batch/schedule/edit -H 'Content-Type:application/json' -H 'Authorization: Bearer ' -d '{}'
    @PostMapping("/edit")
    public void edit() throws SchedulerException {
        //Job에 필요한 Parameter 생성
        Map paramsMap = new HashMap<>();
        //Job의 실행횟수 및 실행시간
        paramsMap.put("jobName", "memberSignupJob");
        paramsMap.put("executeCount", 1);
        paramsMap.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        paramsMap.put("uuid", UUID.randomUUID().toString());

        //Job 생성 및 Scheduler에 등록
        quartzSchedule.addJob(QuartzJob.class, "memberSignupJob", "TestGroup","memberSignupJob 입니다", paramsMap, "0 0/5 * * * ?");
    }

    // curl -X POST http://localhost:8081/batch/schedule/run -H 'Content-Type:application/json' -H 'Authorization: Bearer ' -d '{}'
    @PostMapping("/run")
    public void run() throws SchedulerException, NoSuchJobException {
        JobKey jobKey = new JobKey("memberSignupJob","TestGroup");
        scheduler.triggerJob(jobKey);
    }

    @PostMapping("/register")
    public void register() throws SchedulerException, NoSuchJobException {
        
    }

    // curl -X GET http://localhost:8081/batch/schedule/jobList -H 'Content-Type:application/json' -H 'Authorization: Bearer ' -d '{}'
    @GetMapping("/jobList")
    public ResponseEntity jobList() throws ClassNotFoundException {
        return ResponseEntity.ok(jobRegistry.getJobNames());
    }
}
