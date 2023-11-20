
package com.transfer.exceptions;


public class WrongCurrencyException extends RuntimeException{

    private static final String WRONG_CURRENCY = "Transaction got wrong currency. Expected: %s, Actual: %s";

    public WrongCurrencyException(int expectedCurrency, int actualCurrency) {
        super(String.format(
                WRONG_CURRENCY, expectedCurrency, actualCurrency));
    }
}
