package com.pgoogol.dictionary.controller;

import com.pgoogol.dictionary.dto.ResultPage;
import com.pgoogol.dictionary.mapper.UpdateResopnse;
import com.pgoogol.dictionary.model.IndexDocument;
import com.pgoogol.dictionary.service.DictionaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/dictionary/{dictionary}")
@Tag(name = "Dictionary", description = "Dictionary API")
public class DictionaryController {

    private final DictionaryService dictionaryService;

    public DictionaryController(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @GetMapping
    @Operation(summary = "Get All Data from Dictionary", tags = {"Dictionary"})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation"
            )
    })
    public ResponseEntity<ResultPage<Object>> getAll(
            @Parameter(
                    description = "Dictionary Code",
                    schema = @Schema(implementation = String.class)
            ) @PathVariable(name = "dictionary") @NotNull String dictionaryCode,
            @Parameter(
                    description = "Page",
                    schema = @Schema(implementation = Integer.class)
            ) @RequestParam @NotNull int page,
            @Parameter(
                    description = "Size",
                    schema = @Schema(implementation = Integer.class)
            ) @RequestParam @NotNull int size
    ) {
        ResultPage<Object> all = dictionaryService.getAll(dictionaryCode, PageRequest.of(page, size));
        return ResponseEntity.ok(all);
    }

    @GetMapping("{code}")
    @Operation(summary = "Get Data By Data-Code from Dictionary", tags = {"Dictionary"})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation"
            )
    })
    public ResponseEntity<Object> getByCode(
            @Parameter(
                    description = "Dictionary Code",
                    schema = @Schema(implementation = String.class)
            ) @PathVariable(name = "dictionary") String dictionaryCode,
            @Parameter(
                    description = "Data Code",
                    schema = @Schema(implementation = String.class)
            ) @PathVariable String code) {
        Object byCode = dictionaryService.getByCode(dictionaryCode, code);
        return ResponseEntity.ok(byCode);
    }

    @PostMapping
    @Operation(summary = "Add data to Dictionary", tags = {"Dictionary"})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation"
            )
    })
    public ResponseEntity<Object> create(
            @Parameter(
                    description = "Dictionary Code",
                    schema = @Schema(implementation = String.class)
            ) @PathVariable(name = "dictionary") String dictionaryCode,
            @Parameter(
                    description = "Data",
                    schema = @Schema(implementation = IndexDocument.class)
            ) @RequestBody IndexDocument document) {
        Object o = dictionaryService.create(dictionaryCode, document);
        return ResponseEntity.status(HttpStatus.CREATED).body(o);
    }

    @PutMapping
    @Operation(summary = "Update data in dictionary", tags = {"Dictionary"})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successful Operation Created"
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful Operation Update"
            )
    })
    public ResponseEntity<Object> update(
            @Parameter(
                    description = "Dictionary Code",
                    schema = @Schema(implementation = String.class)
            ) @PathVariable(name = "dictionary") String dictionaryCode,
            @Parameter(
                    description = "Data",
                    schema = @Schema(implementation = IndexDocument.class)
            ) @RequestBody IndexDocument document) {
        UpdateResopnse<Object> updateDictionary = dictionaryService.update(dictionaryCode, document);
        if (updateDictionary.isUpdate()) {
            return ResponseEntity.status(HttpStatus.OK).body(updateDictionary.getValue());
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(updateDictionary.getValue());
        }
    }

}
