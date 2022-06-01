package com.pgoogol.dictionary.service;

import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import com.pgoogol.dictionary.annotation.DictionaryExist;
import com.pgoogol.dictionary.dto.DictionaryConfigRequest;
import com.pgoogol.dictionary.dto.ResultPage;
import com.pgoogol.dictionary.exception.ResourceNotFoundException;
import com.pgoogol.dictionary.mapper.ManageDictionaryMapper;
import com.pgoogol.dictionary.mapper.UpdateResopnse;
import com.pgoogol.dictionary.model.DictionaryConfig;
import com.pgoogol.dictionary.repository.ElasticsearchIndiciesRepository;
import com.pgoogol.dictionary.repository.ElasticsearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Validated
@Service
public class ManageDictionaryService extends AbstractElasticsearchService {

    public static final String DICTIONARY_INDEX_NAME_SUFFIX = "_dictionary";
    private final ElasticsearchRepository repository;
    private final ElasticsearchIndiciesRepository indiciesRepository;
    private final ManageDictionaryMapper mapper;

    public ManageDictionaryService(ElasticsearchRepository repository,
                                   ElasticsearchIndiciesRepository indiciesRepository,
                                   ManageDictionaryMapper mapper
    ) {
        this.repository = repository;
        this.indiciesRepository = indiciesRepository;
        this.mapper = mapper;
    }

    @Override
    protected ElasticsearchRepository getElasticsearchRepository() {
        return repository;
    }

    public DictionaryConfig getByDictionaryCode(String dictionaryCode) {
        Optional<DictionaryConfig> byId = repository.getById(DICTIONARY_CONFIG, dictionaryCode, DictionaryConfig.class);
        if (byId.isPresent()) {
            return byId.get();
        } else {
            throw new ResourceNotFoundException(String.format("Not found element with code %s", dictionaryCode));
        }
    }

    public ResultPage<DictionaryConfig> getAll(PageRequest pageRequest) {
        HitsMetadata<DictionaryConfig> dictionaryName = repository.getAll(DICTIONARY_CONFIG, pageRequest, DictionaryConfig.class);

        return ResultPage
                .<DictionaryConfig>builder()
                .page(pageRequest.getPageNumber())
                .size(dictionaryName.hits().size())
                .totalElements(dictionaryName.total() != null ? dictionaryName.total().value() : 0)
                .totalPages((int) Math.ceil((double) dictionaryName.total().value() / pageRequest.getPageSize()))
                .data(getSource(dictionaryName))
                .build();
    }

    public DictionaryConfig createDictionary(@DictionaryExist DictionaryConfigRequest dictionaryConfig) {
        DictionaryConfig map = mapper.map(dictionaryConfig);
        map.setIndexName(dictionaryConfig.getCode() + DICTIONARY_INDEX_NAME_SUFFIX);
        indiciesRepository.create(map.getIndexName());
        return repository.save(DICTIONARY_CONFIG, map.getCode(), map);
    }

    public UpdateResopnse<DictionaryConfig> updateDictionary(DictionaryConfigRequest request) {
        String code = request.getCode();
        Optional<DictionaryConfig> byId = repository.getById(DICTIONARY_CONFIG, code, DictionaryConfig.class);
        if (byId.isPresent()) {
            DictionaryConfig dictionaryConfig = byId.get();
            mapper.map(dictionaryConfig, request);
            DictionaryConfig update = repository.update(DICTIONARY_CONFIG, code, dictionaryConfig);
            return UpdateResopnse.<DictionaryConfig>builder().value(update).update(true).build();
        } else {
            DictionaryConfig save = repository.save(DICTIONARY_CONFIG, code, null);
            return UpdateResopnse.<DictionaryConfig>builder().value(save).update(false).build();
        }
    }
}
