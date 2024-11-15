package com.dws.challenge.exception;

import com.dws.challenge.constants.ErrorConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class AccountNotFoundException extends RuntimeException {

    @Getter @Setter
    private String exceptionCode;

    public AccountNotFoundException(String message){
        super(message);
        this.exceptionCode= ErrorConstants.ACCOUNT_NOT_FOUND;
    }
}
