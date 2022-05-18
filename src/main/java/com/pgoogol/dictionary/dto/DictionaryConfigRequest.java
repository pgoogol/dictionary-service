package com.pgoogol.dictionary.dto;

import com.pgoogol.dictionary.annotation.DictionaryExist;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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

}
