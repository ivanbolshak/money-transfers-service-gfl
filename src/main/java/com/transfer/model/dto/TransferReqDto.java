/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package com.transfer.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransferReqDto {

    @NotEmpty
    @JsonProperty("src-account-serial")
    private String srcAccountSerial;

    @NotEmpty
    @JsonProperty("dest-account-serial")
    private String destAccountSerial;

    @NotNull
    private BigDecimal amount;

    @NotNull
    @JsonProperty("cur-dig-code")
    private int currencyDigitalCode;

}
