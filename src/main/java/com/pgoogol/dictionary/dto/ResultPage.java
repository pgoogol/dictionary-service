package com.pgoogol.dictionary.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultPage<T> {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private List<T> data;
}
