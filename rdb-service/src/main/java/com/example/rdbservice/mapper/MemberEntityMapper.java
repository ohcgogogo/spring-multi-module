package com.example.rdbservice.mapper;


import com.example.core.entity.Member;
import com.example.rdbservice.entity.MemberEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberEntityMapper {
    MemberEntityMapper INSTANCE = Mappers.getMapper(MemberEntityMapper.class);
    public MemberEntity convert(Member t);
    public Member convert(MemberEntity t);
    public List<Member> convert(List<MemberEntity> t);
}
