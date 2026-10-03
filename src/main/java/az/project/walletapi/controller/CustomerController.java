package az.project.walletapi.controller;

import az.project.walletapi.dtos.request.FundWalletRequest;
import az.project.walletapi.dtos.response.WalletResponse;
import az.project.walletapi.dtos.response.CustomerProfileResponse;
import az.project.walletapi.service.CustomerService;
import az.project.walletapi.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private WalletService walletService;

    @GetMapping("/profile")
    public ResponseEntity<CustomerProfileResponse> getProfile(Authentication authentication) {
        String email = authentication.getName();

        CustomerProfileResponse response = customerService.getProfile(email);

        return ResponseEntity.ok(response);

    }

    @PostMapping("/create-wallet")
    public ResponseEntity<WalletResponse> createWallet(Authentication authentication) {
        String email = authentication.getName();

        WalletResponse response = walletService.createWallet(email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/get-wallet")
    public ResponseEntity<WalletResponse> getWallet(Authentication authentication) {
        String email = authentication.getName();

        WalletResponse response = walletService.getWallet(email);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/fund-wallet")
    public ResponseEntity<WalletResponse> fundWallet(Authentication authentication, @Valid @RequestBody FundWalletRequest fundWalletRequest) {
        String email = authentication.getName();
        WalletResponse response = walletService.fundWallet(email, fundWalletRequest);
        return ResponseEntity.ok(response);
    }
}
