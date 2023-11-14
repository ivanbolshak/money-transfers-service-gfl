/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package com.transfer.services.impl;

import com.transfer.model.dto.UserDto;
import com.transfer.model.entity.User;
import com.transfer.repository.UserRepository;
import com.transfer.services.UserService;
import com.transfer.utils.CustomMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private CustomMapper customMapper;

    public Optional<User> getUser(UUID uuid) {
        return userRepository.findUserByUuid(uuid);
    }

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        log.debug("Start create user. userDto: {}", userDto);
        User user = customMapper.fromUserDtoToNewUserEntity(userDto);
        User psersistanceUser = userRepository.save(user);
        log.debug("User creation complete. psersistanceUser: {}", psersistanceUser);
        return customMapper.fromUserEntityToUserDto(psersistanceUser);
    }


}
