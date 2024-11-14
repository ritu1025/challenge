package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.InsufficientFundsException;
import com.dws.challenge.repository.AccountsRepository;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;

  private final ConcurrentHashMap<String, ReentrantLock> accountLocks = new ConcurrentHashMap<>();

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
    ReentrantLock lockFrom = accountLocks.computeIfAbsent(accountFromId, id -> new ReentrantLock());
    ReentrantLock lockTo = accountLocks.computeIfAbsent(accountToId, id -> new ReentrantLock());

    // Lock accounts to ensure no other operation interferes
    ReentrantLock firstLock = lockFrom;
    ReentrantLock secondLock = lockTo;

    if (accountFromId.compareTo(accountToId) > 0) { // Prevent deadlock by consistent ordering
      firstLock = lockTo;
      secondLock = lockFrom;
    }

    try {
      firstLock.lock();
      secondLock.lock();

      Account accountFrom = getAccount(accountFromId);
      Account accountTo = getAccount(accountToId);

      // Existing transfer logic with balance and notifications
      if (accountFrom.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
        throw new InsufficientFundsException("Insufficient funds for transfer");
      }

      if (amount < 0) {
        throw new IllegalArgumentException("Transfer amount must be positive");
      }

      accountFrom.withdraw(BigDecimal.valueOf(amount));
      accountTo.deposit(BigDecimal.valueOf(amount));

      accountsRepository.save(accountFrom);
      accountsRepository.save(accountTo);

      notificationService.notifyAboutTransfer(accountFrom, String.valueOf(amount));
      notificationService.notifyAboutTransfer(accountTo, String.valueOf(amount));
    } finally {
      firstLock.unlock();
      secondLock.unlock();
    }
  }
}
