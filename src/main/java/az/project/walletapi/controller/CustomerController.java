package az.project.walletapi.controller;

import az.project.walletapi.dtos.request.FundWalletRequest;
import az.project.walletapi.dtos.request.TransferFundsRequest;
import az.project.walletapi.dtos.response.*;
import az.project.walletapi.service.CustomerService;
import az.project.walletapi.service.TransactionService;
import az.project.walletapi.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired private CustomerService customerService;
    @Autowired private WalletService walletService;
    @Autowired private TransactionService transactionService;

    @GetMapping("/profile")
    public ResponseEntity<CustomerProfileResponse> getProfile(Authentication authentication) {
        return ResponseEntity.ok(customerService.getProfile(authentication.getName()));
    }

    @PostMapping("/create-wallet")
    public ResponseEntity<WalletResponse> createWallet(Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(walletService.createWallet(authentication.getName()));
    }

    @GetMapping("/get-wallet")
    public ResponseEntity<WalletResponse> getWallet(Authentication authentication) {
        return ResponseEntity.ok(walletService.getWallet(authentication.getName()));
    }

    @PatchMapping("/fund-wallet")
    public ResponseEntity<WalletResponse> fundWallet(Authentication authentication, @Valid @RequestBody FundWalletRequest request) {
        return ResponseEntity.ok(transactionService.fundWallet(authentication.getName(), request));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transferFunds(Authentication authentication, @Valid @RequestBody TransferFundsRequest request) {
        return ResponseEntity.ok(transactionService.transferFunds(authentication.getName(), request));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(Authentication authentication) {
        return ResponseEntity.ok(transactionService.getTransactions(authentication.getName()));
    }
}
