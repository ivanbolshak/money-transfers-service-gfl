/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package com.transfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transfer.model.dto.TransferReqDto;
import com.transfer.model.dto.TransferRespDto;
import com.transfer.model.entity.Account;
import com.transfer.model.entity.Transaction;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

public class TestUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private TestUtils() {
    }

    public static TransferReqDto createTransferReqDto() throws IOException {
        final File transferFile = new ClassPathResource("init/json/transfer_AABB1122.json").getFile();

        String transferJson = Files.readString(transferFile.toPath());
        TransferReqDto transfer = objectMapper.readValue(transferJson, TransferReqDto.class);

        return transfer;
    }

    public static Account createAccount() throws IOException {
        final File accountFile = new ClassPathResource("init/json/account_entity_AABB1122.json").getFile();

        String accountJson = Files.readString(accountFile.toPath());
        Account account = objectMapper.readValue(accountJson, Account.class);

        return account;
    }

    public static Transaction createTransaction() throws IOException {

        final File accountFile = new ClassPathResource("init/json/transaction_entity_1.json").getFile();

        String transactionJson = Files.readString(accountFile.toPath());
        Transaction transaction = objectMapper.readValue(transactionJson, Transaction.class);

        return transaction;

    }

    public static TransferRespDto createTransferRespDto() {
        return TransferRespDto.builder()
                .message("Sucess")
                .transactionId(UUID.randomUUID())
                .build();
    }
}
