package com.example.rdbservice.service;

import com.example.core.entity.CustomUserDetails;
import com.example.rdbservice.entity.MemberEntity;
import com.example.rdbservice.mapper.MemberEntityMapper;
import com.example.rdbservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsRepositoryService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), email);
        return memberRepository.findByEmail(email)
                .map(member -> createUserDetails(member))
                .orElseThrow(() -> new UsernameNotFoundException(email + "-> 데이터베이스에서 찾을 수 없습니다.") );
    }

    private UserDetails createUserDetails(MemberEntity member) {
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), member.toString());
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), member.getPassword());
        GrantedAuthority grantedAuthority =  new SimpleGrantedAuthority(member.getAuthority().toString());
//        User user =  new User(member.getEmail(), member.getPassword(), Collections.singleton(grantedAuthority));
//        User user =  new User(String.valueOf(member.getId()), member.getPassword(), Collections.singleton(grantedAuthority));
//        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), user.getUsername());

        return new CustomUserDetails(MemberEntityMapper.INSTANCE.convert(member));
    }
}
