/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package com.transfer.services;

import com.transfer.model.dto.UserDto;
import com.transfer.model.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    Optional<User> getUser(UUID uuid);

    UserDto createUser(UserDto userDto);

}
