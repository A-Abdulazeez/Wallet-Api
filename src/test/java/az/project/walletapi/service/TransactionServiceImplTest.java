package az.project.walletapi.service;

import az.project.walletapi.data.model.*;
import az.project.walletapi.data.repository.TransactionRepository;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.data.repository.WalletRepository;
import az.project.walletapi.dtos.request.FundWalletRequest;
import az.project.walletapi.dtos.request.TransferFundsRequest;
import az.project.walletapi.dtos.request.WithdrawRequest;
import az.project.walletapi.dtos.response.TransferResponse;
import az.project.walletapi.dtos.response.WalletResponse;
import az.project.walletapi.exception.UserException;
import az.project.walletapi.exception.WalletException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    WalletRepository walletRepository;

    @Mock
    TransactionRepository transactionRepository;

    @InjectMocks
    TransactionServiceImpl transactionService;

    @Test
    public void fundWallet_withValidAmount_shouldUpdateBalanceAndSaveTransaction() {
        User user = user("sender@gmail.com");
        Wallet wallet = wallet("12345678901234567890", "1000", user);
        FundWalletRequest request = new FundWalletRequest();
        request.setAmount(new BigDecimal("500"));

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(walletRepository.findByUserId(user.getId())).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        WalletResponse response = transactionService.fundWallet(user.getEmail(), request);

        assertEquals(0, new BigDecimal("1500").compareTo(response.getBalance()));

    }

    @Test
    public void fundWallet_withNonExistingUser_shouldThrowUserException() {
        when(userRepository.findByEmail("missing@gmail.com")).thenReturn(Optional.empty());
        FundWalletRequest request = new FundWalletRequest();
        request.setAmount(BigDecimal.ONE);
        assertThrows(UserException.class, () -> transactionService.fundWallet("missing@gmail.com", request));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    public void transferFunds_withValidTransfer_shouldUpdateBothWalletsAndSaveTransaction() {
        User sender = user("sender@gmail.com");
        Wallet senderWallet = wallet("12345678901234567890", "10000", sender);
        Wallet receiverWallet = wallet("98765432109876543210", "2000", user("receiver@gmail.com"));
        TransferFundsRequest request = new TransferFundsRequest();
        request.setReceiverAccountNumber(receiverWallet.getAccountNumber());
        request.setAmount(new BigDecimal("3000"));

        when(userRepository.findByEmail(sender.getEmail())).thenReturn(Optional.of(sender));
        when(walletRepository.findByUserId(sender.getId())).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findByAccountNumber(receiverWallet.getAccountNumber())).thenReturn(Optional.of(receiverWallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> {
            Transaction transaction = i.getArgument(0);
            transaction.setReference("trx-test-reference");
            return transaction;
        });

        TransferResponse response = transactionService.transferFunds(sender.getEmail(), request);

        assertEquals(0, new BigDecimal("7000").compareTo(senderWallet.getBalance()));
        assertEquals(0, new BigDecimal("5000").compareTo(receiverWallet.getBalance()));
        assertEquals("trx-test-reference", response.getReference());

    }

    @Test
    public void transferFunds_withInsufficientBalance_shouldNotSaveWalletOrTransaction() {
        User sender = user("sender@gmail.com");
        Wallet senderWallet = wallet("12345678901234567890", "1000", sender);
        Wallet receiverWallet = wallet("98765432109876543210", "0", user("receiver@gmail.com"));
        TransferFundsRequest request = new TransferFundsRequest();
        request.setReceiverAccountNumber(receiverWallet.getAccountNumber());
        request.setAmount(new BigDecimal("2000"));

        when(userRepository.findByEmail(sender.getEmail())).thenReturn(Optional.of(sender));
        when(walletRepository.findByUserId(sender.getId())).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findByAccountNumber(receiverWallet.getAccountNumber())).thenReturn(Optional.of(receiverWallet));

        assertThrows(WalletException.class, () -> transactionService.transferFunds(sender.getEmail(), request));
        verify(walletRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    public void withdrawFromWallet_withValidAmount_shouldUpdateBalanceAndSaveTransaction() {
        User user = user("atoz@gmail.com");
        Wallet wallet = wallet("12345678901234567890", "6000", user);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(walletRepository.findByUserId(user.getId())).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        WithdrawRequest requestWithdraw = new WithdrawRequest();
        requestWithdraw.setAmount(new BigDecimal("3000"));

        WalletResponse response = transactionService.withdraw(user.getEmail(), requestWithdraw);
        assertEquals(0, new BigDecimal("3000").compareTo(response.getBalance()));

    }

    @Test
    public void withdrawFromWallet_withNonExistingUser_shouldThrowUserException() {
        when(userRepository.findByEmail("fakeemail@gmail.com")).thenReturn(Optional.empty());
        WithdrawRequest requestWithdraw = new WithdrawRequest();
        requestWithdraw.setAmount(new BigDecimal("3000"));
        assertThrows(UserException.class, () -> transactionService.withdraw("fakeemail@gmail.com", requestWithdraw));
        verify(transactionRepository, never()).save(any());

    }

    @Test
    public void withdrawFromWallet_withInsuffficientBalance_shouldThrowWalletException() {
        User user = user("atoz@gmail.com");
        Wallet wallet = wallet("12345678901234567890", "6000", user);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(walletRepository.findByUserId(user.getId())).thenReturn(Optional.of(wallet));

        WithdrawRequest requestWithdraw = new WithdrawRequest();
        requestWithdraw.setAmount(new BigDecimal("7000"));

        assertThrows(WalletException.class, () -> transactionService.withdraw(user.getEmail(), requestWithdraw));
    }

    private User user(String email) {
        User user = new User();
        user.setEmail(email);
        return user;
    }

    private Wallet wallet(String accountNumber, String balance, User user) {
        Wallet wallet = new Wallet();
        wallet.setAccountNumber(accountNumber);
        wallet.setBalance(new BigDecimal(balance));
        wallet.setStatus(Status.ACTIVE);
        wallet.setUser(user);
        return wallet;
    }
}
