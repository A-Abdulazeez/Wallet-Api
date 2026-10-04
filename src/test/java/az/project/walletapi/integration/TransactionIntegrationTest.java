package az.project.walletapi.integration;

import az.project.walletapi.data.model.TransactionType;
import az.project.walletapi.data.model.User;
import az.project.walletapi.data.model.Wallet;
import az.project.walletapi.data.repository.TransactionRepository;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.data.repository.WalletRepository;
import az.project.walletapi.dtos.request.FundWalletRequest;
import az.project.walletapi.dtos.request.RegisterCustomerRequest;
import az.project.walletapi.dtos.request.TransferFundsRequest;
import az.project.walletapi.dtos.response.TransactionResponse;
import az.project.walletapi.dtos.response.TransferResponse;
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
public class TransactionIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    public void transferFunds_shouldPersistBalance() {
        register("sender@test.com");
        register("receiver@test.com");
        User sender = userRepository.findByEmail("sender@test.com").orElseThrow();
        User receiver = userRepository.findByEmail("receiver@test.com").orElseThrow();
        walletService.createWallet(sender.getEmail());
        walletService.createWallet(receiver.getEmail());

        FundWalletRequest funding = new FundWalletRequest();
        funding.setAmount(new BigDecimal("10000"));
        transactionService.fundWallet(sender.getEmail(), funding);

        Wallet receiverWallet = walletRepository.findByUserId(receiver.getId()).orElseThrow();
        TransferFundsRequest transfer = new TransferFundsRequest();
        transfer.setReceiverAccountNumber(receiverWallet.getAccountNumber());
        transfer.setAmount(new BigDecimal("3000"));

        TransferResponse response = transactionService.transferFunds(sender.getEmail(), transfer);

        Wallet updatedSender = walletRepository.findByUserId(sender.getId()).orElseThrow();
        Wallet updatedReceiver = walletRepository.findByUserId(receiver.getId()).orElseThrow();
        assertEquals(0, new BigDecimal("7000").compareTo(updatedSender.getBalance()));
        assertEquals(0, new BigDecimal("3000").compareTo(updatedReceiver.getBalance()));
        assertNotNull(response.getReference());
        assertTrue(transactionRepository.findByReference(response.getReference()).isPresent());

    }

    @Test
    public void transferFunds_shouldPersistTransactionHistory() {
        register("sender@test.com");
        register("receiver@test.com");
        User sender = userRepository.findByEmail("sender@test.com").orElseThrow();
        User receiver = userRepository.findByEmail("receiver@test.com").orElseThrow();
        walletService.createWallet(sender.getEmail());
        walletService.createWallet(receiver.getEmail());

        FundWalletRequest funding = new FundWalletRequest();
        funding.setAmount(new BigDecimal("10000"));
        transactionService.fundWallet(sender.getEmail(), funding);

        Wallet receiverWallet = walletRepository.findByUserId(receiver.getId()).orElseThrow();
        TransferFundsRequest transfer = new TransferFundsRequest();
        transfer.setReceiverAccountNumber(receiverWallet.getAccountNumber());
        transfer.setAmount(new BigDecimal("3000"));

        transactionService.transferFunds(sender.getEmail(), transfer);

        List<TransactionResponse> senderHistory = transactionService.getTransactions(sender.getEmail());
        assertEquals(2, senderHistory.size());
        assertTrue(senderHistory.stream().anyMatch(t -> t.getType() == TransactionType.FUNDING));
        assertTrue(senderHistory.stream().anyMatch(t -> t.getType() == TransactionType.TRANSFER));
    }

    private void register(String email) {
        RegisterCustomerRequest request = new RegisterCustomerRequest();
        request.setFirstName("Test");
        request.setLastName("User");
        request.setEmail(email);
        request.setPassword("password123");
        authService.registerCustomer(request);
    }
}
