package com.pgoogol.dictionary.service;

import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import com.pgoogol.dictionary.dto.ResultPage;
import com.pgoogol.dictionary.exception.ResourceAlreadyExistsException;
import com.pgoogol.dictionary.exception.ResourceNotFoundException;
import com.pgoogol.dictionary.mapper.MapDictionaryMapper;
import com.pgoogol.dictionary.mapper.UpdateResopnse;
import com.pgoogol.dictionary.model.DictionaryConfig;
import com.pgoogol.dictionary.model.IndexDocument;
import com.pgoogol.dictionary.model.ModelDictionary;
import com.pgoogol.elasticsearch.data.repository.ElasticsearchRepository;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DictionaryService extends AbstractElasticsearchService {

    private static final Logger log = LoggerFactory.getLogger(DictionaryService.class);

    private final ElasticsearchRepository repository;
    private final JsonValidateService jsonValidate;

    public DictionaryService(ElasticsearchRepository repository, JsonValidateService jsonValidate) {
        this.repository = repository;
        this.jsonValidate = jsonValidate;
    }

    @Override
    protected ElasticsearchRepository getElasticsearchRepository() {
        return repository;
    }

    public ResultPage<Object> getAll(String dictionaryCode, PageRequest pageRequest) {
        String indexName = getIndexName(dictionaryCode);
        HitsMetadata<Object> all = repository.getAll(indexName, pageRequest, Object.class);
        log.info(String.valueOf(all.total().value()));
        return ResultPage
                .builder()
                .page(pageRequest.getPageNumber())
                .size(all.hits().size())
                .totalElements(all.total() != null ? all.total().value() : 0)
                .totalPages((int) Math.ceil((double) all.total().value() / pageRequest.getPageSize()))
                .data(getSource(all))
                .build();
    }

    @SneakyThrows
    public Object getByCode(String dictionaryCode, String code) {
        String indexName = getIndexName(dictionaryCode);
        Optional<Object> byId = repository.getById(indexName, code, Object.class);
        if (byId.isPresent()) {
            return byId.get();
        } else {
            throw new ResourceNotFoundException(String.format("Not found element with code %s", dictionaryCode));
        }
    }

    @SneakyThrows
    public Object create(String dictionaryCode, IndexDocument document) {
        Optional<DictionaryConfig> dictionaryConfigOptional =
                getDictionaryConfig(dictionaryCode, Arrays.asList("indexName", "modelDictionary"));
        if (dictionaryConfigOptional.isPresent()) {
            DictionaryConfig dictionaryConfig = dictionaryConfigOptional.get();
            if (isExistsById(dictionaryConfig.getIndexName(), document.getId())) {
                throw new ResourceAlreadyExistsException(String.format("Dictionary with code %s exist", dictionaryCode));
            }
            return validAndSave(dictionaryConfig.getModelDictionary(), document, dictionaryConfig.getIndexName());
        } else {
            throw new ResourceNotFoundException(String.format("Not found element with code %s", dictionaryCode));
        }
    }

    @SneakyThrows
    public UpdateResopnse<Object> update(String dictionaryCode, IndexDocument document) {
        Optional<DictionaryConfig> dictionaryConfigOptional =
                getDictionaryConfig(dictionaryCode, Arrays.asList("indexName", "modelDictionary"));
        if (dictionaryConfigOptional.isPresent()) {
            DictionaryConfig dictionaryConfig = dictionaryConfigOptional.get();
            String indexName = dictionaryConfig.getIndexName();
            List<ModelDictionary> modelDictionary = dictionaryConfig.getModelDictionary();
            Optional<Map<String, Object>> byId = getById(indexName, document.getId());
            if (byId.isPresent()) {
                return UpdateResopnse.builder()
                        .value(validAndUpadte(document, modelDictionary, indexName, byId.get()))
                        .update(true)
                        .build();
            } else {
                return UpdateResopnse.builder()
                        .value(validAndSave(modelDictionary, document, indexName))
                        .update(false)
                        .build();
            }
        } else {
            throw new ResourceNotFoundException(String.format("Not found element with code %s", dictionaryCode));
        }
    }

    private Map<String, Object> validAndSave(List<ModelDictionary> modelDictionary, IndexDocument document,
                                             String indexName) {
        jsonValidate.validate(modelDictionary, document.getDocument());
        return repository.save(indexName, document.getId(), document.getDocument());
    }

    private Map<String, Object> validAndUpadte(IndexDocument document, List<ModelDictionary> modelDictionary,
                                               String indexName, Map<String, Object> target) {
        MapDictionaryMapper.map(document.getDocument(), target, "code");
        jsonValidate.validate(modelDictionary, document.getDocument());
        return repository.update(indexName, document.getId(), document.getDocument());
    }
}
