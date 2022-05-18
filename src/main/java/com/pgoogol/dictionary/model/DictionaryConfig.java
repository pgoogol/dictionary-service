package com.pgoogol.dictionary.model;

import com.pgoogol.dictionary.annotation.DictionaryExist;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class DictionaryConfig {

    @NotNull
    private String code;

    private String indexName;

    @NotNull
    private boolean isActive;

    /*private SearchConfig searchConfig;*/

    /*private ModelDictionary modelDictionary;*/

}
