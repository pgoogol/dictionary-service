package com.pgoogol.dictionary.repository;

import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import org.springframework.data.domain.PageRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface ElasticsearchRepository {

    <T> HitsMetadata<T> getAll(@NotBlank String indexName, @NotNull PageRequest pageRequest, @NotNull Class<T> clazz);

    default <T> Optional<T> getById(@NotBlank String indexName, @NotBlank String id, Class<T> clazz) {
        return getById(indexName, id, Collections.emptyList(), clazz);
    }

    default <T> Optional<T> getById(@NotBlank String indexName, @NotBlank String id, @NotBlank String field, Class<T> clazz) {
        return getById(indexName, id, Collections.singletonList(field), clazz);
    }

    <T> Optional<T> getById(@NotBlank String indexName, @NotBlank String id, @NotNull List<String> fields, Class<T> clazz);

    boolean existsById(@NotBlank String indexName, @NotBlank String id);

    <T> T save(@NotBlank String indexName, @NotBlank String id, T document);

    <T> T update(@NotBlank String indexName, @NotBlank String id, T document);
}
