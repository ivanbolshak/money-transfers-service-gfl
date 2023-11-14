package com.transfer.services.impl;

import com.transfer.model.entity.Account;
import com.transfer.repository.AccountRepository;
import com.transfer.services.AccountService;
import com.transfer.services.UserService;
import com.transfer.utils.CustomMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
class AccountServiceImplTest {

    @InjectMocks
    private AccountService testable;

    private AccountRepository accountRepository;

    private CustomMapper customMapper;

    private UserService userService;

    @BeforeEach
    public void init() {
        userService = mock(UserServiceImpl.class);
        customMapper = mock(CustomMapper.class);
        accountRepository = mock(AccountRepository.class);
        testable = new AccountServiceImpl(accountRepository, customMapper, userService);
    }


    @Test
    void addTransferAmountToCurrentBalance_testNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> testable.addTransferAmountToCurrentBalance(BigDecimal.valueOf(-4L), "tttt"));
        assertThrows(IllegalArgumentException.class, () -> testable.addTransferAmountToCurrentBalance(BigDecimal.valueOf(-4L), new Account()));
    }


}