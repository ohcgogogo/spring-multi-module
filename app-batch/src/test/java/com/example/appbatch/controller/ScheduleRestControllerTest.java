package com.example.appbatch.controller;

import com.example.appbatch.quartz.config.QuartzConfig;
import com.example.appbatch.quartz.config.QuartzJobFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ActiveProfiles({"default","develop"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class ScheduleRestControllerTest {
    @Autowired
    private Scheduler scheduler;

    @Autowired
    MockMvc mockMvc;

    @Test
    void contextLoads() {
        assertNotNull(scheduler);
    }

    @Test
    @DisplayName("Scheduler List 확인")
    void test1() throws Exception {
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
                    triggerMap.put("triggers", triggers.toString());
                }
                schedulerList.add(triggerMap);
            }
        }
        schedulerList.stream().forEach(i -> log.info(i.toString()));
        assertTrue(true);
    }

    @Test
    @DisplayName("Scheduler List 확인")
    public void test() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/batch/schedule/list"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}