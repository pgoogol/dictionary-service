package com.pgoogol.dictionary.controller;

import com.pgoogol.dictionary.model.IndexDocument;
import com.pgoogol.dictionary.service.DictionaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dictionary/{dictionary}")
public class DictionaryController {

    private final DictionaryService dictionaryService;

    public DictionaryController(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@PathVariable(name = "dictionary") String dictionaryCode) {
        dictionaryService.getAll(dictionaryCode);
        return null;
    }

    @GetMapping("{code}")
    public ResponseEntity<Object> getByCode(
            @PathVariable(name = "dictionary") String dictionaryCode,
            @PathVariable String code
    ) {
        dictionaryService.getByCode(dictionaryCode, code);
        return null;
    }

    @PostMapping
    public ResponseEntity<Object> create(@PathVariable(name = "dictionary") String dictionaryCode, @RequestBody IndexDocument document) {
        dictionaryService.create(dictionaryCode, document);
        return null;
    }

    @PutMapping
    public ResponseEntity<Object> update(@PathVariable(name = "dictionary") String dictionaryCode, IndexDocument document) {
        dictionaryService.update(dictionaryCode, document);
        return null;
    }

}
