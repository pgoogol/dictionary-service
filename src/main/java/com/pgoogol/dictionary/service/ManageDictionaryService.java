package com.pgoogol.dictionary.service;

import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import com.pgoogol.dictionary.dto.DictionaryConfigRequest;
import com.pgoogol.dictionary.mapper.ManageDictionaryMapper;
import com.pgoogol.dictionary.model.DictionaryConfig;
import com.pgoogol.dictionary.repository.ElasticsearchRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ManageDictionaryService {

    private static final String DICTIONARY_CONFIG = "dictionary-config";
    private final ElasticsearchRepository repository;
    private final ManageDictionaryMapper mapper;

    public ManageDictionaryService(ElasticsearchRepository repository, ManageDictionaryMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public DictionaryConfig getByDictionaryCode(String dictionaryCode) {
        Optional<DictionaryConfig> byId = repository.getById(DICTIONARY_CONFIG, dictionaryCode, DictionaryConfig.class);
        if (byId.isPresent()) {
            return byId.get();
        } else {
            throw new NoSuchElementException();
        }
    }

    public List<DictionaryConfig> getAll() {
        HitsMetadata<DictionaryConfig> dictionaryName = repository.getAll(DICTIONARY_CONFIG, DictionaryConfig.class);
        return dictionaryName
                .hits()
                .stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    public DictionaryConfig createDictionary(DictionaryConfigRequest dictionaryConfig) {
        DictionaryConfig map = mapper.map(dictionaryConfig);
        map.setIndexName(dictionaryConfig.getCode() + "_dictionary");
        return repository.save(DICTIONARY_CONFIG, map.getCode(), map);
    }

    public DictionaryConfig updateDictionary(DictionaryConfigRequest request) {
        String code = request.getCode();
        Optional<DictionaryConfig> byId = repository.getById(DICTIONARY_CONFIG, code, DictionaryConfig.class);
        if (byId.isPresent()) {
            DictionaryConfig dictionaryConfig = byId.get();
            mapper.map(dictionaryConfig, request);
            return repository.update(DICTIONARY_CONFIG, code, dictionaryConfig);
        } else {
            throw new NoSuchElementException();
        }
    }
}
