package com.pgoogol.dictionary.controller;

import com.pgoogol.dictionary.model.IndexDocument;
import com.pgoogol.dictionary.service.DictionaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dictionary/{dictionary}")
public class DictionaryController {

    private final DictionaryService dictionaryService;

    public DictionaryController(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@PathVariable(name = "dictionary") String dictionaryCode) {
        List<Object> all = dictionaryService.getAll(dictionaryCode);
        return ResponseEntity.ok(all);
    }

    @GetMapping("{code}")
    public ResponseEntity<Object> getByCode(
            @PathVariable(name = "dictionary") String dictionaryCode,
            @PathVariable String code
    ) {
        Object byCode = dictionaryService.getByCode(dictionaryCode, code);
        return ResponseEntity.ok(byCode);
    }

    @PostMapping
    public ResponseEntity<Object> create(@PathVariable(name = "dictionary") String dictionaryCode, @RequestBody IndexDocument document) {
        Object o = dictionaryService.create(dictionaryCode, document);
        return ResponseEntity.ok(o);
    }

    @PutMapping
    public ResponseEntity<Object> update(@PathVariable(name = "dictionary") String dictionaryCode, @RequestBody IndexDocument document) {
        Object update = dictionaryService.update(dictionaryCode, document);
        return ResponseEntity.ok(update);
    }

}
