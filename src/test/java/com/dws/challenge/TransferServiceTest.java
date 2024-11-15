package com.dws.challenge;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.AccountNotFoundException;
import com.dws.challenge.exception.InsufficientFundsException;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.NotificationService;
import com.dws.challenge.service.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class TransferServiceTest {

    @Mock
    private AccountsService accountsService;

    @Mock
    private NotificationService notificationService;

    private TransferService transferService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transferService = new TransferService(accountsService, notificationService);
    }

    @Test
    void testProcessTransferSuccess() {
        String accountFromId = "A1";
        String accountToId = "A2";
        BigDecimal amount = new BigDecimal("100.00");

        Account accountFrom = new Account(accountFromId, new BigDecimal("500.00"));
        Account accountTo = new Account(accountToId, new BigDecimal("200.00"));

        when(accountsService.getAccount(accountFromId)).thenReturn(accountFrom);
        when(accountsService.getAccount(accountToId)).thenReturn(accountTo);

        transferService.processTransfer(accountFromId, accountToId, amount);

        verify(accountsService, times(1)).getAccount(accountFromId);
        verify(accountsService, times(1)).getAccount(accountToId);
        verify(notificationService, times(2)).notifyAboutTransfer(any(), any());
        assertEquals(new BigDecimal("400.00"), accountFrom.getBalance());
        assertEquals(new BigDecimal("300.00"), accountTo.getBalance());
    }

    @Test
    void testProcessTransferInsufficientFunds() {
        String accountFromId = "A1";
        String accountToId = "A2";
        BigDecimal amount = new BigDecimal("600.00");

        Account accountFrom = new Account(accountFromId, new BigDecimal("500.00"));
        Account accountTo = new Account(accountToId, new BigDecimal("200.00"));

        when(accountsService.getAccount(accountFromId)).thenReturn(accountFrom);
        when(accountsService.getAccount(accountToId)).thenReturn(accountTo);
        
        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class, () -> {
            transferService.processTransfer(accountFromId, accountToId, amount);
        });
        assertEquals("Insufficient funds in account A1", exception.getMessage());
    }

    @Test
    void testProcessTransferAccountNotFound() {
        String accountFromId = "A1";
        String accountToId = "A2";
        BigDecimal amount = new BigDecimal("100.00");

        when(accountsService.getAccount(accountFromId)).thenReturn(null);
        when(accountsService.getAccount(accountToId)).thenReturn(new Account(accountToId, new BigDecimal("200.00")));

        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () -> {
            transferService.processTransfer(accountFromId, accountToId, amount);
        });
        assertEquals("Account A1 not found", exception.getMessage());
    }

    @Test
    void testProcessTransferSameSourceAndTarget() {
        String accountFromId = "A1";
        String accountToId = "A1";
        BigDecimal amount = new BigDecimal("100.00");
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transferService.processTransfer(accountFromId, accountToId, amount);
        });
        assertEquals("Transfer source and target cannot be the same.", exception.getMessage());
    }
}
