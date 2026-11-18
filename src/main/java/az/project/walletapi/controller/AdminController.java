package az.project.walletapi.controller;

import az.project.walletapi.dtos.response.AdminCustomerResponse;
import az.project.walletapi.dtos.response.AdminWalletResponse;
import az.project.walletapi.dtos.response.TransactionResponse;
import az.project.walletapi.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/customers")
    public ResponseEntity<List<AdminCustomerResponse>> getAllCustomers() {
        return ResponseEntity.ok(adminService.getAllCustomers());
    }

    @GetMapping("/customers/{email}")
    public ResponseEntity<AdminCustomerResponse> getCustomerByEmail(@PathVariable String email) {
        return ResponseEntity.ok(adminService.getCustomerByEmail(email));
    }

    @GetMapping("/wallets")
    public ResponseEntity<List<AdminWalletResponse>> getAllWallets() {
        return ResponseEntity.ok(adminService.getAllWallets());
    }

    @GetMapping("/wallets/{accountNumber}")
    public ResponseEntity<AdminWalletResponse> getWalletByAccountNumber(@PathVariable String accountNumber) {
        return ResponseEntity.ok(adminService.getWalletByAccountNumber(accountNumber));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        return ResponseEntity.ok(adminService.getAllTransactions());
    }

    @GetMapping("/transactions/{reference}")
    public ResponseEntity<TransactionResponse> getTransactionByReference(@PathVariable String reference) {
        return ResponseEntity.ok(adminService.getTransactionByReference(reference));
    }

    @GetMapping("/wallets/{accountNumber}/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByWallet(@PathVariable String accountNumber) {
        return ResponseEntity.ok(adminService.getTransactionsByWallet(accountNumber));
    }

}
