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
public class AccountDetailsDto {

    @JsonProperty("user-id")
    private UUID userId;

    private String serial;

    private BigDecimal balance;

    @JsonProperty("cur-dig-code")
    private int currencyDigitalCode;

    @JsonProperty("created-at")
    private Date createdAt;

    private int transactions;


}
