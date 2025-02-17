package com.example.rdbservice.service;

import com.example.core.entity.Member;
import com.example.rdbservice.entity.MemberEntity;
import com.example.rdbservice.mapper.MemberEntityMapper;
import com.example.rdbservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberRepositoryService {
    private final MemberRepository memberRepository;

    public Member findByEmail(String email) {
       MemberEntity memberEntity = memberRepository.findByEmail(email).orElseThrow(()-> new RuntimeException(email + "데이터베이스에서 찾을 수 없습니다."));
       return MemberEntityMapper.INSTANCE.convert(memberEntity);
    }

    public Member findById(Long id) {
        MemberEntity memberEntity = memberRepository.findById(id).orElseThrow(()-> new RuntimeException(id + "데이터베이스에서 찾을 수 없습니다."));
        return MemberEntityMapper.INSTANCE.convert(memberEntity);
    }

    public List<Member> findByIds(List<Long> ids) {
        List<MemberEntity> memberEntitys = memberRepository.findByIdIn(ids);
        return MemberEntityMapper.INSTANCE.convert(memberEntitys);
    }

    public Boolean isExists(Member member) {
        log.info(member.toString());
        MemberEntity memberEntity = MemberEntityMapper.INSTANCE.convert(member);
        return memberRepository.existsByEmail(memberEntity.getEmail());
    }

    @Transactional
    public Member save(Member member) {
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), member.toString());
        MemberEntity memberEntity = MemberEntityMapper.INSTANCE.convert(member);
        memberEntity = memberRepository.save(memberEntity);
        return MemberEntityMapper.INSTANCE.convert(memberEntity);
    }
}
