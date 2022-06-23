package com.pgoogol.dictionary.model.enums;

public enum Fields {

    INDEX_NAME("indexName"),
    MODEL_DICTIONARY("modelDictionary");

    private final String name;

    Fields(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
