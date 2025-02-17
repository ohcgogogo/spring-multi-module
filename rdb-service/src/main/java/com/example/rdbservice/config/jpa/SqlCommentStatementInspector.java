package com.example.rdbservice.config.jpa;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Set;

@Component
@Slf4j
public class SqlCommentStatementInspector implements StatementInspector {
    // transient는 Serialize하는 과정에 제외하고 싶은 경우 선언하는 키워드 , Serialize시 null값이 대입된다
    private final transient MDCHandlerCommentAppender mdcHandlerCommentAppender;

    public SqlCommentStatementInspector() {
        this(MDCHandlerCommentAppender.HANDLER_MDC_KEYS);
    }

    public SqlCommentStatementInspector(Set<String> handlerKeys) {
        super();
        Preconditions.checkArgument(!CollectionUtils.isEmpty(handlerKeys), "handlerKeys 설정이 필요합니다.");
        this.mdcHandlerCommentAppender = new MDCHandlerCommentAppender(handlerKeys);
    }

    @Override
    public String inspect(String sql) {
        log.info("inspect sql : " + sql);
        return mdcHandlerCommentAppender.appendHandlerName(sql);
    }
}
