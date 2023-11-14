/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package com.transfer.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @NotNull
    private UUID uuid;

    @Email
    @NotNull
    private String email;

    @NotEmpty
    @JsonProperty("first-name")
    private String firstName;

    @NotEmpty
    @JsonProperty("last-name")
    private String lastName;

    @NotNull
    @JsonProperty("created-at")
    private Date createdAt;

    private int accounts;

}
