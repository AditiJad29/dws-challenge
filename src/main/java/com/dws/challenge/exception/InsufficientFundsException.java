package com.dws.challenge.exception;


import com.dws.challenge.constants.ErrorConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class InsufficientFundsException extends RuntimeException {

    @Getter @Setter
    private String exceptionCode;

    public InsufficientFundsException(String message){
        super(message);
        this.exceptionCode= ErrorConstants.INSUFFICIENT_FUNDS;
    }
}
