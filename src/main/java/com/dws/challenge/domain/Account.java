package com.dws.challenge.domain;

import com.dws.challenge.exception.InsufficientFundsException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.locks.ReentrantLock;

@Data
public class Account {

  @NotNull
  @NotEmpty
  private final String accountId;

  @NotNull
  @Min(value = 0, message = "Initial balance must be positive.")
  private BigDecimal balance;

  @JsonIgnore
  private final ReentrantLock lock = new ReentrantLock();

  public Account(String accountId) {
    this.accountId = accountId;
    this.balance = BigDecimal.ZERO;
  }

  @JsonCreator
  public Account(@JsonProperty("accountId") String accountId, @JsonProperty("balance") BigDecimal balance) {
    this.accountId = accountId;
    this.balance = balance;
  }

  // Debit operation - withdraw amount from the account
  public void debit(BigDecimal amount) {
    lock.lock();
    try {
      if (amount.compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("Amount must be positive.");
      }
      if (balance.compareTo(amount) < 0) {
        throw new InsufficientFundsException("Insufficient funds in account " + accountId);
      }
      balance = balance.subtract(amount).setScale(2, RoundingMode.HALF_UP);
    } finally {
       lock.unlock();
    }
  }

  // Credit operation - add amount to the account
  public void credit(BigDecimal amount) {
    lock.lock();
    try {
      if (amount.compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("Amount must be positive.");
      }
      balance = balance.add(amount).setScale(2, RoundingMode.HALF_UP);
    } finally {
       lock.unlock();
    }
  }
}
