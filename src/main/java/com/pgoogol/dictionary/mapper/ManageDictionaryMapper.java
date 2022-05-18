package com.pgoogol.dictionary.mapper;

import com.pgoogol.dictionary.dto.DictionaryConfigRequest;
import com.pgoogol.dictionary.model.DictionaryConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ManageDictionaryMapper {

    @Mapping(target = "indexName", ignore = true)
    DictionaryConfig map(DictionaryConfigRequest source);

    @Mapping(target = "code", ignore = true)
    void map(@MappingTarget DictionaryConfig dictionaryConfig, DictionaryConfigRequest dictionaryConfig1);
}
