package com.dws.challenge;

import com.dws.challenge.exception.InsufficientFundsException;
import com.dws.challenge.model.TransferRequest;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.NotificationService;
import com.dws.challenge.web.AccountTransferController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountTransferController.class)
class AccountTransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountsService accountsService;

    @MockitoBean
    private NotificationService notificationService;

    private TransferRequest transferRequest;

    @BeforeEach
    void setUp() {
        transferRequest = new TransferRequest();
        transferRequest.setAccountFromId("123");
        transferRequest.setAccountToId("456");
        transferRequest.setAmount(100.0);
    }

    @Test
    void transferMoney_Success() throws Exception {
        // Test successful transfer
        doNothing().when(accountsService).transferMoney(
                transferRequest.getAccountFromId(),
                transferRequest.getAccountToId(),
                transferRequest.getAmount(),
                notificationService
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"accountFromId\": \"123\", \"accountToId\": \"456\", \"amount\": 100.0 }"))
                .andExpect(status().isOk());

        verify(accountsService, times(1)).transferMoney(
                transferRequest.getAccountFromId(),
                transferRequest.getAccountToId(),
                transferRequest.getAmount(),
                notificationService
        );
    }

    @Test
    void transferMoney_InsufficientFunds() throws Exception {
        // Test insufficient funds scenario
        doThrow(new InsufficientFundsException("Insufficient funds"))
                .when(accountsService).transferMoney(
                        transferRequest.getAccountFromId(),
                        transferRequest.getAccountToId(),
                        transferRequest.getAmount(),
                        notificationService
                );

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"accountFromId\": \"123\", \"accountToId\": \"456\", \"amount\": 100.0 }"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient funds"));

        verify(accountsService, times(1)).transferMoney(
                transferRequest.getAccountFromId(),
                transferRequest.getAccountToId(),
                transferRequest.getAmount(),
                notificationService
        );
    }

    @Test
    void transferMoney_InternalServerError() throws Exception {
        // Test unexpected error scenario
        doThrow(new RuntimeException("Unexpected error"))
                .when(accountsService).transferMoney(
                        transferRequest.getAccountFromId(),
                        transferRequest.getAccountToId(),
                        transferRequest.getAmount(),
                        notificationService
                );

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"accountFromId\": \"123\", \"accountToId\": \"456\", \"amount\": 100.0 }"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Unexpected error"));

        verify(accountsService, times(1)).transferMoney(
                transferRequest.getAccountFromId(),
                transferRequest.getAccountToId(),
                transferRequest.getAmount(),
                notificationService
        );
    }
}
