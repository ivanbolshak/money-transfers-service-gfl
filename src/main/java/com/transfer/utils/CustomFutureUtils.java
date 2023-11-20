/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package com.transfer.utils;

import com.transfer.exceptions.NoSuchAccountException;
import com.transfer.exceptions.NotEnoughMoneyException;
import com.transfer.exceptions.WrongCurrencyException;
import com.transfer.model.dto.TransferRespDto;

import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class CustomFutureUtils {
    private CustomFutureUtils() {
    }

    public static Optional<TransferRespDto> getFromFutures(Future<TransferRespDto> f, Long timeOut) {
        try {
            return Optional.of(f.get(timeOut, TimeUnit.MILLISECONDS));
        } catch (IllegalArgumentException | NoSuchAccountException | NotEnoughMoneyException | WrongCurrencyException ex) {
            throw  new IllegalArgumentException("IllegalArgumentException got. Message: {}" + ex.getMessage());
        } catch (Exception e) {
            throw  new IllegalArgumentException("Failed to get from futures. Reason: {}" + e.getMessage());
        }
    }

}
