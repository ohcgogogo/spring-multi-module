package com.example.elasticsearchservice.mapper;

import com.example.core.entity.Member;
import com.example.elasticsearchservice.dto.MemberDocumentPageableSearchDto;
import com.example.elasticsearchservice.entity.MemberDocument;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MemberDocumentMapper {
    MemberDocumentMapper INSTANCE = Mappers.getMapper(MemberDocumentMapper.class);
    public MemberDocument convert(Member t);
    public Member convert(MemberDocument t);
}
