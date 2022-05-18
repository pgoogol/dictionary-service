package com.pgoogol.dictionary.controller;

import com.pgoogol.dictionary.dto.DictionaryConfigRequest;
import com.pgoogol.dictionary.model.DictionaryConfig;
import com.pgoogol.dictionary.service.ManageDictionaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/manage/dictionary")
public class ManageDictionaryController {

    private final ManageDictionaryService manageDictionaryService;

    public ManageDictionaryController(ManageDictionaryService manageDictionaryService) {
        this.manageDictionaryService = manageDictionaryService;
    }

    @GetMapping
    public ResponseEntity<List<DictionaryConfig>> getAll() {
        List<DictionaryConfig> allDictionaryName = manageDictionaryService.getAll();
        return ResponseEntity.ok(allDictionaryName);
    }

    @GetMapping("{code}")
    public ResponseEntity<DictionaryConfig> getByCode(@PathVariable String code) {
        DictionaryConfig dictionaryConfig = manageDictionaryService.getByDictionaryCode(code);
        return ResponseEntity.ok(dictionaryConfig);
    }

    @PostMapping
    public ResponseEntity<DictionaryConfig> createDictionary(@RequestBody @Valid DictionaryConfigRequest dictionaryConfig) {
        DictionaryConfig dictionary = manageDictionaryService.createDictionary(dictionaryConfig);
        return ResponseEntity.ok(dictionary);
    }

    @PutMapping
    public ResponseEntity<DictionaryConfig> updateDictionary(@RequestBody DictionaryConfigRequest request) {
        DictionaryConfig dictionary = manageDictionaryService.updateDictionary(request);
        return ResponseEntity.ok(dictionary);
    }

}
