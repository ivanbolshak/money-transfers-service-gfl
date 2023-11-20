/*
 * This file is a subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */

package com.transfer.services.impl;

import com.transfer.config.AppProperties;
import com.transfer.model.concurrent.TransferCall;
import com.transfer.model.dto.TransferReqDto;
import com.transfer.model.dto.TransferRespDto;
import com.transfer.services.TransferService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.transfer.utils.CustomFutureUtils.getFromFutures;

@Slf4j
@Service
public class TransferPoolThreadsServiceImpl implements TransferService {

    TransferService transferServiceImpl;
    private AppProperties appProperties;
    private ExecutorService[] transfersPool;

    private static Long poolTimeOutMilliseconds;

    public TransferPoolThreadsServiceImpl(AppProperties appProperties, TransferService transferServiceImpl) {
        this.transferServiceImpl = transferServiceImpl;
        this.appProperties = appProperties;
        poolTimeOutMilliseconds = Long.valueOf(appProperties.getPoolTimeOut());
    }

    @PostConstruct
    public void init() {
        transfersPool = new ExecutorService[appProperties.getTransfersPoolSize()];

        for (int i = 0; i < transfersPool.length; i++) {
            transfersPool[i] = Executors.newFixedThreadPool(1);
        }
    }

    public TransferRespDto applyTransfer(TransferReqDto transferReqDto) {

        int pos = Math.abs(transferReqDto.getSrcAccountSerial().hashCode()) % (transfersPool.length - 1);
        log.debug("Position in arr: {}, arr.size: {}, transferReqDto: {}", pos, transfersPool.length, transferReqDto);

        List<Future<TransferRespDto>> transferResponse;
        try {
            log.debug("Invoke transfersPool");
            transferResponse = transfersPool[pos].invokeAll(List.of(new TransferCall(transferServiceImpl, transferReqDto)));
        } catch (InterruptedException e) {
            log.error("Failed transfer pool execution for transferReqDto: {}, Reason: {}", transferReqDto, e.getMessage());
            throw new RuntimeException(e);
        }

        TransferRespDto transferRespDto = transferResponse.stream().flatMap(f -> getFromFutures(f, poolTimeOutMilliseconds).stream()).findFirst().orElseThrow();
        log.debug("Transfer applied, transferRespDto: {}", transferRespDto);
        return transferRespDto;
    }



}
