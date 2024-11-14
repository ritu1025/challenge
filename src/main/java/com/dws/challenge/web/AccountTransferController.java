package com.dws.challenge.web;

import com.dws.challenge.exception.InsufficientFundsException;
import com.dws.challenge.model.TransferRequest;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountTransferController {
    @Autowired
    AccountsService accountsService;

    @Autowired
    NotificationService notificationService;
    @PostMapping(path = "/transfer", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> transferMoney(@RequestBody @Valid TransferRequest transferRequest) {
        log.info("Initiating transfer from {} to {} for amount {}", transferRequest.getAccountFromId(),
                transferRequest.getAccountToId(), transferRequest.getAmount());

        try {
            this.accountsService.transferMoney(transferRequest.getAccountFromId(), transferRequest.getAccountToId(),
                    transferRequest.getAmount(), notificationService);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (InsufficientFundsException e) {
            log.error("Insufficient funds for transfer: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error during transfer: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
