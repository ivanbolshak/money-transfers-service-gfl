package com.transfer.services;

import com.transfer.model.dto.AccountDetailsDto;
import com.transfer.model.dto.AccountDto;
import com.transfer.model.entity.Account;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountService {

    Optional<Account> getAccountBySerial(String serial);

    AccountDetailsDto getAccountDetails(String serial);

    @Transactional
    void subtractAmountFromBalance(BigDecimal amount, String accountSerial);

    @Transactional
    void subtractAmountFromBalance(BigDecimal amount, Account accountSerial);

    void addAmountToBalance(BigDecimal amount, String accountSerial);

    public void addAmountToBalance(BigDecimal amount, Account account);

    AccountDetailsDto createAccount(AccountDto accountDto);

    void deleteAccountBySerial(String serial);
}
