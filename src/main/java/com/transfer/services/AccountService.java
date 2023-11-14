package com.transfer.services;

import com.transfer.model.dto.AccountDetailsDto;
import com.transfer.model.dto.AccountDto;
import com.transfer.model.entity.Account;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountService {

    Optional<Account> getAccountBySerial(String serial);

    AccountDetailsDto getAccountDetails(String serial);

    void addTransferAmountToCurrentBalance(BigDecimal amount, String accountSerial);

    public void addTransferAmountToCurrentBalance(BigDecimal amount, Account account);

    AccountDetailsDto createAccount(AccountDto accountDto);

    void deleteAccountBySerial(String serial);
}
