package az.project.walletapi.integration;

import az.project.walletapi.data.model.User;
import az.project.walletapi.data.model.Wallet;
import az.project.walletapi.data.repository.TransactionRepository;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.data.repository.WalletRepository;
import az.project.walletapi.dtos.request.FundWalletRequest;
import az.project.walletapi.dtos.request.RegisterCustomerRequest;
import az.project.walletapi.dtos.request.WithdrawRequest;
import az.project.walletapi.dtos.response.WalletResponse;
import az.project.walletapi.exception.UserException;
import az.project.walletapi.exception.WalletException;
import az.project.walletapi.service.AuthService;
import az.project.walletapi.service.TransactionService;
import az.project.walletapi.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class WithdrawIntegrationTest {

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;


    @Test
    public void withdrawFromWallet_withValidAmount_shouldUpdatePersistedBalanceAndSaveTransaction() {
        RegisterCustomerRequest register =  new RegisterCustomerRequest();
        register.setFirstName("Azeez");
        register.setLastName("Walter");
        register.setEmail("azPapa@gmail.com");
        register.setPassword("password123");
        authService.registerCustomer(register);

        User savedUser = userRepository.findByEmail(register.getEmail()).orElseThrow();
        walletService.createWallet(savedUser.getEmail());

        FundWalletRequest request = new FundWalletRequest();
        request.setAmount(new BigDecimal("10000"));
        transactionService.fundWallet(savedUser.getEmail(), request);

        WithdrawRequest withdraw = new WithdrawRequest();
        withdraw.setAmount(new BigDecimal("1000"));

        WalletResponse walletResponse = transactionService.withdraw(savedUser.getEmail(), withdraw );

        Wallet updatedWallet = walletRepository.findByUserId(savedUser.getId()).orElseThrow();

        assertEquals(walletResponse.getBalance(), updatedWallet.getBalance());
        assertEquals(0, new BigDecimal("9000").compareTo(walletResponse.getBalance()));
    }

    @Test
    public void withdrawFromWallet_withNonExistingUser_shouldThrowUserException() {
        String email = "fakeemail@gmail.com";

        WithdrawRequest withdraw = new WithdrawRequest();
        withdraw.setAmount(new BigDecimal("1000"));
        assertThrows(UserException.class, () -> transactionService.withdraw(email, withdraw));
    }

    @Test
    public void withdrawFromWallet_withExistingUserButNoWallet_shouldThrowWalletException() {
        RegisterCustomerRequest register =  new RegisterCustomerRequest();
        register.setFirstName("Azeez");
        register.setLastName("Walter");
        register.setEmail("azPapa@gmail.com");
        register.setPassword("password123");
        authService.registerCustomer(register);

        User savedUser = userRepository.findByEmail(register.getEmail()).orElseThrow();

        WithdrawRequest withdraw = new WithdrawRequest();
        withdraw.setAmount(new BigDecimal("1000"));
        assertThrows(WalletException.class, () -> transactionService.withdraw(savedUser.getEmail(), withdraw));

    }

    @Test
    public void withdrawFromWallet_withInsufficientBalance_shouldThrowWalletException() {
        RegisterCustomerRequest register =  new RegisterCustomerRequest();
        register.setFirstName("Azeez");
        register.setLastName("Walter");
        register.setEmail("azPapa@gmail.com");
        register.setPassword("password123");
        authService.registerCustomer(register);

        User savedUser = userRepository.findByEmail(register.getEmail()).orElseThrow();
        walletService.createWallet(savedUser.getEmail());

        FundWalletRequest request = new FundWalletRequest();
        request.setAmount(new BigDecimal("10000"));
        transactionService.fundWallet(savedUser.getEmail(), request);

        WithdrawRequest withdraw = new WithdrawRequest();
        withdraw.setAmount(new BigDecimal("11000"));

       assertThrows(WalletException.class, () -> transactionService.withdraw(savedUser.getEmail(), withdraw));
    }

    @Test
    public void withdrawFromWallet_withZeroAmount_shouldThrowWalletException() {
        RegisterCustomerRequest register =  new RegisterCustomerRequest();
        register.setFirstName("Azeez");
        register.setLastName("Walter");
        register.setEmail("azPapa@gmail.com");
        register.setPassword("password123");
        authService.registerCustomer(register);

        User savedUser = userRepository.findByEmail(register.getEmail()).orElseThrow();
        walletService.createWallet(savedUser.getEmail());

        FundWalletRequest request = new FundWalletRequest();
        request.setAmount(new BigDecimal("10000"));
        transactionService.fundWallet(savedUser.getEmail(), request);

        WithdrawRequest withdraw = new WithdrawRequest();
        withdraw.setAmount(new BigDecimal("0"));

        assertThrows(WalletException.class, () -> transactionService.withdraw(savedUser.getEmail(), withdraw));
    }

    @Test
    public void withdrawFromWallet_withNegativeAmount_shouldThrowWalletException() {
        RegisterCustomerRequest register =  new RegisterCustomerRequest();
        register.setFirstName("Azeez");
        register.setLastName("Walter");
        register.setEmail("azPapa@gmail.com");
        register.setPassword("password123");
        authService.registerCustomer(register);

        User savedUser = userRepository.findByEmail(register.getEmail()).orElseThrow();
        walletService.createWallet(savedUser.getEmail());

        FundWalletRequest request = new FundWalletRequest();
        request.setAmount(new BigDecimal("10000"));
        transactionService.fundWallet(savedUser.getEmail(), request);

        WithdrawRequest withdraw = new WithdrawRequest();
        withdraw.setAmount(new BigDecimal("-1000"));

        assertThrows(WalletException.class, () -> transactionService.withdraw(savedUser.getEmail(), withdraw));

    }

    @Test
    public void withdrawFromWallet_withExactBalance_shouldPersistZeroBalanceAndSaveTransaction() {
        RegisterCustomerRequest register =  new RegisterCustomerRequest();
        register.setFirstName("Azeez");
        register.setLastName("Walter");
        register.setEmail("azPapa@gmail.com");
        register.setPassword("password123");
        authService.registerCustomer(register);

        User savedUser = userRepository.findByEmail(register.getEmail()).orElseThrow();
        walletService.createWallet(savedUser.getEmail());

        FundWalletRequest request = new FundWalletRequest();
        request.setAmount(new BigDecimal("10000"));
        transactionService.fundWallet(savedUser.getEmail(), request);

        WithdrawRequest withdraw = new WithdrawRequest();
        withdraw.setAmount(new BigDecimal("10000"));

        WalletResponse response = transactionService.withdraw(savedUser.getEmail(), withdraw);

        Wallet updatedWallet = walletRepository.findByAccountNumber(response.getAccountNumber()).orElseThrow();
        assertEquals(response.getBalance(), updatedWallet.getBalance());
        assertEquals(0, new BigDecimal("0").compareTo(response.getBalance()));

    }


}
