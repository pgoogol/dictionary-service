package com.pgoogol.dictionary.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class DictionarySchema {

    private String type;
    private Map<String, DictionarySchema> properties;
    private DictionarySchemaItems items;
    private List<String> required;

}