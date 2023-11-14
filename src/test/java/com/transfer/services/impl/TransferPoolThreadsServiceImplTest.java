package com.transfer.services.impl;

import com.transfer.TestUtils;
import com.transfer.config.AppProperties;
import com.transfer.model.dto.TransferReqDto;
import com.transfer.model.dto.TransferRespDto;
import com.transfer.services.TransferService;
import com.transfer.utils.CustomFutureUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.transfer.TestUtils.createTransferReqDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TransferPoolThreadsServiceImplTest {

    @Mock
    private TransferService transferServiceImpl;

    @Mock
    private AppProperties appProperties;

    @InjectMocks
    private TransferPoolThreadsServiceImpl testable;

    private ExecutorService executorServiceMock;
    private ExecutorService[] transfersPool;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(appProperties.getTransfersPoolSize()).thenReturn(10);
        when(appProperties.getPoolTimeOut()).thenReturn(1000);

        testable.init();
    }

    @Test
    public void applyTransfer_SuccessfulTransfer() throws Exception {
        TransferReqDto transferReqDto = createTransferReqDto();
        TransferRespDto expectedResponse = TestUtils.createTransferRespDto();

        executorServiceMock = mock(ExecutorService.class);
        Future<TransferRespDto> futureMock = mock(Future.class);
        when(futureMock.get(anyLong(), any(TimeUnit.class))).thenReturn(expectedResponse);
        when(executorServiceMock.invokeAll(any())).thenReturn(List.of());

        try (MockedStatic<CustomFutureUtils> customFutureUtils = Mockito.mockStatic(CustomFutureUtils.class)) {

            customFutureUtils.when(() -> CustomFutureUtils.getFromFutures(any(), anyLong()))
                    .thenReturn(Optional.of(expectedResponse));
            TransferRespDto actualResponse = testable.applyTransfer(transferReqDto);
            assertEquals(expectedResponse, actualResponse);
        }

    }

    @Test
    public void applyTransfer_ExecutionException_ThrowsRuntimeException() throws Exception {
        TransferReqDto transferReqDto = createTransferReqDto();
        TransferRespDto expectedResponse = TestUtils.createTransferRespDto();

        executorServiceMock = mock(ExecutorService.class);
        Future<TransferRespDto> futureMock = mock(Future.class);
        when(futureMock.get(anyLong(), any(TimeUnit.class))).thenReturn(expectedResponse);
        when(executorServiceMock.invokeAll(any())).thenReturn(List.of());

        try (MockedStatic<CustomFutureUtils> customFutureUtils = Mockito.mockStatic(CustomFutureUtils.class)) {

            customFutureUtils.when(() -> CustomFutureUtils.getFromFutures(any(), anyLong()))
                    .thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> testable.applyTransfer(transferReqDto));
        }

    }
}