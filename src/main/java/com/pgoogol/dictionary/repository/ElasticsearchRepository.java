package com.pgoogol.dictionary.repository;

import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import lombok.SneakyThrows;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface ElasticsearchRepository {

    <T> HitsMetadata<T> getAll(String indexName, Class<T> clazz);

    default <T> Optional<T> getById(String indexName, String id) {
        return getById(indexName, id, Collections.emptyList());
    }

    default <T> Optional<T> getById(String indexName, String id, @NotNull String field) {
        return getById(indexName, id, Collections.singletonList(field));
    }
    <T> Optional<T> getById(String indexName, String id, @NotNull List<String> fields);

    @SneakyThrows
    <T> T save(String indexName, String id, T document);
    <T> T update(String indexName, String id, T document);
}
