package com.pgoogol.dictionary.mapper;

import com.pgoogol.dictionary.dto.DictionaryConfigRequest;
import com.pgoogol.dictionary.model.DictionaryConfig;
import com.pgoogol.dictionary.model.ModelDictionary;
import com.pgoogol.elasticsearch.data.model.IndexModel;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ManageDictionaryMapper {

    @Mapping(target = "indexName", ignore = true)
    DictionaryConfig map(DictionaryConfigRequest source);

    @Mapping(target = "code", ignore = true)
    void map(@MappingTarget DictionaryConfig dictionaryConfig, DictionaryConfigRequest dictionaryConfig1);

    @Named("toIndexModel")
    IndexModel toIndexModel(ModelDictionary modelDictionary);

    @IterableMapping(qualifiedByName = "toIndexModel")
    List<IndexModel> mapToIndexModelList(List<ModelDictionary> modelDictionary);
}
