/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package com.transfer.services.impl;

import com.transfer.model.dto.TransferReqDto;
import com.transfer.model.dto.TransferRespDto;
import com.transfer.model.entity.Account;
import com.transfer.model.entity.Transaction;
import com.transfer.services.AccountService;
import com.transfer.services.TransactionService;
import com.transfer.services.TransferService;
import com.transfer.utils.CustomMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.context.annotation.Primary;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Slf4j
@Service
@Primary
public class TransferServiceImpl implements TransferService {

    private AccountService accountService;
    private TransactionService transactionService;
    private CustomMapper customMapper;


    @Override
    @Transactional
    @Retryable(retryFor = StaleObjectStateException.class, maxAttemptsExpression = "${retry.transfer.maxAttempts}")
    public TransferRespDto applyTransfer(TransferReqDto transferReqDto) {

        Account account = accountService.getAccountBySerial(transferReqDto.getDestAccountSerial())
                .orElseThrow(() ->
                        new IllegalArgumentException(String.format("Current dest-account-serial ( %s ) absent in DB", transferReqDto.getDestAccountSerial())));

        if (account.getCurrencyDigitalCode() != transferReqDto.getCurrencyDigitalCode()) {
            throw new IllegalArgumentException(String.format("Transaction got wrong currency. Expected: %s, Actual: %s", account.getCurrencyDigitalCode(), transferReqDto.getCurrencyDigitalCode()));
        }

        Transaction transaction = customMapper.fromTransferDtoToNewTransactionEntity(transferReqDto, account);
        Transaction persistanceTransaction = transactionService.saveNew(transaction);
        persistanceTransaction.setAccount(account);

        accountService.addTransferAmountToCurrentBalance(persistanceTransaction.getAmount(), account);

        log.debug("Trying to update balance  for transferReqDto: {}", transferReqDto);
        return TransferRespDto.builder()
                .message("Success")
                .transactionId(persistanceTransaction.getUuid())
                .build();
    }
}
