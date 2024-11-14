package com.dws.challenge;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.InsufficientFundsException;
import com.dws.challenge.repository.AccountsRepository;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class AccountsServiceTestNew {
    @Mock
    private AccountsRepository accountsRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AccountsService accountsService;

    private Account accountFrom;
    private Account accountTo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initializing accounts with initial balances
        accountFrom = new Account("123", BigDecimal.valueOf(200.0));
        accountTo = new Account("456", BigDecimal.valueOf(100.0));

        // Mocking the account retrieval
        when(accountsRepository.getAccount("123")).thenReturn(accountFrom);
        when(accountsRepository.getAccount("456")).thenReturn(accountTo);
    }

    @Test
    void transferMoney_SuccessfulTransfer() {
        double amount = 50.0;

        // Perform the transfer
        accountsService.transferMoney("123", "456", amount, notificationService);

        // Verify balances after transfer
        assert accountFrom.getBalance().compareTo(BigDecimal.valueOf(150.0)) == 0;
        assert accountTo.getBalance().compareTo(BigDecimal.valueOf(150.0)) == 0;

    }

    @Test
    void transferMoney_InsufficientFunds() {
        double amount = 250.0;

        // Expect InsufficientFundsException when funds are insufficient
        assertThrows(InsufficientFundsException.class, () ->
                accountsService.transferMoney("123", "456", amount, notificationService)
        );

        // Verify balances remain unchanged
        assert accountFrom.getBalance().compareTo(BigDecimal.valueOf(200.0)) == 0;
        assert accountTo.getBalance().compareTo(BigDecimal.valueOf(100.0)) == 0;

    }

    @Test
    void transferMoney_ZeroAmount() {
        double amount = 0.0;

        // Perform the transfer with zero amount
        accountsService.transferMoney("123", "456", amount, notificationService);

        // Verify that balances remain unchanged
        assert accountFrom.getBalance().compareTo(BigDecimal.valueOf(200.0)) == 0;
        assert accountTo.getBalance().compareTo(BigDecimal.valueOf(100.0)) == 0;

    }
}
