package com.dws.challenge.web;

import com.dws.challenge.payloads.TransferRequest;
import com.dws.challenge.payloads.TransferResponse;
import com.dws.challenge.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/transfer")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<TransferResponse> transfer(@Valid @RequestBody TransferRequest transferRequest) {
        transferService.processTransfer(transferRequest.getAccountFrom(),
                transferRequest.getAccountTo(),
                transferRequest.getAmount());

        return ResponseEntity.status(HttpStatus.OK).body(new TransferResponse("Transfer of "+transferRequest.getAmount()+" from account "+transferRequest.getAccountFrom()+" to account "+transferRequest.getAccountTo()+" completed successfully"));
    }
}