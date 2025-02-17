package com.example.appapi.controller;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.example.appapi.TestConfiguration;
import com.example.appapi.config.security.jwt.JwtFilter;
import com.example.appapi.config.security.jwt.JwtToken;
import com.example.appapi.dto.MemberLoginRequestDto;
import com.example.appapi.dto.MemberSignupRequestDto;
import com.example.appapi.service.AuthService;
import com.example.businessservice.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 직접 설정한 Spring Security Configuration, Bean들도 불러오지 않는다
// 그 대신에 스프링 시큐리티가 자동으로 구성하는 Configuration 파일들을 불러와서 사용한다. 그래서 자동 설정에 맞춰서 요청을 진행.
@WebMvcTest(AuthRestController.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@ContextConfiguration(
        classes = TestConfiguration.class
)
@DisplayName("AuthRestController 테스트")
class AuthRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private AuthService authService;

    private String password = "P!1assword12";

    @DisplayName("회원가입")
    @WithMockUser(username = "테스트_최고관리자", roles = {"SUPER"})
    @Test
    void signup() throws Exception {
        String email = UUID.randomUUID().toString() + "@kr.accommate.com";
        MemberSignupRequestDto.Signup memberSignupRequestDto = new MemberSignupRequestDto.Signup(email, password);
//        memberSignupRequestDto.setEmail(email);
//        memberSignupRequestDto.setPassword(password);
        ObjectMapper objectMapper = new ObjectMapper();
//        String content = "{\"email\": \""+UUID.randomUUID().toString() + "@kr.accommate.com"+"\", \"password\":\"password\"}";

        doNothing().when(memberService).signup(any());

        mockMvc.perform(
//                    RestDocumentationRequestBuilders.post("/auth/signup")
//                    .param("email", UUID.randomUUID().toString() + "@kr.accommate.com")
//                    .param("password", "password")
                    RestDocumentationRequestBuilders
                        .post("/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, JwtFilter.BEARER_PREFIX)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(memberSignupRequestDto))
                )
                .andExpect(status().isOk())
//                .andDo(print());
//                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
                .andDo(MockMvcRestDocumentationWrapper.document("signup",
                        ResourceSnippetParameters.builder()
                            .tag("회원")
                            .summary("회원가입")
                            .description("회원가입설명")
                            .requestSchema(Schema.schema("MemberSignupRequestDto.Signup")),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
//                        relaxedRequestFields(
//                            fieldWithPath("email").type(JsonFieldType.STRING).description("email"),
//                            fieldWithPath("password").type(JsonFieldType.STRING).description("password")
//                        ),
                        requestFields(
                            fieldWithPath("email").type(JsonFieldType.STRING).description("email"),
                            fieldWithPath("password").type(JsonFieldType.STRING).description("password")
                        )
//                        responseFields()
                ))
                .andDo(print());
    }

    // TODO response검증과 관련 설명을 추가해야함.
    @DisplayName("로그인")
    @WithMockUser(username = "테스트_최고관리자", roles = {"SUPER"})
    @Test
    void login() throws Exception {
        MemberLoginRequestDto memberLoginRequestDto = new MemberLoginRequestDto("ben-121@kr.accommate.com", password);
        ObjectMapper objectMapper = new ObjectMapper();

        given(authService.login(any())).willReturn(JwtToken.builder()
                .grantType("bearer")
                .accessToken("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI1ODYiLCJhdXRoIjoiUk9MRV9VU0VSIiwiZXhwIjoxNzAwNzU4ODIxfQ.qrWK98TWR7e1kLRA9cDRXxSPjYkLwOKFOUcADS5WYjWXUHuNsxbVRU6mKU0gO6Dw5oYfygE5hDKAs0oq_3RB0g")
                .refreshToken("eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE3MDEzMzEyMjF9.SacZCKqV3oTwk-BDOIgqbA_QQh_15cWRDseovEEKbO2OKQEsAsT0XEj1BWf_3NXaiQFYvjEYsYOfyc8aKrltxA")
                .accessTokenExpiresIn(1700758821711L)
                .build());
        mockMvc.perform(
                        RestDocumentationRequestBuilders
                                .post("/auth/login")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
//                                .accept(MediaType.APPLICATION_JSON)
                                .accept(MediaTypes.HAL_JSON)
                                .header(HttpHeaders.AUTHORIZATION, JwtFilter.BEARER_PREFIX)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .content(objectMapper.writeValueAsString(memberLoginRequestDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grantType").value( "bearer"))
                .andExpect(jsonPath("$.accessToken").value( "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI1ODYiLCJhdXRoIjoiUk9MRV9VU0VSIiwiZXhwIjoxNzAwNzU4ODIxfQ.qrWK98TWR7e1kLRA9cDRXxSPjYkLwOKFOUcADS5WYjWXUHuNsxbVRU6mKU0gO6Dw5oYfygE5hDKAs0oq_3RB0g"))
                .andExpect(jsonPath("$.refreshToken").value( "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE3MDEzMzEyMjF9.SacZCKqV3oTwk-BDOIgqbA_QQh_15cWRDseovEEKbO2OKQEsAsT0XEj1BWf_3NXaiQFYvjEYsYOfyc8aKrltxA"))
                .andExpect(jsonPath("$.accessTokenExpiresIn").value( 1700758821711L))
                .andDo(MockMvcRestDocumentationWrapper.document("login",
                        ResourceSnippetParameters.builder()
                                .tag("회원")
                                .summary("로그인")
                                .description("로그인설명")
                                .requestSchema(Schema.schema("MemberLoginRequestDto"))
                                .responseSchema(Schema.schema("JwtToken")),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("email"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("password")
                        ),
                        responseFields(
                                fieldWithPath("grantType").type(JsonFieldType.STRING).description("grantType"),
                                fieldWithPath("accessToken").type(JsonFieldType.STRING).description("accessToken"),
                                fieldWithPath("refreshToken").type(JsonFieldType.STRING).description("refreshToken"),
                                fieldWithPath("accessTokenExpiresIn").type(JsonFieldType.NUMBER).description("accessTokenExpiresIn"),
                                fieldWithPath("_links.self.href").description("Link to self"),
                                fieldWithPath("_links.signup.href").description("회원가입"),
                                fieldWithPath("_links.signup.type").description("회원가입 Request Type")
                        )
//                        ),
//                        links(
//                                linkWithRel("self").description("link to self"),
//                                linkWithRel("signup").description("회원가입")
//                        )
                ))
                .andDo(print());
    }
}



