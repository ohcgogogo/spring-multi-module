package com.example.appbatch.quartz;

import com.example.appbatch.component.RemoteJobClassLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
public class QuartzSchedule {
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    private Scheduler scheduler;


    public void init() throws Exception {
        try {
            scheduler = schedulerFactoryBean.getScheduler();

//            //스케줄러 초기화 -> DB도 CLAER
            scheduler.clear();
//            //Job 리스너 등록
//            scheduler.getListenerManager().addJobListener(new AddJobListner());
//            //Trigger 리스너 등록
//            scheduler.getListenerManager().addTriggerListener(new AddTriggerListener());


            //Job에 필요한 Parameter 생성
            Map paramsMap = new HashMap<>();
            //Job의 실행횟수 및 실행시간
            paramsMap.put("jobName", "memberSignupJob");
            paramsMap.put("executeCount", 1);
            paramsMap.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            paramsMap.put("uuid", UUID.randomUUID().toString());

            //Job 생성 및 Scheduler에 등록
            addJob(QuartzJob.class, "memberSignupJob", "TestGroup","memberSignupJob 입니다", paramsMap, "0 0/1 * * * ?");

            scheduler.start();
        } catch (Exception e){
            log.error("addJob error  : {}", e);
        }
    }

    public void restart() {
        try {
            scheduler.standby();
            scheduler.clear();
//            List<SchdulVO> schdulList = getSchdulList();
//            registScheduleForScheduler(schdulList);
            scheduler.start();
        } catch (Exception e) {
            log.error("QuartzSchedule in restart() {}", e.getMessage());
        }
    }

    public void destroy() {
        try {
            if (scheduler != null) {
                scheduler.shutdown();
//                schedulerFactoryBean.destroy();
            }
        } catch(Exception e) {
            log.error("QuartzSchedule in destroy() {}", e.getMessage());
        }
    }

    //Job 추가
    public <T extends Job> void addJob(Class<? extends Job> job ,String id, String groupName, String  dsec, Map paramsMap, String cron) throws SchedulerException {
        JobDetail jobDetail = buildJobDetail(job,id,groupName,dsec,paramsMap);
        String triggerId = id+"Trigger";
        Trigger trigger = buildCronTrigger(triggerId, groupName, cron);
        if(scheduler.checkExists(jobDetail.getKey())) scheduler.deleteJob(jobDetail.getKey());
        scheduler.scheduleJob(jobDetail,trigger);
    }

    //JobDetail 생성
    public <T extends Job> JobDetail buildJobDetail(Class<? extends Job> job, String id, String groupName, String desc, Map paramsMap) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.putAll(paramsMap);

        return JobBuilder
                .newJob(job)
                .withIdentity(id, groupName)
                .withDescription(desc)
                .usingJobData(jobDataMap)
//                .storeDurably()
                .build();
    }

    //Trigger 생성
    private Trigger buildCronTrigger(String name, String groupName, String cronExp) {
//        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
//                .simpleSchedule()
//                .withIntervalInSeconds(60)
//                .repeatForever();
        CronScheduleBuilder cronSchedule = CronScheduleBuilder
                .cronSchedule(cronExp)
                .withMisfireHandlingInstructionDoNothing();
/*
MISFIRE_INSTRUCTION_SMART_POLICY : 기본값으로 MISFIRE_INSTRUCTION_DO_NOTHING를 사용
MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY : 모든 misfired trigger를 실행
MISFIRE_INSTRUCTION_DO_NOTHING : misfired trigger가 한개 이상일 경우, 한번만 실행
MISFIRE_INSTRUCTION_FIRE_ONCE_NOW : misfired trigger를 모두 무시함.
*/
        return TriggerBuilder.newTrigger()
                .withIdentity(name, groupName)
                .withSchedule(cronSchedule)
                .build();
    }
}
