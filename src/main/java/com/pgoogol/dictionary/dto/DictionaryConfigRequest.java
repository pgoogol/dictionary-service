package com.pgoogol.dictionary.dto;

import com.pgoogol.dictionary.annotation.DictionaryExist;
import com.pgoogol.dictionary.model.ModelDictionary;
import com.pgoogol.dictionary.model.SearchConfig;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@DictionaryExist
public class DictionaryConfigRequest {

    @NotNull
    private String code;

    @NotNull
    private String name;

    private String description;

    @NotNull
    private boolean isActive;

    private SearchConfig searchConfig;

    private List<ModelDictionary> modelDictionary;

}
