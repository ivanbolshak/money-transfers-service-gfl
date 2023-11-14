package com.transfer.services;

import com.transfer.model.dto.TransferReqDto;
import com.transfer.model.dto.TransferRespDto;

public interface TransferService {

    TransferRespDto applyTransfer(TransferReqDto transferReqDto);

}
