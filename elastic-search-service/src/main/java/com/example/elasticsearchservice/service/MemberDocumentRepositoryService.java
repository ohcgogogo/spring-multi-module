package com.example.elasticsearchservice.service;

import com.example.core.entity.Member;
import com.example.elasticsearchservice.dto.MemberDocumentPageableSearchDto;
import com.example.elasticsearchservice.entity.MemberDocument;
import com.example.elasticsearchservice.mapper.MemberDocumentMapper;
import com.example.elasticsearchservice.repository.MemberDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberDocumentRepositoryService {
    private final MemberDocumentRepository memberDocumentRepository;

    @Transactional
    public Member save(Member member) {
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), member.toString());
        MemberDocument memberDocument = MemberDocumentMapper.INSTANCE.convert(member);
        memberDocument = this.memberDocumentRepository.save(memberDocument);
        return MemberDocumentMapper.INSTANCE.convert(memberDocument);
    }


    public Long findById(Long id) {
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), id.toString());
        MemberDocument memberDocument = this.memberDocumentRepository.findById(id);
        return memberDocument.getId();
    }

    public List<Long> search(MemberDocumentPageableSearchDto memberDocumentPageableSearchDto) {
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), memberDocumentPageableSearchDto.toString());
        SearchHits<MemberDocument>  searchHits = this.memberDocumentRepository.search(memberDocumentPageableSearchDto);
        return searchHits.stream().map(i -> Long.parseLong(i.getId())).collect(Collectors.toList());
    }
}
