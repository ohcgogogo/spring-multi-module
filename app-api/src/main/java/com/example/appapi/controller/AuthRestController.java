package com.example.appapi.controller;

import com.example.appapi.config.security.jwt.JwtToken;
import com.example.appapi.dto.MemberLoginRequestDto;
import com.example.appapi.dto.MemberSignupRequestDto;
import com.example.appapi.dto.validation.ValidationSequence;
import com.example.appapi.mapper.MemberControllerMapper;
import com.example.appapi.service.AuthService;
import com.example.businessservice.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

//@Validated
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthRestController {
    private final MemberService memberService;
    private final AuthService authService;

    /**
     * curl -X POST http://localhost:9100/auth/signup -H 'Content-Type:application/json' -H 'Authorization: Bearer ' -d '{ "email":"ben-63@kr.accommate.com", "password":"password"}'
     * curl -X POST http://localhost:8090/api/auth/auth/signup -H 'Content-Type:application/json' -H 'Authorization: Bearer ' -d '{ "email":"ben-63@kr.accommate.com", "password":"P1!assword12"}'
     */
    @PostMapping("/signup")
//    public void signup(@RequestBody @Valid MemberSignupRequestDto memberSignupRequestDto) {
    public ResponseEntity signup(@Validated(ValidationSequence.class) @RequestBody MemberSignupRequestDto.Signup memberSignupRequestDto) {
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), memberSignupRequestDto.toString());
        memberService.signup(MemberControllerMapper.INSTANCE.convert(memberSignupRequestDto));
        return ResponseEntity.ok().body(null);
    }

    /**
     * curl -X POST http://localhost:9100/auth/login -H 'Content-Type:application/json' -H 'Authorization: Bearer ' -d '{ "email":"ben-63@kr.accommate.com", "password":"password"}'
     * curl -X POST http://localhost:8090/api/auth/auth/login -H 'Content-Type:application/json' -H 'Authorization: Bearer ' -d '{ "email":"ben-63@kr.accommate.com", "password":"password"}'
     *
     * curl -X GET http://localhost:8090/api/user/ -H 'Content-Type:application/json' -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI1ODYiLCJhdXRoIjoiUk9MRV9VU0VSIiwiZXhwIjoxNjk5MjQxNzAzfQ.BHVjo4g0opl8afMYJeOPOhTvTrpCk5aaIJmybny2vyknTSrX-awKT4hr2Ba088um1Y8dZZLgpx9XxPPuP49ffQ'
     */
    @PostMapping("/login")
    public ResponseEntity<EntityModel<JwtToken>> login(@RequestBody @Valid MemberLoginRequestDto memberLoginRequestDto) {
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), memberLoginRequestDto.toString());
        JwtToken jwtToken  = authService.login(MemberControllerMapper.INSTANCE.convert(memberLoginRequestDto));

        return ResponseEntity.ok().header(SET_COOKIE, jwtToken.generateCookie().toString()).body(
                EntityModel.of(jwtToken)
                    .add(linkTo(methodOn(AuthRestController.class).signup(new MemberSignupRequestDto.Signup(memberLoginRequestDto.getEmail(), memberLoginRequestDto.getPassword()))).withRel("signup").withType("POST"))
                    .add(linkTo(methodOn(AuthRestController.class).login(memberLoginRequestDto)).withSelfRel())
        );
    }
}
