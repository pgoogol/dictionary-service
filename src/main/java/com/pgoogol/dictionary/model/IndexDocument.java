package com.pgoogol.dictionary.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class IndexDocument {

    @Schema(description = "Data id", implementation = String.class)
    private String id;
    @Schema(description = "Json data", implementation = Object.class)
    private Map<String, Object> document = new HashMap<>();

}
