package com.transfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfer.model.dto.AccountDto;
import com.transfer.repository.AccountRepository;
import com.transfer.repository.TransactionRepository;
import com.transfer.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = "test")
@AutoConfigureMockMvc
class MoneyTransfersServiceGflApplicationTests {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void contextLoadsTestInitDto() throws IOException {

        final File jsonFile = new ClassPathResource("init/json/account_save_XXYY7799.json").getFile();
        AccountDto account = objectMapper.readValue(jsonFile, AccountDto.class);
        assertThat(account).matches(a ->
                "XXYY7799".equals(a.getSerial())
                        && 840 == a.getCurrencyDigitalCode()
                        && BigDecimal.valueOf(0L).equals(a.getBalance())
                        && "f5b71c77-157e-40aa-9c8d-d02426984d3b".equals(a.getUserUuid().toString()));

    }

    @Test
    void testDbInit() {
        assertThat(accountRepository.count()).isGreaterThan(0);
        assertThat(userRepository.count()).isGreaterThan(0);
        assertThat(transactionRepository.count()).isGreaterThan(0);
    }

}
