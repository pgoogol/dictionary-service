package com.pgoogol.dictionary.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.FieldAndFormat;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ElasticsearchRepositoryImpl implements ElasticsearchRepository {

    private final ElasticsearchClient client;

    public ElasticsearchRepositoryImpl(ElasticsearchClient client) {
        this.client = client;
    }

    @SneakyThrows(IOException.class)
    @Override
    public <T> HitsMetadata<T> getAll(String indexName, Class<T> clazz) {
        SearchRequest request = new SearchRequest.Builder().index(indexName).build();

        SearchResponse<T> search = client.search(request, clazz);
        return search.hits();
    }

    @SneakyThrows(IOException.class)
    @Override
    public <T> Optional<T> getById(String indexName, String id, @NotNull List<String> fields, Class<T> clazz) {
        SearchRequest.Builder query = new SearchRequest
                .Builder()
                .index(indexName)
                .query(qb -> qb
                        .ids(idBuilder -> idBuilder
                                .queryName("idQuery" + id)
                                .values(id)
                        )
                );
        setFields(fields, query);
        SearchResponse<T> search = client.search(query.build(), clazz);
        return search.hits().hits().stream().findFirst().map(Hit::source);
    }

    private void setFields(List<String> fields, SearchRequest.Builder queryBuilder) {
        if (!fields.isEmpty()) {
            queryBuilder.fields(
                    fields.stream()
                            .map(field -> new FieldAndFormat.Builder().field(field).build())
                            .collect(Collectors.toList())
            );
        }
    }

    @SneakyThrows(IOException.class)
    @Override
    public <T> T save(String indexName, String id, T document) {
        client.create(builder -> builder
                .index(indexName)
                .document(document)
                .id(id)
        );
        return document;
    }

    @SneakyThrows(IOException.class)
    @Override
    public <T> T update(String indexName, String id, T document) {
        client.update(
                builder -> builder.index(indexName)
                        .id(id)
                        .doc(document),
                Object.class
        );
        return document;

    }

}
