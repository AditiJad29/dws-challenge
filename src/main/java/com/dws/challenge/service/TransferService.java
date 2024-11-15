package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.AccountNotFoundException;
import com.dws.challenge.exception.InsufficientFundsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class TransferService {
    private final AccountsService accountsService;
    private final NotificationService notificationService;
    private ConcurrentHashMap<String, ReentrantLock> accountLocks;
    public TransferService(AccountsService accountsService, NotificationService notificationService) {
        this.accountsService = accountsService;
        this.notificationService = notificationService;
        this.accountLocks = new ConcurrentHashMap<>();
    }

    public void processTransfer(String accountFromId, String accountToId, BigDecimal amount) {

        log.info("Initiating transfer of {} from account {} to account {}", amount, accountFromId, accountToId);

        if(accountFromId.equals(accountToId)){
            throw new IllegalArgumentException("Transfer source and target cannot be the same.");
        }

        Account accountFrom = Optional.ofNullable(accountsService.getAccount(accountFromId))
                .orElseThrow(() -> new AccountNotFoundException("Account " + accountFromId + " not found"));

        Account accountTo = Optional.ofNullable(accountsService.getAccount(accountToId))
                .orElseThrow(() -> new AccountNotFoundException("Account " + accountToId + " not found"));

        String firstLockId = accountFromId.compareTo(accountToId) < 0 ? accountFromId : accountToId;
        String secondLockId = accountFromId.compareTo(accountToId) > 0 ? accountToId : accountFromId;

        // Ensure locks are acquired in a consistent order to avoid deadlock
        ReentrantLock firstLock = accountLocks.computeIfAbsent(firstLockId, k -> new ReentrantLock());
        ReentrantLock secondLock = accountLocks.computeIfAbsent(secondLockId, k -> new ReentrantLock());

        firstLock.lock();
        try {
            secondLock.lock();
            try {
                if (accountFrom.getBalance().compareTo(amount) < 0) {
                    throw new InsufficientFundsException("Insufficient funds in account " + accountFrom.getAccountId());
                }
                // Performing the transfer logic (atomic balance update)
                accountFrom.debit(amount);
                accountTo.credit(amount);

                // Notifying both accounts about the transfer
                sendTransferNotifications(accountFrom, accountTo, amount);
            } finally {
                secondLock.unlock();
            }
        } finally {
            firstLock.unlock();
        }
         log.info("Transfer of {} from account {} to account {} completed successfully", amount, accountFromId, accountToId);
    }

    @Async
    public void sendTransferNotifications(Account accountFrom, Account accountTo, BigDecimal amount) {
        notificationService.notifyAboutTransfer(accountFrom, "Account " + accountFrom.getAccountId() + " has successfully debited amount " + amount + " to Account " + accountTo.getAccountId() + ". New balance of Account " + accountFrom.getAccountId() + ": "  + accountFrom.getBalance());
        notificationService.notifyAboutTransfer(accountTo, "Account " + accountTo.getAccountId() + " is successfully credited with amount " + amount + " from Account " + accountFrom.getAccountId() + ". New balance of Account " + accountTo.getAccountId() + ": " + accountTo.getBalance());
    }
}