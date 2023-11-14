package com.transfer.contoller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfer.model.dto.AccountDto;
import com.transfer.model.dto.TransferReqDto;
import com.transfer.model.entity.Account;
import com.transfer.repository.AccountRepository;
import com.transfer.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "test")
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void transferMoney_single_success() throws Exception {
        AccountDataModel dataBeforeCall = getAccountData("init/json/transfer_AABB1122.json");
        callToAccountTransfer(dataBeforeCall, "/rest/api/v1/account/transfer");
        Account accountAfter = accountRepository.getAccountBySerial(dataBeforeCall.transfer().getDestAccountSerial()).orElseThrow();
        checkSuccessTransaction(dataBeforeCall.accountBefore(), accountAfter, List.of(dataBeforeCall.transfer()), dataBeforeCall.transactionCountBefore());
    }

    @Test
    void transferMoneyForNonExistentAccount_single_success() throws Exception {

        final File transferFile = new ClassPathResource("init/json/transfer_non_exist_AABB11222.json").getFile();
        String transferJson = Files.readString(transferFile.toPath());

        this.mockMvc.perform(post("/rest/api/v1/account/transfer")
                        .contentType(APPLICATION_JSON)
                        .content(transferJson))
                .andDo(print())
                .andExpect(status().is5xxServerError())
        ;
    }

    @Test
    void transferMoneyNegativeAmount_single_error() throws Exception {

        final File transferFile = new ClassPathResource("init/json/transfer_negative_ammount_AABB1122.json").getFile();
        String transferJson = Files.readString(transferFile.toPath());

        this.mockMvc.perform(post("/rest/api/v1/account/transfer")
                        .contentType(APPLICATION_JSON)
                        .content(transferJson))
                .andDo(print())
                .andExpect(status().is5xxServerError())
        ;
    }

    @Test
    void transferPoolMoney_single_success() throws Exception {
        AccountDataModel dataBeforeCall = getAccountData("init/json/transfer_AABB1122.json");
        callToAccountTransfer(dataBeforeCall, "/rest/api/v1/account/transfer/pool");
        Account accountAfter = accountRepository.getAccountBySerial(dataBeforeCall.transfer().getDestAccountSerial()).orElseThrow();
        checkSuccessTransaction(dataBeforeCall.accountBefore(), accountAfter, List.of(dataBeforeCall.transfer()), dataBeforeCall.transactionCountBefore());
    }

    @Test
    void transferPoolMoneyForNonExistentAccount_single_error() throws Exception {

        final File transferFile = new ClassPathResource("init/json/transfer_non_exist_AABB11222.json").getFile();
        String transferJson = Files.readString(transferFile.toPath());

        this.mockMvc.perform(post("/rest/api/v1/account/transfer/pool")
                        .contentType(APPLICATION_JSON)
                        .content(transferJson))
                .andDo(print())
                .andExpect(status().is5xxServerError())
        ;
    }

    @Test
    void transferPoolMoneyNegativeAmount_single_error() throws Exception {

        final File transferFile = new ClassPathResource("init/json/transfer_negative_ammount_AABB1122.json").getFile();
        String transferJson = Files.readString(transferFile.toPath());

        this.mockMvc.perform(post("/rest/api/v1/account/transfer/pool")
                        .contentType(APPLICATION_JSON)
                        .content(transferJson))
                .andDo(print())
                .andExpect(status().is5xxServerError())
        ;
    }

    @Test
    void getAccountDetails_success() throws Exception {

        AccountDataModel accountData = getAccountData("init/json/transfer_AABB1122.json");
        this.mockMvc.perform(get("/rest/api/v1/account/details/" + accountData.transfer().getDestAccountSerial())
                        .contentType(APPLICATION_JSON)
                        .content(accountData.transferJson()))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("serial").value(accountData.accountBefore().getSerial()))
                .andExpect(jsonPath("user-id").exists())
                .andExpect(jsonPath("cur-dig-code").value(accountData.accountBefore().getCurrencyDigitalCode()))
                .andExpect(jsonPath("created-at").exists())
                .andExpect(jsonPath("transactions").exists())
        ;
    }

    @Test
    void getAccountDetailsForNonExistentAccount_error() throws Exception {

        AccountDataModel accountData = getAccountData("init/json/transfer_AABB1122.json");
        this.mockMvc.perform(get("/rest/api/v1/account/details/" + accountData.transfer().getDestAccountSerial() + 1)
                        .contentType(APPLICATION_JSON)
                        .content(accountData.transferJson()))
                .andDo(print())
                .andExpect(status().is5xxServerError())
        ;
    }

    @Test
    void createAccount_success() throws Exception {

        long countBeforeCall = accountRepository.count();
        AccountDto accountDto = AccountDto.builder()
                .serial("YYQQ7711")
                .balance(BigDecimal.ZERO)
                .currencyDigitalCode(804)
                .userUuid(UUID.fromString("60482aae-c95e-4b6d-b1bf-b3d5179d1402"))
                .build();
        String jsonAccount = objectMapper.writeValueAsString(accountDto);

        AccountDataModel accountData = getAccountData("init/json/transfer_AABB1122.json");
        this.mockMvc.perform(post("/rest/api/v1/account")
                        .contentType(APPLICATION_JSON)
                        .content(jsonAccount))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("serial").value(accountDto.getSerial()))
                .andExpect(jsonPath("user-id").value(accountDto.getUserUuid().toString()))
                .andExpect(jsonPath("user-id").exists())
                .andExpect(jsonPath("cur-dig-code").value(accountDto.getCurrencyDigitalCode()))
                .andExpect(jsonPath("created-at").exists())
                .andExpect(jsonPath("transactions").exists())
        ;
        assertThat(accountRepository.count()).isEqualTo(countBeforeCall + 1);
    }

    @Test
    void deleteAccount_success() throws Exception {

        long accountsBefore = accountRepository.count();

        this.mockMvc.perform(delete("/rest/api/v1/account/" + "BBDD2211")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
        ;

        assertThat(accountRepository.count()).isEqualTo(accountsBefore - 1);
    }

    private synchronized AccountDataModel getAccountData(String accountFilePath) throws IOException {
        final File transferFile = new ClassPathResource(accountFilePath).getFile();

        String transferJson = Files.readString(transferFile.toPath());
        TransferReqDto transfer = objectMapper.readValue(transferJson, TransferReqDto.class);
        long transactionCountBefore = transactionRepository.count();
        Account accountBefore = accountRepository.getAccountBySerial(transfer.getDestAccountSerial()).orElseThrow();

        return new AccountDataModel(transferJson, transfer, transactionCountBefore, accountBefore);
    }

    private void callToAccountTransfer(AccountDataModel dataBeforeCall, String pathToTransferEndpoint) throws Exception {
        this.mockMvc.perform(post(pathToTransferEndpoint)
                        .contentType(APPLICATION_JSON)
                        .content(dataBeforeCall.transferJson()))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("message").value("Success"))
                .andExpect(jsonPath("transaction-id").exists());
    }

    private void checkSuccessTransaction(Account accountBeforeCall, Account accountAfterCall, List<TransferReqDto> transfers, long transactionCountBeforeCall) {

        assertThat(transactionRepository.count()).isEqualTo(transactionCountBeforeCall + transfers.size());
        assertThat(accountAfterCall.getOptLockVersion()).isEqualTo(accountBeforeCall.getOptLockVersion() + transfers.size());

        BigDecimal fullTransfersAmount = transfers.stream().map(TransferReqDto::getAmount).reduce(BigDecimal::add).orElseThrow();
        assertThat(accountAfterCall.getBalance()).isEqualTo(accountBeforeCall.getBalance().add(fullTransfersAmount));
    }


}

record AccountDataModel(String transferJson, TransferReqDto transfer, long transactionCountBefore, Account accountBefore) {}