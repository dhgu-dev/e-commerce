package com.loopers.application.common.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public class PageResult<T> extends PageImpl<T> {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES) // 속성 기반으로 객체 생성
    public PageResult(
        @JsonProperty("content") List<T> content,
        @JsonProperty("pageable") PageableResult pageable,
        @JsonProperty("total") Long total,
        @JsonProperty("page") PageMeta page
    ) {
        super(content, PageRequest.of(page.number, page.size), page.totalElements);
    }

    public PageResult(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public PageResult(List<T> content) {
        super(content);
    }

    private static class PageableResult extends PageRequest {

        @JsonCreator
        protected PageableResult(
            @JsonProperty("page") int page,
            @JsonProperty("size") int size,
            @JsonProperty("sort") Sort sort
        ) {
            super(page, size, sort);
        }
    }

    private static class PageMeta {

        private final Integer size;
        private final Integer number;
        private final Long totalElements;
        private final Long totalPages;

        @JsonCreator
        protected PageMeta(
            Integer size,
            Integer number,
            Long totalElements,
            Long totalPages
        ) {
            this.size = size;
            this.number = number;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
        }
    }
}
