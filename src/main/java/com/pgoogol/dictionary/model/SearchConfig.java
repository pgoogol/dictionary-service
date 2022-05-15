package com.pgoogol.dictionary.model;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.pgoogol.dictionary.model.enums.ListType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class SearchConfig {

    public static final ListType FILTER = ListType.FILTER;
    public static final Query.Kind WILDCARD = Query.Kind.Wildcard;


    @NotBlank
    private String field;

    @NotBlank
    private String indexField;

    private ListType listType = FILTER;

    private Query.Kind queryKind = WILDCARD;


}
