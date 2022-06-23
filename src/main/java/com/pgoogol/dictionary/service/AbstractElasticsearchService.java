package com.pgoogol.dictionary.service;

import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import com.pgoogol.dictionary.exception.ResourceNotFoundException;
import com.pgoogol.dictionary.model.DictionaryConfig;
import com.pgoogol.elasticsearch.data.repository.ElasticsearchRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.pgoogol.dictionary.model.enums.Fields.INDEX_NAME;

public abstract class AbstractElasticsearchService {

    protected static final String DICTIONARY_CONFIG = "dictionary-config";

    protected abstract ElasticsearchRepository getElasticsearchRepository();

    protected String getIndexName(String dictionaryCode) {
        Optional<DictionaryConfig> dictionaryConfig = getElasticsearchRepository()
                .getById(DICTIONARY_CONFIG, dictionaryCode, INDEX_NAME.getName(), DictionaryConfig.class);
        if (dictionaryConfig.isPresent()) {
            return dictionaryConfig.get().getIndexName();
        } else {
            throw new ResourceNotFoundException("dictionary not exist");
        }
    }

    protected Optional<DictionaryConfig> getDictionaryConfig(String dictionaryCode, List<String> filedList) {
        return getElasticsearchRepository()
                .getById(DICTIONARY_CONFIG, dictionaryCode, filedList, DictionaryConfig.class);
    }

    @SuppressWarnings("unchecked")
    protected Optional<Map<String, Object>> getById(String indexName, String dictionaryCode) {
        Class<Map<String,Object>> clazz = (Class)Map.class;
        return getElasticsearchRepository()
                .getById(indexName, dictionaryCode, clazz);
    }

    protected <T> List<T> getSource(HitsMetadata<T> dictionaryName) {
        return dictionaryName
                .hits()
                .stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    protected boolean isExistsById(String indexName, String id) {
        return getElasticsearchRepository().existsById(indexName, id);
    }

}
