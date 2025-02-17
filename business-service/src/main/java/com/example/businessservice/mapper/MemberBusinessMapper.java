package com.example.businessservice.mapper;

import com.example.businessservice.dto.MemberPageableSearchDto;
import com.example.businessservice.dto.MemberSignupDto;
import com.example.core.entity.Member;
import com.example.elasticsearchservice.dto.MemberDocumentPageableSearchDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberBusinessMapper {
    MemberBusinessMapper INSTANCE = Mappers.getMapper(MemberBusinessMapper.class);
    @Mapping(target = "id", ignore = true )
    @Mapping(source = "email", target = "email")
    @Mapping(source = "password", target = "password")
    public Member convert(MemberSignupDto t);
    public MemberDocumentPageableSearchDto convert(MemberPageableSearchDto t);
}
