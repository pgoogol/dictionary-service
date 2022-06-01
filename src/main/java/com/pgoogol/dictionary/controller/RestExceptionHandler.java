package com.pgoogol.dictionary.controller;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import com.pgoogol.dictionary.exception.JsonValidException;
import com.pgoogol.dictionary.exception.ResourceAlreadyExistsException;
import com.pgoogol.dictionary.exception.ResourceNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LogManager.getLogger(RestExceptionHandler.class);

    @ExceptionHandler({IllegalArgumentException.class})
    protected ResponseEntity<Object> handleIllegalArgException(IllegalArgumentException exception, WebRequest request) {
        return handleBadRequest(exception, request);
    }

    @ExceptionHandler({IOException.class})
    protected ResponseEntity<Object> handleIOException(IOException exception, WebRequest request) {
        return handleServerErrorRequest(exception, request);
    }

    @ExceptionHandler({ElasticsearchException.class})
    protected ResponseEntity<Object> handleElasticsearchException(ElasticsearchException exception, WebRequest request) {
        return handleServerErrorRequest(exception, request);
    }

    @ExceptionHandler({ResourceAlreadyExistsException.class})
    protected ResponseEntity<Object> handleExistException(ResourceAlreadyExistsException exception, WebRequest request) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildErrorMap(HttpStatus.CONFLICT, request, exception.getMessage()));
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    protected ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException exception, WebRequest request) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildErrorMap(HttpStatus.NOT_FOUND, request, exception.getMessage()));
    }

    @ExceptionHandler({JsonValidException.class})
    protected ResponseEntity<Object> handleJsonValidException(JsonValidException exception, WebRequest request) {
        log.error(exception.getMessages(), exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildErrorMap(HttpStatus.BAD_REQUEST, request, exception.getMessages()));
    }

    @ExceptionHandler({javax.validation.ConstraintViolationException.class})
    protected ResponseEntity<Object> handleConstraintViolationException(
            javax.validation.ConstraintViolationException exception,
            WebRequest request
    ) {
        log.error(exception.getMessage(), exception);
        List<String> errors = exception.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildErrorMap(HttpStatus.BAD_REQUEST, request, errors));
    }

    @ExceptionHandler({javax.validation.ValidationException.class})
    protected ResponseEntity<Object> handleValidationException(
            javax.validation.ValidationException exception,
            WebRequest request
    ) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildErrorMap(
                        HttpStatus.BAD_REQUEST,
                        request,
                        "Validation Exception during validation: ",
                        exception.getMessage()
                ));
    }

    @ExceptionHandler({Throwable.class})
    protected ResponseEntity<Object> handleAllException(Throwable exception, WebRequest request) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorMap(HttpStatus.INTERNAL_SERVER_ERROR, request, exception.getMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleBindException(
            BindException exception,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        log.error(exception.getMessage(), exception);
        return handleValidExceptionRequest(exception.getBindingResult(), headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        log.error(exception.getMessage(), exception);
        return handleValidExceptionRequest(exception.getBindingResult(), headers, status, request);
    }

    private ResponseEntity<Object> handleValidExceptionRequest(
            BindingResult bindingResult,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        List<String> errors = bindingResult.getAllErrors()
                .stream()
                .map(this::prepareFieldOrObjectName)
                .collect(Collectors.toList());
        return new ResponseEntity<>(buildErrorMap(status, request, errors), headers, status);
    }

    private String prepareFieldOrObjectName(ObjectError objectError) {
        String field;
        if (objectError instanceof FieldError) {
            field = ((FieldError) objectError).getField();
        } else {
            field = objectError.getObjectName();
        }
        return String.format("'%s' field is incorrect: %s", field, objectError.getDefaultMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException exception,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        log.error(exception.getMessage(), exception);
        return handleBadRequest(exception, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        log.error(exception.getMessage(), exception);
        return handleBadRequest(exception, request);
    }

    private ResponseEntity<Object> handleBadRequest(Exception exception, WebRequest request) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildErrorMap(HttpStatus.BAD_REQUEST, request, exception.getMessage()));
    }

    private ResponseEntity<Object> handleServerErrorRequest(Exception exception, WebRequest request) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorMap(HttpStatus.INTERNAL_SERVER_ERROR, request, exception.getMessage()));
    }

    private Map<String, Object> buildErrorMap(HttpStatus status, WebRequest request, String error) {
        Map<String, Object> body = buildErrorMap(status, (ServletWebRequest) request);
        setMessage(error, body);
        return body;
    }

    private Map<String, Object> buildErrorMap(HttpStatus status, WebRequest request, String error, String constraintName) {
        Map<String, Object> body = buildErrorMap(status, (ServletWebRequest) request);
        setMessage(error, body);
        body.put("constraintName", constraintName);
        return body;
    }

    private Map<String, Object> buildErrorMap(HttpStatus status, WebRequest request, List<String> errors) {
        Map<String, Object> body = buildErrorMap(status, (ServletWebRequest) request);
        setMessage(errors, body);
        return body;
    }

    private Map<String, Object> buildErrorMap(HttpStatus status, ServletWebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("path", request.getRequest().getRequestURI());
        return body;
    }

    private void setMessage(Object error, Map<String, Object> body) {
        body.put("message", error);
    }

}
