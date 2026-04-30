package com.interswitch.fraudtransactionapp.controller;

import com.interswitch.fraudtransactionapp.config.RateLimiter;
import com.interswitch.fraudtransactionapp.dto.request.TransactionRequest;
import com.interswitch.fraudtransactionapp.dto.response.ApiResponse;
import com.interswitch.fraudtransactionapp.model.FraudDecision;
import com.interswitch.fraudtransactionapp.service.FraudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fraud")
@RequiredArgsConstructor
@Tag(
        name = "Fraud",
        description = "Endpoints for processing transactions and performing fraud checks"
)
public class FraudController {

    private final FraudService fraudService;
    private final RateLimiter rateLimiter;

    @PostMapping("/process")
    @Operation(
            summary = "Process transaction for fraud",
            description = "Runs fraud checks on the incoming transaction request and returns a fraud decision. "
                    + "Requests are rate-limited per IP address."
    )
    public ResponseEntity<ApiResponse<FraudDecision>> processTransaction(
            @Valid @RequestBody TransactionRequest request
            // You can also document headers here if you later re-enable X-Forwarded-For
            // @RequestHeader("X-Forwarded-For") String ipHeader
    ) {
        // String ip = ipHeader != null ? ipHeader.split(\",\")[0].trim() : request.getIpAddress();
        String ip = request.getIpAddress();

        if (!rateLimiter.isAllowedByIp(ip)) {
            return ResponseEntity.status(429)
                    .body(ApiResponse.error(
                            String.format("Too many requestion within a minute for IP: %s", ip)
                    ));
        }

        FraudDecision decision = fraudService.process(request);

        return ResponseEntity.ok(ApiResponse.success(
                "Fraud Check Completed",
                decision
        ));
    }

}