package com.example.businessservice.service;


import com.example.businessservice.dto.MemberSignupDto;
import com.example.businessservice.event.SignedupEvent;
import com.example.businessservice.mapper.MemberBusinessMapper;
import com.example.core.entity.Authority;
import com.example.core.entity.Member;
import com.example.rdbservice.service.MemberRepositoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepositoryService memberRepositoryService;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public void signup(MemberSignupDto memberSignupDto) {
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), memberSignupDto.toString());
        Member member = MemberBusinessMapper.INSTANCE.convert(memberSignupDto);
        member.setAuthority(Authority.ROLE_USER);
        if (memberRepositoryService.isExists(member)) {
            throw new RuntimeException(member.getEmail() + "이미 가입되어 있는 유저입니다");
        }
        member = memberRepositoryService.save(member);
//        publisher.publishEvent(new TransactionalSignedupEvent(member));
        publisher.publishEvent(new SignedupEvent(member));
    }

}
