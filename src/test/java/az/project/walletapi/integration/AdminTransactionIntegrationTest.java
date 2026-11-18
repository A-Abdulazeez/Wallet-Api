package az.project.walletapi.integration;

import az.project.walletapi.data.model.Status;
import az.project.walletapi.data.repository.TransactionRepository;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.data.repository.WalletRepository;
import az.project.walletapi.dtos.request.FundWalletRequest;
import az.project.walletapi.dtos.request.RegisterCustomerRequest;
import az.project.walletapi.dtos.request.TransferFundsRequest;
import az.project.walletapi.dtos.request.WithdrawRequest;
import az.project.walletapi.dtos.response.TransactionResponse;
import az.project.walletapi.dtos.response.WalletResponse;
import az.project.walletapi.exception.WalletException;
import az.project.walletapi.service.AdminService;
import az.project.walletapi.service.AuthService;
import az.project.walletapi.service.TransactionService;
import az.project.walletapi.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class AdminTransactionIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AdminService adminService;

    @Test
    public void getAllTransactions_andLookupByReferenceAndWallet() {
        RegisterCustomerRequest user1 = new RegisterCustomerRequest();
        user1.setFirstName("Sender");
        user1.setLastName("User");
        user1.setEmail("sender1@mail.com");
        user1.setPassword("password123");
        authService.registerCustomer(user1);
        WalletResponse wallet1 = walletService.createWallet(user1.getEmail());

        FundWalletRequest fundRequest = new FundWalletRequest();
        fundRequest.setAmount(new BigDecimal("5000.00"));
        transactionService.fundWallet(user1.getEmail(), fundRequest);

        List<TransactionResponse> allTransactions = adminService.getAllTransactions();
        assertFalse(allTransactions.isEmpty());

        TransactionResponse tx = allTransactions.get(0);
        TransactionResponse foundByRef = adminService.getTransactionByReference(tx.getReference());
        assertEquals(tx.getReference(), foundByRef.getReference());

        List<TransactionResponse> walletTx = adminService.getTransactionsByWallet(wallet1.getAccountNumber());
        assertFalse(walletTx.isEmpty());
    }

    @Test
    public void operationsOnFrozenWallet_shouldThrowExceptions() {
        RegisterCustomerRequest user1 = new RegisterCustomerRequest();
        user1.setFirstName("UserOne");
        user1.setLastName("Test");
        user1.setEmail("frozen1@mail.com");
        user1.setPassword("password123");
        authService.registerCustomer(user1);
        WalletResponse wallet1 = walletService.createWallet(user1.getEmail());

        RegisterCustomerRequest user2 = new RegisterCustomerRequest();
        user2.setFirstName("UserTwo");
        user2.setLastName("Test");
        user2.setEmail("frozen2@mail.com");
        user2.setPassword("password123");
        authService.registerCustomer(user2);
        WalletResponse wallet2 = walletService.createWallet(user2.getEmail());

        // Freeze wallet1
        adminService.freezeWallet(wallet1.getAccountNumber());

        // 1. Funding frozen wallet blocked
        FundWalletRequest fundRequest = new FundWalletRequest();
        fundRequest.setAmount(new BigDecimal("1000.00"));
        assertThrows(WalletException.class, () -> transactionService.fundWallet(user1.getEmail(), fundRequest));

        // 2. Transfer from frozen sender blocked
        TransferFundsRequest transferRequest = new TransferFundsRequest();
        transferRequest.setReceiverAccountNumber(wallet2.getAccountNumber());
        transferRequest.setAmount(new BigDecimal("500.00"));
        assertThrows(WalletException.class, () -> transactionService.transferFunds(user1.getEmail(), transferRequest));

        // 3. Transfer to frozen receiver blocked
        // Fund wallet2 first while active
        transactionService.fundWallet(user2.getEmail(), fundRequest);
        TransferFundsRequest transferToFrozen = new TransferFundsRequest();
        transferToFrozen.setReceiverAccountNumber(wallet1.getAccountNumber());
        transferToFrozen.setAmount(new BigDecimal("500.00"));
        assertThrows(WalletException.class, () -> transactionService.transferFunds(user2.getEmail(), transferToFrozen));

        // 4. Withdrawal from frozen wallet blocked
        WithdrawRequest withdrawRequest = new WithdrawRequest();
        withdrawRequest.setAmount(new BigDecimal("500.00"));
        assertThrows(WalletException.class, () -> transactionService.withdraw(user1.getEmail(), withdrawRequest));
    }
}
