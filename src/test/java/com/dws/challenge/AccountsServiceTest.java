package com.dws.challenge;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.exception.InsufficientFundsException;
import com.dws.challenge.repository.AccountsRepository;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;

  @InjectMocks
  private AccountsService accountsService1;
  @MockitoBean
  private NotificationService notificationService;
  @Mock
  private AccountsRepository accountsRepository;
  @Mock
  private NotificationService notificationService1;
  private Account accountFrom;
  private Account accountTo;

  @BeforeEach
  void setUp() {
    // Initializing accounts with initial balances
    accountFrom = new Account("123", BigDecimal.valueOf(200.0));
    accountTo = new Account("456", BigDecimal.valueOf(100.0));

    // Mocking the account retrieval
    when(accountsRepository.getAccount("123")).thenReturn(accountFrom);
    when(accountsRepository.getAccount("456")).thenReturn(accountTo);
  }
  @Test
  void addAccount() {
    Account account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }

  @Test
  void addAccount_failsOnDuplicateId() {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }
  }
  @Test
  void transferMoney_SuccessfulTransfer() {
    double amount = 50.0;

    // Perform the transfer
    accountsService1.transferMoney("123", "456", amount, notificationService1);

    // Verify balances after transfer
    assert accountFrom.getBalance().compareTo(BigDecimal.valueOf(150.0)) == 0;
    assert accountTo.getBalance().compareTo(BigDecimal.valueOf(150.0)) == 0;

  }

  @Test
  void transferMoney_InsufficientFunds() {
    double amount = 250.0;

    // Expect InsufficientFundsException when funds are insufficient
    assertThrows(InsufficientFundsException.class, () ->
            accountsService1.transferMoney("123", "456", amount, notificationService1)
    );

    // Verify balances remain unchanged
    assert accountFrom.getBalance().compareTo(BigDecimal.valueOf(200.0)) == 0;
    assert accountTo.getBalance().compareTo(BigDecimal.valueOf(100.0)) == 0;

  }

  @Test
  void transferMoney_ZeroAmount() {
    double amount = 0.0;

    // Perform the transfer with zero amount
    accountsService1.transferMoney("123", "456", amount, notificationService1);

    // Verify that balances remain unchanged
    assert accountFrom.getBalance().compareTo(BigDecimal.valueOf(200.0)) == 0;
    assert accountTo.getBalance().compareTo(BigDecimal.valueOf(100.0)) == 0;

  }
  @Test
  void testTransferMoney_withNegativeAmount_throwsException() {
    String accountFromId = "123";
    String accountToId = "456";
    double amount = -100.0;
    accountFrom = new Account("123", BigDecimal.valueOf(200.0));
    accountTo = new Account("456", BigDecimal.valueOf(100.0));

    // Mocking the account retrieval
    when(accountsRepository.getAccount("123")).thenReturn(accountFrom);
    when(accountsRepository.getAccount("456")).thenReturn(accountTo);

    Exception exception = assertThrows(IllegalArgumentException.class, () ->
            accountsService1.transferMoney(accountFromId, accountToId, amount, notificationService1));

    assertEquals("Transfer amount must be positive", exception.getMessage());
  }
}
