package com.transfer.contoller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfer.model.dto.TransferReqDto;
import com.transfer.model.entity.Account;
import com.transfer.repository.AccountRepository;
import com.transfer.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
import java.util.NoSuchElementException;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "test")
@AutoConfigureMockMvc
class AccountControllerTransferMultiThreadingTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @ParameterizedTest
    @ValueSource(ints = {10, 100, 1000})
    void transferMoney_multi_thread_success(int poolSize) throws Exception {

        int taskTimeOutSeconds = 1;

        AccountDataModel dataBeforeCall = getAccountData("init/json/transfer_AABB1122.json");
        String pathToTransferEndpointh = "/rest/api/v1/account/transfer";
        String transferJson = dataBeforeCall.transferJson();

        threadPoolExecutor(pathToTransferEndpointh, transferJson, poolSize, taskTimeOutSeconds);

        Account accountAfter = accountRepository.getAccountBySerial(dataBeforeCall.transfer().getDestAccountSerial()).orElseThrow();
        checkSuccessTransaction(dataBeforeCall.accountBefore(), accountAfter, dataBeforeCall.transfer(), poolSize, dataBeforeCall.transactionCountBefore());
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 100, 1000})
    void transferPoolMoney_multi_thread_success(int poolSize) throws Exception {

        int taskTimeOutSeconds = 1;

        AccountDataModel dataBeforeCall = getAccountData("init/json/transfer_AABB1122.json");
        String pathToTransferEndpointh = "/rest/api/v1/account/transfer/pool";
        String transferJson = dataBeforeCall.transferJson();

        threadPoolExecutor(pathToTransferEndpointh, transferJson, poolSize, taskTimeOutSeconds);

        Account accountAfter = accountRepository.getAccountBySerial(dataBeforeCall.transfer().getDestAccountSerial()).orElseThrow();
        checkSuccessTransaction(dataBeforeCall.accountBefore(), accountAfter, dataBeforeCall.transfer(), poolSize, dataBeforeCall.transactionCountBefore());
    }

    private void threadPoolExecutor(String pathToTransferEndpointh, String transferJson, int poolSize, int taskTimeOutSeconds) {
        List<TransferExecutor> tasks = IntStream.range(0, poolSize).mapToObj(o -> new TransferExecutor(pathToTransferEndpointh, transferJson)).toList();
        ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);

        int sizeOfFailedTransfers = 0;
        try {
            long start = System.currentTimeMillis();
            List<Future<Boolean>> futures = threadPool.invokeAll(tasks);
            sizeOfFailedTransfers = futures.stream().map(f -> {
                try {
                    return f.get(taskTimeOutSeconds, TimeUnit.SECONDS);
                } catch (Exception e) {
                    log.error("Failed to get from futures. Reason: " + e.getMessage());
                    return false;
                }
            }).filter(b -> !b).toList().size();
            log.info("Duration threadPool.invokeAll(tasks): " + (System.currentTimeMillis() - start));
        } catch (InterruptedException e) {
            log.error("Error threadPool.invokeAll tasks. Reason: {}", e.getMessage(), e);
        }

        assertThat(sizeOfFailedTransfers).isZero();
    }

    private synchronized AccountDataModel getAccountData(String accountFilePath) throws IOException {
        final File transferFile = new ClassPathResource(accountFilePath).getFile();

        String transferJson = Files.readString(transferFile.toPath());
        TransferReqDto transfer = objectMapper.readValue(transferJson, TransferReqDto.class);
        long transactionCountBefore = transactionRepository.count();
        Account accountBefore = accountRepository.getAccountBySerial(transfer.getDestAccountSerial()).orElseThrow(() ->
                new NoSuchElementException(String.format("Error for accountFilePath: %s,  accountRepository: %s", accountFilePath, accountRepository.findAll())));

        return new AccountDataModel(transferJson, transfer, transactionCountBefore, accountBefore);
    }


    private void checkSuccessTransaction(Account accountBeforeCall, Account accountAfterCall, TransferReqDto transfer, int poolSize, long transactionCountBeforeCall) {

        assertThat(transactionRepository.count()).isEqualTo(transactionCountBeforeCall + poolSize);
        assertThat(accountAfterCall.getOptLockVersion()).isEqualTo(accountBeforeCall.getOptLockVersion() + poolSize);

        BigDecimal fullTransfersAmount = transfer.getAmount().multiply(BigDecimal.valueOf(poolSize));
        assertThat(accountAfterCall.getBalance()).isEqualTo(accountBeforeCall.getBalance().add(fullTransfersAmount));
    }

    class TransferExecutor implements Callable<Boolean> {

        private String pathToTransferEndpointh;
        private String transferJson;

        public TransferExecutor(String pathToTransferEndpointh, String transferJson) {
            this.pathToTransferEndpointh = pathToTransferEndpointh;
            this.transferJson = transferJson;
        }

        @Override
        public Boolean call() throws Exception {
            try {
                log.info("Start TransferExecutor thread name: {}, Current time: {}", Thread.currentThread().getName(), System.currentTimeMillis());
                transferCallSingle();
                log.info("Finish TransferExecutor thread name: {}, Current time: {}", Thread.currentThread().getName(), System.currentTimeMillis());
                return true;
            } catch (Exception e) {
                log.error("Error call transfer. Tread name: {}, Reason: {}",
                        Thread.currentThread().getName(), e.getMessage(), e);
                return false;
            }
        }

        private void transferCallSingle() throws Exception {
            mockMvc.perform(post(pathToTransferEndpointh)
                            .contentType(APPLICATION_JSON)
                            .content(transferJson))
                    .andDo(print())
                    .andExpect(status().is2xxSuccessful())
                    .andExpect(content().contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("message").value("Success"))
                    .andExpect(jsonPath("transaction-id").exists());
        }


    }

}