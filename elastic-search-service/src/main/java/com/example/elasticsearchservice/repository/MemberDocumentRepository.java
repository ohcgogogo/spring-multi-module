package com.example.elasticsearchservice.repository;


import ch.qos.logback.core.subst.Tokenizer;
import co.elastic.clients.elasticsearch._types.analysis.AnalyzerBuilders;
import co.elastic.clients.elasticsearch._types.analysis.TokenizerBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.indices.AnalyzeRequest;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import com.example.elasticsearchservice.dto.MemberDocumentPageableSearchDto;
import com.example.elasticsearchservice.entity.MemberDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQuery;
import org.springframework.data.elasticsearch.client.erhlc.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.convert.GeoConverters;
import org.springframework.data.elasticsearch.core.geo.GeoJsonLineString;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberDocumentRepository {
    private final ElasticsearchOperations elasticsearchOperations;

    public MemberDocument save(MemberDocument memberDocument) {
        return elasticsearchOperations.save(memberDocument);
    }
    public MemberDocument findById(Long id) {
        return elasticsearchOperations.get(String.valueOf(id), MemberDocument.class);
    }
    public SearchHits<MemberDocument> search(MemberDocumentPageableSearchDto memberDocumentPageableSearchDto) {
        log.info("{} : {}", Thread.currentThread().getStackTrace()[1].getMethodName(), memberDocumentPageableSearchDto.toString());
        List<String> fieldNames = MemberDocument.getEmailFieldNames();
        BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();
        for(String fieldName : fieldNames) {
//            WildcardQuery.Builder wildcardQuery = QueryBuilders.wildcard();
//            wildcardQuery
//                    .field(fieldName)
//                    .value("*"+memberDocumentPageableSearchDto.getKeyword()+"*");
//            boolQueryBuilder.should(wildcardQuery.build()._toQuery());

            MatchQuery.Builder matchQueryBuilder = QueryBuilders.match();
            matchQueryBuilder
                    .field(fieldName)
                    .query(memberDocumentPageableSearchDto.getKeyword())
                    .operator(Operator.Or);
            boolQueryBuilder.should(matchQueryBuilder.build()._toQuery());

            MatchPhraseQuery.Builder matchPhraseQueryBuilder = QueryBuilders.matchPhrase();
            matchPhraseQueryBuilder
                    .field(fieldName)
                    .query(memberDocumentPageableSearchDto.getKeyword());
            boolQueryBuilder.should(matchPhraseQueryBuilder.build()._toQuery());
        }
        NativeQuery query = NativeQuery.builder()
                .withQuery(boolQueryBuilder.build()._toQuery())
                .withPageable(memberDocumentPageableSearchDto.getPageable())
                .build();

//        Query query = NativeQuery.builder()
//                .withQuery(q -> q
//                        .bool(i -> i
//                                .should(s -> s
//                                        .wildcard(w -> w.field(MemberDocument.getEmailFieldName()).value(memberDocumentPageableSearchDto.getKeyword()))
//                                )
//                        )
//                )
//                .withPageable(memberDocumentPageableSearchDto.getPageable())
//                .build();

        SearchHits<MemberDocument> searchHits = elasticsearchOperations.search(query, MemberDocument.class);
//        searchHits.stream().forEach(i -> log.info(i.getContent().getEmail()));
        return searchHits;
    }
}
