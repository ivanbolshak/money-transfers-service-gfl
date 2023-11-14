/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package com.transfer.model.concurrent;

import com.transfer.model.dto.TransferReqDto;
import com.transfer.model.dto.TransferRespDto;
import com.transfer.services.TransferService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

@Slf4j
public class TransferCall implements Callable<TransferRespDto> {

    private TransferService transferService;
    private TransferReqDto transferReqDto;

    public TransferCall(TransferService transferService, TransferReqDto transferReqDto) {
        this.transferService = transferService;
        this.transferReqDto = transferReqDto;
    }

    @Override
    public TransferRespDto call() throws Exception {

        log.debug("Call from pool. Thread name: {}, transferReqDto: {}", Thread.currentThread().getName(), transferReqDto);
        TransferRespDto transferRespDto = transferService.applyTransfer(transferReqDto);
        log.debug("Result from pool. Thread name: {}, transferRespDto: {}", Thread.currentThread().getName(), transferRespDto);
        return transferRespDto;
    }
}
