package com.pgoogol.dictionary.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ModelDictionary {

    @Schema(description = "Field name")
    private String field;

    @Schema(description = "Field type")
    private String fieldType;

    @Schema(description = "Field label")
    private String label;

    @Schema(description = "Is Required?")
    private boolean required;

    @ArraySchema(arraySchema = @Schema(description = "Array SubModel(ModelDictionary.class)", implementation = ModelDictionary.class))
    private List<ModelDictionary> items;

    @ArraySchema(arraySchema = @Schema(description = "SubModel(ModelDictionary.class)", implementation = ModelDictionary.class))
    private List<ModelDictionary> properties;

}
