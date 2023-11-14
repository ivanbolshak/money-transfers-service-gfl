/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package com.transfer.utils;

import com.transfer.model.dto.AccountDetailsDto;
import com.transfer.model.dto.AccountDto;
import com.transfer.model.dto.TransferReqDto;
import com.transfer.model.dto.UserDto;
import com.transfer.model.entity.Account;
import com.transfer.model.entity.Transaction;
import com.transfer.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class CustomMapper {

    private ModelMapper modelMapper;

    public Transaction fromTransferDtoToNewTransactionEntity(TransferReqDto transferReqDto, Account account) {
        log.debug("Start convert fromTransferDtoToNewTransactionEntity. transferReqDto: {}, account: {}", transferReqDto, account);
        Transaction transaction = modelMapper.map(transferReqDto, Transaction.class);
        transaction.setAccount(account);
        log.debug("Complete convert fromTransferDtoToNewTransactionEntity. Result transaction: {}", transaction);
        return transaction;
    }

    public AccountDetailsDto fromAccountEntityToAccountDetailsDto(Account account) {
        log.debug("Start convert fromAccountEntityToAccountDetailsDto. account: {}", account);
        return AccountDetailsDto.builder()
                .userId(account.getUser().getUuid())
                .serial(account.getSerial())
                .balance(account.getBalance())
                .currencyDigitalCode(account.getCurrencyDigitalCode())
                .createdAt(account.getCreatedAt())
                .transactions(account.getTransactions() == null ? 0 : account.getTransactions().size())
                .build();
    }

    public Account fromAccountDtoToNewAccountEntity(AccountDto accountDto, User user) {
        log.debug("Start convert fromAccountDtoToNewAccountEntity. accountDto: {}, user: {}", accountDto, user);
        Account accountEntity = modelMapper.map(accountDto, Account.class);
        accountEntity.setUser(user);
        log.debug("Complete convert fromAccountDtoToNewAccountEntity. Result accountEntity: {}", accountEntity);
        return accountEntity;
    }

    public User fromUserDtoToNewUserEntity(UserDto userDto) {
        log.debug("Start convert fromUserDtoToNewUserEntity. userDto: {}", userDto);
        User userentity = modelMapper.map(userDto, User.class);
        if (userDto.getUuid() == null) {
            userentity.setUuid(UUID.randomUUID());
        }
        log.debug("Complete convert fromUserDtoToNewUserEntity. userentity: {}", userentity);
        return userentity;
    }

    public UserDto fromUserEntityToUserDto(User userEntity) {
        log.debug("Start convert fromUserEntityToUserDto. userEntity: {}", userEntity);
        UserDto userDto = modelMapper.map(userEntity, UserDto.class);
        log.debug("Complete convert fromUserEntityToUserDto. userDto: {}", userDto);
        return userDto;
    }
}
