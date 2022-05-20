package com.pgoogol.dictionary.service;

import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import com.pgoogol.dictionary.model.*;
import com.pgoogol.dictionary.repository.ElasticsearchRepository;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DictionaryService {

    private final ElasticsearchRepository repository;
    private final JsonValidateService jsonValidate;

    public DictionaryService(ElasticsearchRepository repository, JsonValidateService jsonValidate) {
        this.repository = repository;
        this.jsonValidate = jsonValidate;
    }

    public List<Object> getAll(String dictionaryCode) {
        Optional<DictionaryConfig> dictionaryConfig = repository.getById("", dictionaryCode, DictionaryConfig.class);
        if (dictionaryConfig.isPresent()) {
            HitsMetadata<Object> all = repository.getAll(dictionaryConfig.get().getIndexName(), Object.class);
            return all.hits().stream().map(Hit::source).collect(Collectors.toList());
        } else {
            throw new NoSuchElementException();
        }
    }

    @SneakyThrows
    public Object getByCode(String dictionaryCode, String code) {
        Optional<DictionaryConfig> dictionaryConfig = repository.getById("", dictionaryCode, DictionaryConfig.class);
        if (dictionaryConfig.isPresent()) {
            Optional<Object> byId = repository.getById(dictionaryConfig.get().getIndexName(), code, Object.class);
            if (byId.isPresent()) {
                return byId.get();
            } else {
                throw new NoSuchElementException();
            }
        } else {
            throw new NoSuchElementException();
        }
    }

    @SneakyThrows
    public Object create(String dictionaryCode, IndexDocument document) {
        Optional<DictionaryConfig> dictionaryConfigOptional = repository.getById("dictionary-config", dictionaryCode, DictionaryConfig.class);
        if (dictionaryConfigOptional.isPresent()) {
            DictionaryConfig dictionaryConfig = dictionaryConfigOptional.get();
            jsonValidate.validate(dictionaryConfig, document.getDocument());
            return repository.save(dictionaryConfig.getIndexName(), document.getId(), document.getDocument());
        } else {
            throw new NoSuchElementException();
        }
    }

    @SneakyThrows
    public Object update(String dictionaryCode, IndexDocument document) {
        Optional<DictionaryConfig> dictionaryConfigOptional = repository.getById("", dictionaryCode, "indexName", DictionaryConfig.class);
        if (dictionaryConfigOptional.isPresent()) {
            DictionaryConfig dictionaryConfig = dictionaryConfigOptional.get();
            jsonValidate.validate(dictionaryConfig, document.getDocument());
            return repository.update(dictionaryConfig.getIndexName(), document.getId(), document.getDocument());
        } else {
            throw new NoSuchElementException();
        }
    }
}
