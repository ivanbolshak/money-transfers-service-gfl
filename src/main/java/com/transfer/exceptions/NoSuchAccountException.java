
package com.transfer.exceptions;

public class NoSuchAccountException extends RuntimeException{

    private static final String NO_SUCH_ACCOUNT = "No such account serial ( %s ) in db";

    public NoSuchAccountException(String accountSerial) {
        super(String.format(NO_SUCH_ACCOUNT, accountSerial));
    }

}
