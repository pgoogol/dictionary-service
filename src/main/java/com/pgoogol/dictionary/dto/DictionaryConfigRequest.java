package com.pgoogol.dictionary.dto;

import com.pgoogol.dictionary.model.ModelDictionary;
import com.pgoogol.dictionary.model.SearchConfig;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class DictionaryConfigRequest {

    @Schema(description = "Dictionary Code")
    @NotBlank
    private String code;

    @Schema(description = "Dictionary Name")
    @NotBlank
    private String name;

    @Schema(description = "Dictionary Description")
    private String description;

    @Schema(description = "Is Active?")
    @NotNull
    private boolean isActive = true;

    @ArraySchema(schema = @Schema(description = "Search Configuration", implementation = SearchConfig.class))
    private List<SearchConfig> searchConfig = Collections.emptyList();

    @ArraySchema(schema = @Schema(description = "Model Dictionary", implementation = ModelDictionary.class))
    private List<ModelDictionary> modelDictionary = Collections.emptyList();

}
