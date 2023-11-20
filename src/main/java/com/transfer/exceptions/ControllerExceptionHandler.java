/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package com.transfer.exceptions;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {IllegalArgumentException.class,
            IllegalStateException.class,
            NoSuchElementException.class,
            NoSuchAccountException.class,
            NotEnoughMoneyException.class,
            WrongCurrencyException.class})
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = String.format("Error processing request. Reason: %s", ex.getMessage());
        log.error(bodyOfResponse);
        ex.printStackTrace();
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }


}
