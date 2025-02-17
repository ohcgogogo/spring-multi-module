package com.example.businessservice.service;


import com.example.businessservice.dto.MemberPageableSearchDto;
import com.example.businessservice.mapper.MemberBusinessMapper;
import com.example.core.entity.Member;
import com.example.elasticsearchservice.service.MemberDocumentRepositoryService;
import com.example.rdbservice.service.MemberRepositoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberSearchService {
    private final MemberDocumentRepositoryService memberDocumentRepositoryService;
    private final MemberRepositoryService memberRepositoryService;
    public Member get(Long id) {
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), id.toString());
        id = memberDocumentRepositoryService.findById(id);
        return memberRepositoryService.findById(id);
    }

    public List<Member> search(MemberPageableSearchDto memberPageableSearchDto) {
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), memberPageableSearchDto.toString());
        List<Long> ids = memberDocumentRepositoryService.search(MemberBusinessMapper.INSTANCE.convert(memberPageableSearchDto));
        return memberRepositoryService.findByIds(ids);
    }
}
