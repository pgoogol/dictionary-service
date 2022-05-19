package com.pgoogol.dictionary.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ModelDictionary {

    private String field;
    private String fieldType;
    private String label;
    private boolean required;
    private List<ModelDictionary> items;
    private List<ModelDictionary> properties;

}
