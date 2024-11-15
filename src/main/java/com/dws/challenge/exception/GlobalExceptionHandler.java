package com.dws.challenge.exception;

import com.dws.challenge.constants.ErrorConstants;
import com.dws.challenge.payloads.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> myMethodArgumentNotValidException(MethodArgumentNotValidException mave){
        Map<String, String> errorRes = new HashMap<>();
        mave.getBindingResult().getAllErrors().forEach((error)->{
            String fieldName = ((FieldError)error).getField();
            String errMsg = error.getDefaultMessage();
            errorRes.put(fieldName,errMsg);
        });
        return new ResponseEntity<>(errorRes, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateAccountIdException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateAccountIdException(DuplicateAccountIdException ex) {
        return new ResponseEntity<>(ErrorResponse.builder().errorMessage(ex.getMessage()).errorCode(ex.getExceptionCode()).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFoundException(AccountNotFoundException ex) {
        return new ResponseEntity<>(ErrorResponse.builder().errorMessage(ex.getMessage()).errorCode(ex.getExceptionCode()).build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFundsException(InsufficientFundsException ex) {
        return new ResponseEntity<>(ErrorResponse.builder().errorMessage(ex.getMessage()).errorCode(ex.getExceptionCode()).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ErrorResponse.builder().errorMessage(ex.getMessage()).errorCode(ErrorConstants.ILLEGAL_ARGUMENT_CODE).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return new ResponseEntity<>(ErrorResponse.builder().errorMessage(ex.getMessage()).errorCode(ErrorConstants.HTTP_MESSAGE_NOT_READABLE_CODE).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(ErrorResponse.builder().errorMessage("An error occurred while processing the request: " + ex.getMessage()).errorCode(ErrorConstants.GENERIC_EXCEPTION_CODE).build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return new ResponseEntity<>(ErrorResponse.builder().errorMessage("An unexpected error occurred: " + ex.getMessage()).errorCode(ErrorConstants.GENERIC_EXCEPTION_CODE).build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}