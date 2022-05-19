package com.pgoogol.dictionary.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.google.common.collect.Maps;
import com.pgoogol.dictionary.model.*;
import com.pgoogol.dictionary.repository.ElasticsearchRepository;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DictionaryService {

    private final ElasticsearchRepository repository;
    private final ElasticsearchClient client;

    public DictionaryService(ElasticsearchRepository repository, ElasticsearchClient client) {
        this.repository = repository;
        this.client = client;
    }

   /* private String getIndexName(String dictionaryCode) throws IOException {
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
    }*/

    public List<Object> getAll(String dictionaryCode) {
        Optional<String> indexName = repository.getById("", dictionaryCode, "indexName", String.class);
        if (indexName.isPresent()) {
            HitsMetadata<Object> all = repository.getAll((String) indexName.get(), Object.class);
            return all.hits().stream().map(Hit::source).collect(Collectors.toList());
        } else {
            throw new NoSuchElementException();
        }
    }

    @SneakyThrows
    public String getByCode(String dictionaryCode, String code) {
        Optional<String> indexName = repository.getById("", dictionaryCode, "indexName", String.class);
        if (indexName.isPresent()) {
            Optional<String> byId = repository.getById(indexName.get(), code, String.class);
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
        Optional<DictionaryConfig> indexName = repository.getById("dictionary-config", dictionaryCode, "indexName", DictionaryConfig.class);
        if (indexName.isPresent()) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            DictionaryConfig dictionaryConfig = mapper.convertValue(indexName.get(), DictionaryConfig.class);
            List<ModelDictionary> modelDictionary = dictionaryConfig.getModelDictionary();
            DictionarySchemaItems dictionarySchemaItems = DictionarySchemaItems
                    .builder()
                    .type("object")
                    .properties(get1(modelDictionary))
                    .build();
            JsonNode schemaNode1 = mapper.readValue(mapper.writeValueAsString(dictionarySchemaItems), JsonNode.class);

            JsonNode schemaNode = mapper.readValue(mapper.writeValueAsString(modelDictionary), JsonNode.class);
            JsonNode dataNode = mapper.readValue(mapper.writeValueAsString(document.getDocument()), JsonNode.class);

            JsonSchema jsonSchema = JsonSchemaFactory.byDefault().getJsonSchema(schemaNode1);

            ProcessingReport validationReport = jsonSchema.validate(dataNode);

            System.out.println(validationReport.toString());


             Object save = repository.save(dictionaryConfig.getIndexName(), document.getId(), document.getDocument());
            return null;
        } else {
            throw new NoSuchElementException();
        }
    }

    private Map<String, DictionarySchema> get1(List<ModelDictionary> modelDictionary) {
        Map<String, DictionarySchema> map = new HashMap<>();
        for (int i = 0; i < modelDictionary.size(); i++) {
            ModelDictionary md = modelDictionary.get(i);
            DictionarySchema.DictionarySchemaBuilder builder = DictionarySchema.builder();
            builder.type(md.getFieldType());
            if (md.getItems() != null && !md.getItems().isEmpty()) {
                Map<String, DictionarySchema> schemaMap = get1(md.getItems());
                builder.items(DictionarySchemaItems.builder().type("object").properties(schemaMap).build());
            } else if (md.getProperties() != null && !md.getProperties().isEmpty()) {
                builder.properties(get1(md.getProperties()));
            }
            map.put(md.getField(), builder.build());
        }
        return map;
    }

    @SneakyThrows
    public String update(String dictionaryCode, IndexDocument document) {
        Optional<String> indexName = repository.getById("", dictionaryCode, "indexName", String.class);
        if (indexName.isPresent()) {
            String update = repository.update((String) indexName.get(), document.getId(), document.getDocument().toString());
            return update;
        } else {
            throw new NoSuchElementException();
        }
    }
}
