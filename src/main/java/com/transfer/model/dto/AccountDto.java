/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package com.transfer.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;


@Data
@Builder
public class AccountDto {

    private String serial;

    private BigDecimal balance;

    private int currencyDigitalCode;

    @JsonProperty("user-uuid")
    private UUID userUuid;

    private Date createdAt;

    private int transactions;


}
