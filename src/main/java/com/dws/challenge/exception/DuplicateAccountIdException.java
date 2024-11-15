package com.dws.challenge.exception;

import com.dws.challenge.constants.ErrorConstants;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class DuplicateAccountIdException extends RuntimeException {

  @Getter @Setter
  private String exceptionCode;

  public DuplicateAccountIdException(String message){
    super(message);
    this.exceptionCode= ErrorConstants.DUPLICATE_ACCOUNT_ID;
  }
}
