package com.pgoogol.dictionary.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import com.pgoogol.dictionary.model.DictionaryConfig;
import com.pgoogol.dictionary.model.IndexDocument;
import com.pgoogol.dictionary.repository.ElasticsearchRepository;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DictionaryService {

    private final ElasticsearchRepository repository;
    private final ElasticsearchClient client;

    public DictionaryService(ElasticsearchRepository repository, ElasticsearchClient client) {
        this.repository = repository;
        this.client = client;
    }

    private String getIndexName(String dictionaryCode) throws IOException {
        SearchResponse<DictionaryConfig> search = client.search(searchBuilder -> searchBuilder
                .index("addresses_version_read")
                .fields(builder -> builder.field("indexName"))
                .query(queryBuilder -> queryBuilder.ids(
                        idsBuilder -> idsBuilder
                                .queryName("idQuery")
                                .values(dictionaryCode)
                )), DictionaryConfig.class
        );
        return search.hits().hits().get(0).source() != null ? search.hits().hits().get(0).source().getIndexName() : null;
    }

    public List<Object> getAll(String dictionaryCode) {
        Optional<String> indexName = repository.getById("", dictionaryCode, "indexName");
        if (indexName.isPresent()) {
            HitsMetadata<Object> all = repository.getAll((String) indexName.get(), Object.class);
            return all.hits().stream().map(Hit::source).collect(Collectors.toList());
        } else {
            throw new NoSuchElementException();
        }
    }

    @SneakyThrows
    public String getByCode(String dictionaryCode, String code) {
        Optional<String> indexName = repository.getById("", dictionaryCode, "indexName");
        if (indexName.isPresent()) {
            Optional<String> byId = repository.getById((String) indexName.get(), code);
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
    public String create(String dictionaryCode, IndexDocument document) {
        Optional<String> indexName = repository.getById("", dictionaryCode, "indexName");
        if (indexName.isPresent()) {
            String save = repository.save((String) indexName.get(), document.getId(), document.getDocument().toString());
            return save;
        } else {
            throw new NoSuchElementException();
        }
    }

    @SneakyThrows
    public String update(String dictionaryCode, IndexDocument document) {
        Optional<String> indexName = repository.getById("", dictionaryCode, "indexName");
        if (indexName.isPresent()) {
            String update = repository.update((String) indexName.get(), document.getId(), document.getDocument().toString());
            return update;
        } else {
            throw new NoSuchElementException();
        }
    }
}
