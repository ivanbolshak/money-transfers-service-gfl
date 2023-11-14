
/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package com.transfer.services.impl;

import com.transfer.model.dto.AccountDetailsDto;
import com.transfer.model.dto.AccountDto;
import com.transfer.model.entity.Account;
import com.transfer.model.entity.User;
import com.transfer.repository.AccountRepository;
import com.transfer.services.AccountService;
import com.transfer.services.UserService;
import com.transfer.utils.CustomMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

@AllArgsConstructor
@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;
    private CustomMapper customMapper;
    private UserService userService;

    @Override
    public Optional<Account> getAccountBySerial(String serial) {
        return accountRepository.getAccountBySerial(serial);
    }

    @Override
    public AccountDetailsDto getAccountDetails(String serial) {
        Account account = this.getAccountBySerial(serial).orElseThrow();
        return customMapper.fromAccountEntityToAccountDetailsDto(account);
    }

    @Transactional
    @Override
    public void addTransferAmountToCurrentBalance(BigDecimal amount, String accountSerial) {
        amountIsNotNegativeValidation(amount);

        Account account = accountRepository.getAccountBySerial(accountSerial).orElseThrow(() -> new NoSuchElementException(String.format("No such account serial ( %s ) in db", accountSerial)));
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
    }

    @Transactional
    @Override
    public void addTransferAmountToCurrentBalance(BigDecimal amount, Account account) {
        amountIsNotNegativeValidation(amount);

        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
    }

    private void amountIsNotNegativeValidation(BigDecimal amount) {
        if (BigDecimal.ZERO.compareTo(amount) > 0) {
            throw new IllegalArgumentException(String.format("Transfer amount shouldn't be negative. amount: %s", amount));
        }
    }

    @Transactional
    @Override
    public AccountDetailsDto createAccount(AccountDto accountDto) {
        log.debug("Creating account. accountDto: {}", accountDto);
        User user = userService.getUser(accountDto.getUserUuid()).orElseThrow();
        Account account = customMapper.fromAccountDtoToNewAccountEntity(accountDto, user);
        Account persistanceAccount = accountRepository.save(account);
        log.debug("Completed account. persistanceAccount: {}", persistanceAccount);
        return customMapper.fromAccountEntityToAccountDetailsDto(persistanceAccount);
    }

    @Transactional
    @Override
    public void deleteAccountBySerial(String serial) {
        log.info("Soft delete account with serial: {}", serial);
        Account account = accountRepository.getAccountBySerial(serial).orElseThrow();
        accountRepository.delete(account);
        log.info("Account with serial: {} deleted (soft)", serial);
    }


}
