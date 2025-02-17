package com.example.appapi.controller;

import com.example.appapi.dto.MemberPageableSearchRequestDto;
import com.example.appapi.dto.MemberResponseDto;
import com.example.appapi.mapper.MemberControllerMapper;
import com.example.businessservice.service.MemberSearchService;
import com.example.core.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/search/member")
@RequiredArgsConstructor
@Slf4j
public class MemberSearchRestController {
    private final MemberSearchService memberSearchService;

    // curl -X GET "http://localhost:8080/search/member/get?id=582" -H 'Content-Type:application/json' -H 'Authorization: Bearer '
    @GetMapping("/get")
    public ResponseEntity get(@RequestParam(value="id") Long id) {
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), id.toString());
        Member member = memberSearchService.get(id);
        MemberResponseDto responseDto = MemberControllerMapper.INSTANCE.convert(member);
        return ResponseEntity.ok(responseDto);
    }

    // curl -X GET "http://localhost:8080/search/member/search" -G --data-urlencode "keyword=다시" --data "pageNumber=1" --data "pageSize=10" -H 'Content-Type:application/json' -H 'Authorization: Bearer '
    @GetMapping("/search")
//    public ResponseEntity search(@RequestParam(value="keyword", required=true) String memberSearchKeyword) {
    public ResponseEntity search(MemberPageableSearchRequestDto memberPageableSearchRequestDto) {
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), memberPageableSearchRequestDto.toString());
        List<Member> members =  memberSearchService.search(MemberControllerMapper.INSTANCE.convert(memberPageableSearchRequestDto));
        List<MemberResponseDto> responseDto = MemberControllerMapper.INSTANCE.convert(members);
        return ResponseEntity.ok(responseDto);
    }
}
