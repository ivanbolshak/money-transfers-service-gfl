/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package com.transfer.services.impl;

import com.transfer.exceptions.NoSuchAccountException;
import com.transfer.exceptions.WrongCurrencyException;
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
//step 1 subtract amount from src account
        Account accountSrc = accountService.getAccountBySerial(transferReqDto.getSrcAccountSerial())
                .orElseThrow(() ->
                        new NoSuchAccountException(transferReqDto.getSrcAccountSerial()));
        //checking currency
        if (accountSrc.getCurrencyDigitalCode() != transferReqDto.getCurrencyDigitalCode()) {
            throw new WrongCurrencyException(accountSrc.getCurrencyDigitalCode(), transferReqDto.getCurrencyDigitalCode());
        }

        accountService.subtractAmountFromBalance(transferReqDto.getAmount(), accountSrc);

        Transaction transactionForAccountSrc = customMapper.fromTransferDtoToNewTransactionEntity(transferReqDto, accountSrc);
        Transaction persistenceTransactionSrc = transactionService.saveNew(transactionForAccountSrc);

        persistenceTransactionSrc.setAccount(accountSrc);

// Step 2 add amount to dest account
        Account accountDest = accountService.getAccountBySerial(transferReqDto.getDestAccountSerial())
                .orElseThrow(() ->
                        new NoSuchAccountException(transferReqDto.getDestAccountSerial()));

        //checking currency
        if (accountDest.getCurrencyDigitalCode() != transferReqDto.getCurrencyDigitalCode()) {
            throw new WrongCurrencyException(accountDest.getCurrencyDigitalCode(), transferReqDto.getCurrencyDigitalCode());
        }

        Transaction transactionForAccountDest = customMapper.fromTransferDtoToNewTransactionEntity(transferReqDto, accountDest);

        //checking amount in dest and src persistence transaction entity
        if (!transactionForAccountsHaveTheSameAmount(transactionForAccountSrc, transactionForAccountDest)) {
            throw new IllegalArgumentException("Persistence Transactions have different amount");
        }

        Transaction persistenceTransactionDest = transactionService.saveNew(transactionForAccountDest);
        persistenceTransactionDest.setAccount(accountDest);

        accountService.addAmountToBalance(persistenceTransactionSrc.getAmount(), accountDest);

        log.debug("Trying to update balance  for transferReqDto: {}", transferReqDto);
        return TransferRespDto.builder()
                .message("Success")
                .transactionIdSrc(transactionForAccountSrc.getUuid())
                .transactionIdDest(transactionForAccountDest.getUuid())
                .build();
    }

    private boolean transactionForAccountsHaveTheSameAmount(Transaction transactionForAccountSrc, Transaction transactionForAccountDest) {
        return transactionForAccountSrc.getAmount().compareTo(transactionForAccountDest.getAmount()) == 0;
    }
}
