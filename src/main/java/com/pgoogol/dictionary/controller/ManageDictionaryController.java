package com.pgoogol.dictionary.controller;

import com.pgoogol.dictionary.model.DictionaryConfig;
import com.pgoogol.dictionary.service.ManageDictionaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manage/dictionary")
public class ManageDictionaryController {

    private final ManageDictionaryService manageDictionaryService;

    public ManageDictionaryController(ManageDictionaryService manageDictionaryService) {
        this.manageDictionaryService = manageDictionaryService;
    }

    @PostMapping
    public ResponseEntity<Object> createDictionary(@RequestBody DictionaryConfig dictionaryConfig) {
        DictionaryConfig dictionary = manageDictionaryService.createDictionary(dictionaryConfig);
        return ResponseEntity.ok(dictionary);
    }

    @GetMapping("name")
    public ResponseEntity<Object> getAllDictionaryName() {
        List<String> allDictionaryName = manageDictionaryService.getAllDictionaryName();
        return ResponseEntity.ok(allDictionaryName);
    }

}
