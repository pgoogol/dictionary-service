package com.pgoogol.dictionary.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import lombok.SneakyThrows;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Repository
public class ElasticsearchRepositoryImpl implements ElasticsearchRepository, ElasticsearchIndiciesRepository {

    private final ElasticsearchClient client;

    public ElasticsearchRepositoryImpl(ElasticsearchClient client) {
        this.client = client;
    }

    //todo separate interface for pagination and sortable like in data jpa
    @Override
    @SneakyThrows({IOException.class, ElasticsearchException.class})
    public <T> HitsMetadata<T> getAll(String indexName, PageRequest pageRequest, Class<T> clazz) {
        SearchRequest request = new SearchRequest
                .Builder()
                .index(indexName)
                .from(pageRequest.getPageNumber() * pageRequest.getPageSize())
                .size(pageRequest.getPageSize())
                .build();

        SearchResponse<T> search = client.search(request, clazz);
        return search.hits();
    }

    @Override
    @SneakyThrows({IOException.class, ElasticsearchException.class})
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
            queryBuilder.source(builder -> builder.filter(builder1 -> builder1.includes(fields)));
        }
    }

    @Override
    @SneakyThrows({IOException.class, ElasticsearchException.class})
    public <T> T save(String indexName, String id, T document) {
        client.create(builder -> builder
                .index(indexName)
                .document(document)
                .id(id)
        );
        return document;
    }

    @Override
    @SneakyThrows({IOException.class, ElasticsearchException.class})
    public <T> T update(String indexName, String id, T document) {
        client.update(
                builder -> builder.index(indexName)
                        .id(id)
                        .doc(document),
                Object.class
        );
        return document;

    }

    @Override
    @SneakyThrows({IOException.class, ElasticsearchException.class})
    public boolean existsById(String indexName, String id) {
        SearchResponse<Object> search = client.search(builder -> builder.index(indexName).query(builder1 -> builder1.ids(builder2 -> builder2.values(id))), Object.class);
        return (search.hits().total() != null ? search.hits().total().value() : 0) == 1;
    }

    //todo prepare fix mapping dictionary
    @Override
    @SneakyThrows({IOException.class, ElasticsearchException.class})
    public boolean create(String dictionaryName) {
        CreateIndexResponse createIndexResponse = client.indices().create(builder -> builder.index(dictionaryName));
        return createIndexResponse.acknowledged();
    }

    @Override
    @SneakyThrows({IOException.class, ElasticsearchException.class})
    public boolean exists(String dictionaryName) {
        BooleanResponse exists = client.indices().exists(builder -> builder.index(dictionaryName));
        return exists.value();
    }
}
