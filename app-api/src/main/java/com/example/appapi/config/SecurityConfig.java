package com.example.appapi.config;


import com.example.appapi.config.security.jwt.JwtAccessDeniedHandler;
import com.example.appapi.config.security.jwt.JwtAuthenticationEntryPoint;
import com.example.appapi.config.security.jwt.JwtSecurityConfig;
import com.example.appapi.config.security.jwt.JwtTokenProvider;
import com.example.core.util.DefaultPasswordEncoder;
import com.example.rdbservice.service.CustomUserDetailsRepositoryService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static jakarta.servlet.DispatcherType.ERROR;
import static jakarta.servlet.DispatcherType.FORWARD;

@Configuration
@EnableWebSecurity // 기본적인 Web 보안 활성화
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final CustomUserDetailsRepositoryService customUserDetailsRepositoryService;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return DefaultPasswordEncoder.getDefaultPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setHideUserNotFoundExceptions(false);
        authProvider.setUserDetailsService(customUserDetailsRepositoryService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring().requestMatchers("/favicon.*", "/docs/**"); // Spring Security 로직을 수행하지 않고 아래 요청에 접근
//    }

    // https://velog.io/@choidongkuen/Spring-Security-SecurityConfig-%ED%81%B4%EB%9E%98%EC%8A%A4%EC%9D%98-permitAll-%EC%9D%B4-%EC%A0%81%EC%9A%A9%EB%90%98%EC%A7%80-%EC%95%8A%EC%95%98%EB%8D%98-%EC%9D%B4%EC%9C%A0
    // jwt 인증이 필요없는 경우를 제외하고 모드 인증 조치
    @Bean
    @Order(2)
    protected SecurityFilterChain filterChain(final @NotNull HttpSecurity http) throws Exception {
        http.httpBasic(HttpBasicConfigurer::disable)
                .csrf(CsrfConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(configurer -> configurer.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .authorizeHttpRequests(authorize->
                    authorize
                    .dispatcherTypeMatchers(FORWARD, ERROR).permitAll()
//                    .requestMatchers("/auth/**").permitAll()
//                    .requestMatchers("/search/member/**").permitAll()
//                    .requestMatchers("/serverSideEvent/**").permitAll()
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .anyRequest().authenticated()
                )
                .exceptionHandling(authenticationManager ->
                    authenticationManager.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                    .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .apply(new JwtSecurityConfig(jwtTokenProvider));
        return http.build();
    }

    // jwt 인증이 필요없는 경우
    @Bean
    @Order(1)
    protected SecurityFilterChain permitAllFilterChain(final @NotNull HttpSecurity http) throws Exception {
        http.securityMatcher("/favicon.*", "/docs/**","/auth/**","/search/member/**","/serverSideEvent/**")
                .httpBasic(HttpBasicConfigurer::disable)
                .csrf(CsrfConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(configurer -> configurer.frameOptions(frameOptions -> frameOptions.sameOrigin()));
        return http.build();
    }

//    @Bean
//    protected CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("http://localhost:9100"));
//        configuration.setAllowedMethods(Arrays.asList("*"));
//        configuration.setAllowCredentials(true);
//        configuration.setAllowedHeaders(Arrays.asList("*"));
////        configuration.setExposedHeaders(Arrays.asList("X-Get-Header")); // 직접 만든 해더를 사용해야할경우 이용.
//        configuration.setMaxAge(3600L);
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
}
