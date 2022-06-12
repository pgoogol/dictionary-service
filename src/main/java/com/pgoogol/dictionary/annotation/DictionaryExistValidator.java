package com.pgoogol.dictionary.annotation;

import com.pgoogol.dictionary.dto.DictionaryConfigRequest;
import com.pgoogol.dictionary.exception.ResourceAlreadyExistsException;
import com.pgoogol.elasticsearch.data.repository.ElasticsearchRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DictionaryExistValidator implements ConstraintValidator<DictionaryExist, DictionaryConfigRequest> {

    private static final String DICTIONARY_CONFIG = "dictionary-config";
    public static final String EXCEPTION_MESSAGE = "Dictionary with code %s exist";

    private final ElasticsearchRepository repository;

    public DictionaryExistValidator(ElasticsearchRepository repository) {
        this.repository = repository;
    }

    @Override
    public void initialize(DictionaryExist constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(DictionaryConfigRequest value, ConstraintValidatorContext context) {
        boolean exists = repository.existsById(DICTIONARY_CONFIG, value.getCode());
        if (exists) {
            throw new ResourceAlreadyExistsException(String.format(EXCEPTION_MESSAGE, value.getCode()));
        }
        return true;
    }

}
