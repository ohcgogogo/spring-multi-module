package com.example.rdbservice.config.jpa;

import com.google.common.base.Preconditions;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.util.CollectionUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static java.util.function.Predicate.not;

@Slf4j
public class MDCHandlerCommentAppender {
    public static final Set<String> HANDLER_MDC_KEYS = Set.of("handler", "consumer", "batch");
    private final Set<String> handlerKeys; //Controller 혹은 Consumer 를 나타내는 handler 값을 저장할 MDC key 들. 이 중에서 값이 존재하는 하나만 사용하게 됩니다
    public MDCHandlerCommentAppender() {
        this(HANDLER_MDC_KEYS);
    }
    public MDCHandlerCommentAppender(Set<String> handlerKeys) {
        Preconditions.checkArgument(!CollectionUtils.isEmpty(handlerKeys), "handlerKeys 설정이 필요합니다.");
        this.handlerKeys = handlerKeys;
    }

    private Optional<String> readHandlerMdcValue() {
        return handlerKeys.stream()
                .map(MDC::get)
                .filter(Objects::nonNull)
                .filter(not(String::isBlank))
                .findFirst();
    }

    public String appendHandlerName(String sql) {
        final Optional<String> handlerMdcValue = readHandlerMdcValue();

        if (handlerMdcValue.isEmpty()) {
            return sql;
        }

        if (StringUtils.isBlank(handlerMdcValue.get())) {
            return sql;
        }
        return buildSqlWithHandlerInfo(sql, handlerMdcValue.get());
    }

    private String buildSqlWithHandlerInfo(String sql, String refinedHandlerMdcValue) {
        log.info("append handler info : {}", refinedHandlerMdcValue);
        return "/* handler: " + refinedHandlerMdcValue + " */ " + sql;
    }
}
