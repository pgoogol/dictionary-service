package com.pgoogol.dictionary.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.pgoogol.dictionary.exception.JsonValidException;
import com.pgoogol.dictionary.model.DictionaryConfig;
import com.pgoogol.dictionary.model.DictionarySchema;
import com.pgoogol.dictionary.model.DictionarySchemaItems;
import com.pgoogol.dictionary.model.ModelDictionary;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JsonValidateService {

    private static final Logger log = LoggerFactory.getLogger(JsonValidateService.class);
    private static final ObjectMapper MAPPER = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    private static final String OBJECT = "object";

    @SneakyThrows({JsonValidException.class})
    public void validate(List<ModelDictionary> modelDictionary, Map<String, Object> data) {
        ProcessingReport validationReport = validateJson(modelDictionary, data);
        if (!validationReport.isSuccess()) {
            List<String> errors = new ArrayList<>();
            validationReport.forEach(processingMessage -> errors.add(processingMessage.getMessage()));
            throw new JsonValidException(errors);
        }
    }

    @SneakyThrows({JsonProcessingException.class, ProcessingException.class})
    public ProcessingReport validateJson(List<ModelDictionary> modelDictionary, Map<String, Object> data) {
        DictionarySchemaItems dictionarySchemaItems = DictionarySchemaItems
                .builder()
                .type(OBJECT)
                .properties(prepareSchema(modelDictionary))
                .required(getRequiredFields(modelDictionary))
                .build();
        JsonNode schemaNode = MAPPER.readValue(MAPPER.writeValueAsString(dictionarySchemaItems), JsonNode.class);
        JsonNode dataNode = MAPPER.readValue(MAPPER.writeValueAsString(data), JsonNode.class);

        JsonSchema jsonSchema = JsonSchemaFactory.byDefault().getJsonSchema(schemaNode);

        ProcessingReport validationReport = jsonSchema.validate(dataNode, true);
        return validationReport;
    }

    private Map<String, DictionarySchema> prepareSchema(List<ModelDictionary> modelDictionary) {
        Map<String, DictionarySchema> map = new HashMap<>();
        modelDictionary.forEach(md -> {
            DictionarySchema.DictionarySchemaBuilder builder = DictionarySchema.builder();
            builder.type(md.getFieldType());
            if (md.getItems() != null && !md.getItems().isEmpty()) {
                Map<String, DictionarySchema> schemaMap = prepareSchema(md.getItems());
                builder.items(
                    DictionarySchemaItems
                            .builder()
                            .type(OBJECT)
                            .properties(schemaMap)
                            .required(getRequiredFields(md.getItems()))
                            .build()
                );
            } else if (md.getProperties() != null && !md.getProperties().isEmpty()) {
                builder.properties(prepareSchema(md.getProperties()));
                builder.required(getRequiredFields(md.getProperties()));
            }
            map.put(md.getField(), builder.build());
        });
        return map;
    }

    private List<String> getRequiredFields(List<ModelDictionary> md) {
        return md.stream()
                .filter(ModelDictionary::isRequired)
                .map(ModelDictionary::getField)
                .collect(Collectors.toList());
    }

}
