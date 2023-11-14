/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package com.transfer.contoller;

import com.transfer.model.dto.AccountDetailsDto;
import com.transfer.model.dto.AccountDto;
import com.transfer.model.dto.TransferReqDto;
import com.transfer.model.dto.TransferRespDto;
import com.transfer.services.AccountService;
import com.transfer.services.TransferService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/api/v1/account")
public class AccountController {

    private TransferService transferService;
    private TransferService transferPoolThreadsService;
    private AccountService accountService;

    public AccountController(TransferService transferService,
                             @Qualifier("transferPoolThreadsServiceImpl") TransferService transferPoolThreadsService,
                             AccountService accountService) {
        this.transferService = transferService;
        this.transferPoolThreadsService = transferPoolThreadsService;
        this.accountService = accountService;
    }

    @PostMapping("/transfer")
    public TransferRespDto transferMoney(@Validated @RequestBody TransferReqDto transferReqDto) {
        return transferService.applyTransfer(transferReqDto);
    }

    @PostMapping("/transfer/pool")
    public TransferRespDto transferPoolMoney(@Validated @RequestBody TransferReqDto transferReqDto) {
        return transferPoolThreadsService.applyTransfer(transferReqDto);
    }

    @GetMapping("/details/{serial}")
    public AccountDetailsDto getAccountDetails(@PathVariable(name = "serial") String serial) {
        return accountService.getAccountDetails(serial);
    }

    @PostMapping
    public AccountDetailsDto createAccount(@RequestBody AccountDto accountDto) {
        return accountService.createAccount(accountDto);
    }

    @DeleteMapping("/{serial}")
    public void deleteAccount(@PathVariable(name = "serial") String serial) {
        accountService.deleteAccountBySerial(serial);
    }

}
