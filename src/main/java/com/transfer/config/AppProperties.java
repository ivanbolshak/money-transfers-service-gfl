/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package com.transfer.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "app-transfer")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AppProperties {

    @JsonProperty("transfers-pool-size")
    private Integer transfersPoolSize;

    @JsonProperty("pool-time-out")
    private Integer poolTimeOut;

}
