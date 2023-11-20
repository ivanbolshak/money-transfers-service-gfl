package com.transfer.services.impl;

import com.transfer.exceptions.NoSuchAccountException;
import com.transfer.exceptions.WrongCurrencyException;
import com.transfer.model.dto.TransferReqDto;
import com.transfer.model.dto.TransferRespDto;
import com.transfer.model.entity.Account;
import com.transfer.model.entity.Transaction;
import com.transfer.services.AccountService;
import com.transfer.services.TransactionService;
import com.transfer.utils.CustomMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Optional;

import static com.transfer.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransferServiceImplTest {

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private CustomMapper customMapper;

    @InjectMocks
    private TransferServiceImpl transferService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void applyTransfer_SuccessfulTransfer() throws IOException {
        TransferReqDto transferReqDto = createTransferReqDto();
        Account account = createAccount();
        Transaction transaction = createTransaction();

        when(accountService.getAccountBySerial(any()))
                .thenReturn(Optional.of(account));
        when(customMapper.fromTransferDtoToNewTransactionEntity(transferReqDto, account))
                .thenReturn(transaction);
        when(transactionService.saveNew(transaction)).thenReturn(transaction);

        TransferRespDto response = transferService.applyTransfer(transferReqDto);

        assertEquals("Success", response.getMessage());
        verify(accountService).addAmountToBalance(transaction.getAmount(), account);
    }

    @Test
    public void applyTransfer_AccountNotFound_ThrowsIllegalArgumentException() throws IOException {
        TransferReqDto transferReqDto = createTransferReqDto();
        when(accountService.getAccountBySerial(transferReqDto.getDestAccountSerial()))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchAccountException.class, () -> transferService.applyTransfer(transferReqDto));
    }

    @Test
    public void applyTransfer_WrongCurrency_ThrowsIllegalArgumentException() throws IOException {
        TransferReqDto transferReqDto = createTransferReqDto();
        Account account = createAccount();
        account.setCurrencyDigitalCode(111);

        when(accountService.getAccountBySerial(transferReqDto.getDestAccountSerial()))
                .thenReturn(Optional.of(account));

        when(accountService.getAccountBySerial(transferReqDto.getSrcAccountSerial()))
                .thenReturn(Optional.of(account));

        assertThrows(WrongCurrencyException.class, () -> transferService.applyTransfer(transferReqDto));
    }

}