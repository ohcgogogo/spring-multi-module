package com.example.appapi.mapper;

import com.example.appapi.dto.*;
import com.example.businessservice.dto.MemberPageableSearchDto;
import com.example.businessservice.dto.MemberSignupDto;
import com.example.core.annotation.EncodedMapping;
import com.example.core.entity.Member;
import com.example.core.mapper.PasswordEncoderMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = PasswordEncoderMapper.class)
public interface MemberControllerMapper {
    MemberControllerMapper INSTANCE = Mappers.getMapper(MemberControllerMapper.class);

    default MemberPageableSearchDto convert(MemberPageableSearchRequestDto t) {
        return MemberPageableSearchDto.builder()
                .keyword(t.getKeyword())
                .pageable(PageRequest.of(t.getPageNumber() - 1, t.getPageSize()))
                .build();
    }

    default UsernamePasswordAuthenticationToken convert(MemberLoginDto t) {
        return new UsernamePasswordAuthenticationToken(t.getEmail(), t.getPassword());
    }

    @Mapping(source = "email", target = "email")
    @Mapping(source = "password", target = "password", qualifiedBy = EncodedMapping.class)
    public MemberSignupDto convert(MemberSignupRequestDto.Signup t);

    @Mapping(source = "email", target = "email")
//    @Mapping(source = "password", target = "password", qualifiedBy = EncodedMapping.class)
    @Mapping(source = "password", target = "password") // 패스워드 암호화 되지 않은 상태여야 additionalAuthenticationChecks에서 jwt token 로그인이 가능함.
    public MemberLoginDto convert(MemberLoginRequestDto t);

    public MemberResponseDto convert(Member t);
    public List<MemberResponseDto> convert(List<Member> t);
}