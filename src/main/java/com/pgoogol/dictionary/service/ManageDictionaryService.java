package com.pgoogol.dictionary.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import com.pgoogol.dictionary.model.DictionaryConfig;
import com.pgoogol.dictionary.repository.ElasticsearchRepository;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ManageDictionaryService {

    private final ElasticsearchRepository repository;

    public ManageDictionaryService(ElasticsearchRepository repository) {
        this.repository = repository;
    }

    public DictionaryConfig createDictionary(DictionaryConfig dictionaryConfig) {
        return repository.save("dictionary-config", dictionaryConfig.getDictionaryName(), dictionaryConfig);
    }

    public DictionaryConfig getByDictionaryCode(String dictionaryCode) {
        Optional<DictionaryConfig> byId = repository.getById("dictionary-config", dictionaryCode);
        if (byId.isPresent()) {
            return byId.get();
        } else {
            throw new NoSuchElementException();
        }
    }

    public List<String> getAllDictionaryName() {
        HitsMetadata<DictionaryConfig> dictionaryName = repository.getAll("dictionary-config", DictionaryConfig.class);
        return dictionaryName
                .hits()
                .stream()
                .map(Hit::source)
                .filter(Objects::nonNull)
                .map(DictionaryConfig::getDictionaryName)
                .collect(Collectors.toList());
    }

}
