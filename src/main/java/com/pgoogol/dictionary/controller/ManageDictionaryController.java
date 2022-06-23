package com.pgoogol.dictionary.controller;

import com.pgoogol.dictionary.dto.DictionaryConfigRequest;
import com.pgoogol.dictionary.dto.ResultPage;
import com.pgoogol.dictionary.mapper.UpdateResopnse;
import com.pgoogol.dictionary.model.DictionaryConfig;
import com.pgoogol.dictionary.service.ManageDictionaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/manage/dictionary")
@Tag(name = "Manage Dictionary", description = "Manage Dictionary API")
public class ManageDictionaryController {

    private final ManageDictionaryService manageDictionaryService;

    public ManageDictionaryController(ManageDictionaryService manageDictionaryService) {
        this.manageDictionaryService = manageDictionaryService;
    }

    @GetMapping
    @Operation(summary = "Get All Dictionaries", tags = {"Manage Dictionary"})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation"
            )
    })
    public ResponseEntity<ResultPage<DictionaryConfig>> getAll(
            @Parameter(
                    description = "Page",
                    schema = @Schema(implementation = Integer.class)
            ) @RequestParam @NotNull int page,
            @Parameter(
                    description = "Size",
                    schema = @Schema(implementation = Integer.class)
            ) @RequestParam @NotNull int size
    ) {
        ResultPage<DictionaryConfig> allDictionaryName = manageDictionaryService.getAll(PageRequest.of(page, size));
        return ResponseEntity.ok(allDictionaryName);
    }

    @GetMapping("{code}")
    @Operation(summary = "Get Dictionary by Code", tags = {"Manage Dictionary"})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = DictionaryConfig.class))
            )
    })
    public ResponseEntity<DictionaryConfig> getByCode(
            @Parameter(
                    description = "Dictionary Code",
                    schema = @Schema(implementation = String.class)
            ) @PathVariable String code
    ) {
        DictionaryConfig dictionaryConfig = manageDictionaryService.getByDictionaryCode(code);
        return ResponseEntity.ok(dictionaryConfig);
    }

    @PostMapping
    @Operation(summary = "Create Dictionary", tags = {"Manage Dictionary"})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successful Operation Created",
                    content = @Content(schema = @Schema(implementation = DictionaryConfig.class))
            )
    })
    public ResponseEntity<DictionaryConfig> createDictionary(
            @Parameter(
                    description = "Dictionary Configuration",
                    schema = @Schema(implementation = DictionaryConfigRequest.class)
            ) @RequestBody @Valid DictionaryConfigRequest dictionaryConfig
    ) {
        DictionaryConfig dictionary = manageDictionaryService.createDictionary(dictionaryConfig);
        return ResponseEntity.status(HttpStatus.CREATED).body(dictionary);
    }

    @PutMapping
    @Operation(summary = "Update Dictionary", tags = {"Manage Dictionary"})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successful Operation Created",
                    content = @Content(schema = @Schema(implementation = DictionaryConfig.class))
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful Operation Update",
                    content = @Content(schema = @Schema(implementation = DictionaryConfig.class))
            )
    })
    public ResponseEntity<DictionaryConfig> updateDictionary(
            @Parameter(
                    description = "Dictionary Configuration For Update",
                    schema = @Schema(implementation = DictionaryConfigRequest.class)
            ) @Valid @RequestBody DictionaryConfigRequest request
    ) {
        UpdateResopnse<DictionaryConfig> updateDictionary = manageDictionaryService.updateDictionary(request);
        if (updateDictionary.isUpdate()) {
            return ResponseEntity.status(HttpStatus.OK).body(updateDictionary.getValue());
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(updateDictionary.getValue());
        }
    }

}
