/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package com.transfer.services.impl;

import com.transfer.model.entity.Transaction;
import com.transfer.repository.TransactionRepository;
import com.transfer.services.TransactionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    private TransactionRepository transactionRepository;

    @Override
    public Transaction saveNew(Transaction transaction) {
        log.debug("Save new transaction. transaction: {}", transaction);
        Transaction persistTransaction = transactionRepository.save(transaction);
        log.debug("Transaction saved. persistTransaction: {}", persistTransaction);
        return persistTransaction;
    }
}
