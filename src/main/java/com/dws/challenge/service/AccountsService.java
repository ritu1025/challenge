package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.InsufficientFundsException;
import com.dws.challenge.repository.AccountsRepository;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;

  @Autowired
  public AccountsService(AccountsRepository accountsRepository) {
    this.accountsRepository = accountsRepository;
  }

  public void createAccount(@Valid Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }
  // Transfer Money
  public synchronized void transferMoney(String accountFromId, String accountToId, double amount, NotificationService notificationService) {
    Account accountFrom = getAccount(accountFromId);
    Account accountTo = getAccount(accountToId);

    if (accountFrom.getBalance().compareTo(BigDecimal.valueOf(amount))<0) {
      throw new InsufficientFundsException("Insufficient funds for transfer");
    }

      accountFrom.withdraw(BigDecimal.valueOf(amount));
      accountTo.deposit(BigDecimal.valueOf(amount));

      // Save the updated accounts
      accountsRepository.save(accountFrom);
      accountsRepository.save(accountTo);

      // Send notifications
      notificationService.notifyAboutTransfer(accountFrom, String.valueOf(amount));
      notificationService.notifyAboutTransfer(accountTo, String.valueOf(amount));

  }
}
