package com.pgoogol.dictionary.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class DictionaryConfig {

    @NotNull
    private String dictionaryName;

    @NotNull
    private String indexName;

    @NotNull
    private boolean isActive;

    private SearchConfig searchConfig;

    private ModelDictionary modelDictionary;

}
