package com.example.rdbservice.config.jpa;

import com.example.core.entity.CustomUserDetails;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

@Component
public class PrincipalAuditorAware implements AuditorAware<Long>, DateTimeProvider {
    private static final Long SYSTEM = 1L;

    @Override
    public Optional<TemporalAccessor> getNow() {
        return Optional.of(ZonedDateTime.now());
    }

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(null == authentication || !authentication.isAuthenticated()) {
            return Optional.of(SYSTEM);
        }
        CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
        return Optional.of(userDetails.getId());
    }
}
