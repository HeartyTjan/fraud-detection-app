package com.interswitch.fraudtransactionapp.controller;

import com.interswitch.fraudtransactionapp.dto.response.ApiResponse;
import com.interswitch.fraudtransactionapp.dto.response.BlacklistResponse;
import com.interswitch.fraudtransactionapp.model.Transactions;
import com.interswitch.fraudtransactionapp.service.BlacklistService;
import com.interswitch.fraudtransactionapp.service.FlaggedTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin operations for flagged transactions and blacklist management")
public class AdminController {

    private final FlaggedTransactionService flaggedTransactionService;
    private final BlacklistService blacklistService;

//    @PreAuthorize("hasAuthority('VIEW_FLAGGED_TRANSACTIONS')")
//    @GetMapping("/flagged")
//    @Operation(
//            summary = "Get all flagged transactions",
//            description = "Returns all transactions that have been flagged by the fraud engine"
//    )
//    public ResponseEntity<ApiResponse<List<Transactions>>> getAllFlagged() {
//        return ResponseEntity.ok(
//                ApiResponse.success("All flagged transactions", flaggedTransactionService.getAllFlagged())
//        );
//    }

    @PreAuthorize("hasAuthority('VIEW_FLAGGED_TRANSACTIONS')")
    @GetMapping("/flagged/blocked")
    @Operation(
            summary = "Get blocked transactions",
            description = "Returns transactions that have been flagged and blocked"
    )
    public ResponseEntity<ApiResponse<List<Transactions>>> getBlocked() {
        return ResponseEntity.ok(
                ApiResponse.success("Blocked transactions", flaggedTransactionService.getBlocked())
        );
    }

    @PreAuthorize("hasAuthority('VIEW_FLAGGED_TRANSACTIONS')")
    @GetMapping("/flagged/review")
    @Operation(
            summary = "Get transactions under review",
            description = "Returns transactions that require manual review"
    )
    public ResponseEntity<ApiResponse<List<Transactions>>> getReview() {
        return ResponseEntity.ok(
                ApiResponse.success("Review transactions", flaggedTransactionService.getReview())
        );
    }

    @PreAuthorize("hasAuthority('VIEW_FLAGGED_TRANSACTIONS')")
    @GetMapping("/flagged/ip/{ip}")
    @Operation(
            summary = "Get flagged transactions by IP",
            description = "Returns flagged transactions associated with a specific IP address"
    )
    public ResponseEntity<ApiResponse<List<Transactions>>> getFlaggedByIp(
            @Parameter(description = "IP address to search for", example = "192.168.1.1")
            @PathVariable String ip) {
        return ResponseEntity.ok(
                ApiResponse.success("Flagged transactions by IP", flaggedTransactionService.getFlaggedByIp(ip))
        );
    }


    @PreAuthorize("hasAuthority('VIEW_FLAGGED_TRANSACTIONS')")
    @GetMapping("/flagged")
    @Operation(
            summary = "Get all flagged transactions paginated response",
            description = "Returns all transactions that have been flagged by the fraud engine with pagination"
    )
    public ResponseEntity<ApiResponse<List<Transactions>>> getAllFlagged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(
                ApiResponse.success("All flagged transactions",
                        flaggedTransactionService.getAllFlagged(page, size))
        );
    }
    @PreAuthorize("hasAuthority('VIEW_FLAGGED_TRANSACTIONS')")
    @GetMapping("/flagged/card/{cardNo}")
    @Operation(
            summary = "Get flagged transactions by card",
            description = "Returns flagged transactions associated with a specific card number"
    )
    public ResponseEntity<ApiResponse<List<Transactions>>> getFlaggedByCard(
            @Parameter(description = "Masked or full card number", example = "411111******1111")
            @PathVariable String cardNo) {
        return ResponseEntity.ok(
                ApiResponse.success("Flagged transactions by card",
                        flaggedTransactionService.getFlaggedByCard(cardNo))
        );
    }

    @PreAuthorize("hasAuthority('VIEW_FLAGGED_TRANSACTIONS')")
    @GetMapping("/flagged/merchant/{merchantId}")
    @Operation(
            summary = "Get flagged transactions by merchant",
            description = "Returns flagged transactions associated with a specific merchant ID"
    )
    public ResponseEntity<ApiResponse<List<Transactions>>> getFlaggedByMerchant(
            @Parameter(description = "Merchant identifier", example = "MERCHANT_123")
            @PathVariable String merchantId) {
        return ResponseEntity.ok(
                ApiResponse.success("Flagged transactions by merchant",
                        flaggedTransactionService.getFlaggedByMerchant(merchantId))
        );
    }

    @PreAuthorize("hasAuthority('GET_ALL_BLACKLISTED')")
    @GetMapping("/blacklisted/all")
    @Operation(
            summary = "Get all blacklist data",
            description = "Returns all blacklisted cards, IPs and merchants"
    )
    public ResponseEntity<ApiResponse<BlacklistResponse>> getAllBlacklist() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "All blacklist data fetched",
                        blacklistService.getAllBlacklist()
                )
        );
    }

    @PreAuthorize("hasAuthority('GET_ALL_BLACKLISTED')")
    @GetMapping("/blacklisted/cards")
    @Operation(
            summary = "Get all blacklisted cards",
            description = "Returns all card numbers that are currently blacklisted"
    )
    public ResponseEntity<ApiResponse<?>> getAllCards() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Blacklisted cards fetched",
                        blacklistService.getAllCards()
                )
        );
    }

    @PreAuthorize("hasAuthority('GET_ALL_BLACKLISTED')")
    @GetMapping("/blacklisted/cards/{cardNo}")
    @Operation(
            summary = "Get blacklisted card",
            description = "Returns a single blacklisted card entry by card number"
    )
    public ResponseEntity<ApiResponse<?>> getCard(
            @Parameter(description = "Card number to look up", example = "411111******1111")
            @PathVariable String cardNo) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Card fetched",
                        blacklistService.getCard(cardNo)
                )
        );
    }

    @PreAuthorize("hasAuthority('GET_ALL_BLACKLISTED')")
    @GetMapping("/blacklisted/ips")
    @Operation(
            summary = "Get all blacklisted IPs",
            description = "Returns all IP addresses that are currently blacklisted"
    )
    public ResponseEntity<ApiResponse<?>> getAllIps() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Blacklisted IPs fetched",
                        blacklistService.getAllIps()
                )
        );
    }

    @PreAuthorize("hasAuthority('GET_ALL_BLACKLISTED')")
    @GetMapping("/blacklisted/ips/{ip}")
    @Operation(
            summary = "Get blacklisted IP",
            description = "Returns a single blacklisted IP entry by IP address"
    )
    public ResponseEntity<ApiResponse<?>> getIp(
            @Parameter(description = "IP address to look up", example = "192.168.1.10")
            @PathVariable String ip) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Blacklisted IP fetched",
                        blacklistService.getIp(ip)
                )
        );
    }

    @PreAuthorize("hasAuthority('GET_ALL_BLACKLISTED')")
    @GetMapping("/blacklisted/merchants")
    @Operation(
            summary = "Get all blacklisted merchants",
            description = "Returns all merchants that are currently blacklisted"
    )
    public ResponseEntity<ApiResponse<?>> getAllMerchants() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Blacklisted merchants fetched",
                        blacklistService.getAllMerchants()
                )
        );
    }

    @PreAuthorize("hasAuthority('GET_ALL_BLACKLISTED')")
    @GetMapping("/blacklisted/merchants/{merchantId}")
    @Operation(
            summary = "Get blacklisted merchant",
            description = "Returns a single blacklisted merchant entry by merchant ID"
    )
    public ResponseEntity<ApiResponse<?>> getMerchant(
            @Parameter(description = "Merchant ID to look up", example = "MERCHANT_123")
            @PathVariable String merchantId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Merchant fetched",
                        blacklistService.getMerchant(merchantId)
                )
        );
    }

}