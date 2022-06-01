package com.pgoogol.dictionary.mapper;

import java.util.Map;

public interface MapDictionaryMapper {

    static void map(Map<String, Object> source, Map<String, Object> target, String idName) {
        source.entrySet()
                .stream()
                .filter(s -> !s.getKey().equals(idName))
                .forEach(entry -> target.put(entry.getKey(), entry.getValue()));
    }

}
