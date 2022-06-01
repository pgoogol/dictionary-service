package com.pgoogol.dictionary.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class JsonValidException extends Exception {

    private List<String> messages = new ArrayList<>();

    public JsonValidException() {
    }

    public JsonValidException(List<String> message) {
        super();
        this.messages = message;
    }
}
