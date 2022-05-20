package com.pgoogol.dictionary.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class DictionarySchemaItems {

    private String type;
    private Map<String, DictionarySchema> properties;
    private List<String> required;

}