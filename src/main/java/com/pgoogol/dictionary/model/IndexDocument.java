package com.pgoogol.dictionary.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class IndexDocument {

    private String id;
    private Map<String, Object> document = new HashMap<>();

}
