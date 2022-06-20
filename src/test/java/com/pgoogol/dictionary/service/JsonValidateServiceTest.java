package com.pgoogol.dictionary.service;

import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.pgoogol.dictionary.exception.JsonValidException;
import com.pgoogol.dictionary.model.ModelDictionary;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

public class JsonValidateServiceTest {

    private JsonValidateService jsonValidateService = new JsonValidateService();

    @Test
    void simpleSuccess() {
        List<ModelDictionary> modelDictionary = Collections.singletonList(
                ModelDictionary
                        .builder()
                        .field("code")
                        .fieldType("string")
                        .required(true)
                        .build()
        );
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("code", "kod");
        Assertions.assertDoesNotThrow(() -> jsonValidateService.validate(modelDictionary, objectMap));
    }

    @Test
    void simpleFailed() {
        List<ModelDictionary> modelDictionary = Collections.singletonList(
                ModelDictionary
                        .builder()
                        .field("code")
                        .fieldType("string")
                        .required(true)
                        .build()
        );
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("code1", "kod");
        Assertions.assertThrows(JsonValidException.class, () -> jsonValidateService.validate(modelDictionary, objectMap));
    }

    @Test
    void oneRequiredField() {
        List<ModelDictionary> modelDictionary = Arrays.asList(
                ModelDictionary
                        .builder()
                        .field("code")
                        .fieldType("string")
                        .required(true)
                        .build(),
                ModelDictionary
                        .builder()
                        .field("value")
                        .fieldType("string")
                        .required(false)
                        .build()
        );
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("code", "kod");
        Assertions.assertDoesNotThrow(() -> jsonValidateService.validate(modelDictionary, objectMap));
    }

    @Test
    void wrongKeyInRequiredField() {
        List<ModelDictionary> modelDictionary = Arrays.asList(
                ModelDictionary
                        .builder()
                        .field("code")
                        .fieldType("string")
                        .required(true)
                        .build(),
                ModelDictionary
                        .builder()
                        .field("value")
                        .fieldType("string")
                        .required(false)
                        .build()
        );
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("code1", "kod");
        Assertions.assertThrows(JsonValidException.class, () -> jsonValidateService.validate(modelDictionary, objectMap));
    }

    @Test
    void oneRequiredFieldInValidateJson() {
        List<ModelDictionary> modelDictionary = Arrays.asList(
                ModelDictionary
                        .builder()
                        .field("code")
                        .fieldType("string")
                        .required(true)
                        .build(),
                ModelDictionary
                        .builder()
                        .field("value")
                        .fieldType("string")
                        .required(false)
                        .build()
        );
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("code", "kod");
        ProcessingReport processingMessages = jsonValidateService.validateJson(modelDictionary, objectMap);
        Assertions.assertTrue(processingMessages.isSuccess());
    }

    @Test
    void wrongKeyInRequiredFieldInValidateJson() {
        List<ModelDictionary> modelDictionary = Arrays.asList(
                ModelDictionary
                        .builder()
                        .field("code")
                        .fieldType("string")
                        .required(true)
                        .build(),
                ModelDictionary
                        .builder()
                        .field("value")
                        .fieldType("string")
                        .required(false)
                        .build()
        );
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("code1", "kod");
        ProcessingReport processingMessages = jsonValidateService.validateJson(modelDictionary, objectMap);
        List<String> missingField = new ArrayList<>();
        processingMessages.forEach(processingMessage ->
                missingField.add(processingMessage.asJson().get("missing").get(0).asText())
        );
        Assertions.assertFalse(processingMessages.isSuccess());
        Assertions.assertEquals(1, missingField.size());
        Assertions.assertEquals("code", missingField.get(0));
    }

}
