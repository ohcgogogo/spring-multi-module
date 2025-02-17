package com.example.businessjob.batch.job;

import com.example.businessservice.dto.MemberSignupDto;
import com.example.businessservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;


import java.util.UUID;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ExternalMemberSignupJobConfig {
    private final MemberService memberService;

    @Bean
    public Job externalMemberSignupJob(JobRepository jobRepository, Step externalSignupStepLambda, Step externalSignupStepAnonymousClass) {
        return new JobBuilder("externalMemberSignupJob", jobRepository)
                .start(externalSignupStepLambda)
                .next(externalSignupStepAnonymousClass)
                .incrementer(new ExternalMemberSignupJobParametersIncrementor())
                .preventRestart()
                .validator(new ExternalMemberSignupJobParametersValidator())
//                .incrementer(new RunIdIncrementer())
//                .validator(new DefaultJobParametersValidator(new String[]{"name","date"},new String[]{"count"}))
//                .listener()
                .build();
    }

    @Bean
    @JobScope
    protected Step externalSignupStepLambda(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("signupStepLambda", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.info("{} : [외부잡 수정] 여기에 비즈니스 로직 작성 1", Thread.currentThread().getStackTrace()[1].getMethodName());
                MemberSignupDto memberSignupDto = new MemberSignupDto();
                memberSignupDto.setEmail(UUID.randomUUID().toString() + "@kr.accommate.com");
                memberSignupDto.setPassword("password");
                memberService.signup(memberSignupDto);
                memberSignupDto.setEmail(UUID.randomUUID().toString() + "@kr.accommate.com");
                memberSignupDto.setPassword("password");
                memberService.signup(memberSignupDto);
                return RepeatStatus.FINISHED;
            }, platformTransactionManager)
            .build();
    }

    @Bean
    @JobScope
    protected Step externalSignupStepAnonymousClass(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        // TODO tasklet, reader, writer, processor 에 @StepScope를 붙여 주어려면, @Bean으로 등록할할수 있도록 method로 작성 변경해야함.
        return new StepBuilder("signupStepAnonymousClass", jobRepository)
            .tasklet(new Tasklet() {
                @Override
                public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                    log.info("{} : [외부잡 수정] 여기에 비즈니스 로직 작성 2", Thread.currentThread().getStackTrace()[1].getMethodName());
                    // Tasklet을 사용사에는 메타테이블에 Read-Count, Write-Count등의 메타 정보를 직접 기록해야함.

                    /*
                        RepeatStatus.FINISHED : 종료
                        RepeatStatus.CONTINUABLE : 다시 실행
                     */
                    // contribution.setExitStatus(ExitStatus.FAILED); // 정상적으로 처리되지 않았음을 MetaTable에 남겨둘수있다.
                    return RepeatStatus.FINISHED;
                }
            } , platformTransactionManager)
            .build();
    }
}
