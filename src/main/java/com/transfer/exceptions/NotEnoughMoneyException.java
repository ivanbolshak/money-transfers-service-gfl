
package com.transfer.exceptions;

import java.math.BigDecimal;

public class NotEnoughMoneyException extends RuntimeException{

    private static final String NOT_ENOUGHT_BALANCE_MESSAGE = "The amount on the resource account %s is not enough to withdraw: %s";

    public NotEnoughMoneyException(String srcAccountSerial, BigDecimal amount) {
        super(String.format(
                NOT_ENOUGHT_BALANCE_MESSAGE, srcAccountSerial, amount));
    }
}
