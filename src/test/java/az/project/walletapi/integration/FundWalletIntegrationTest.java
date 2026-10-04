package az.project.walletapi.integration;

import az.project.walletapi.data.model.User;
import az.project.walletapi.data.model.Wallet;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.data.repository.WalletRepository;
import az.project.walletapi.dtos.request.FundWalletRequest;
import az.project.walletapi.dtos.request.RegisterCustomerRequest;
import az.project.walletapi.dtos.response.WalletResponse;
import az.project.walletapi.exception.UserException;
import az.project.walletapi.exception.WalletException;
import az.project.walletapi.service.AuthService;
import az.project.walletapi.service.WalletService;
import az.project.walletapi.service.TransactionService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class FundWalletIntegrationTest {

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
    public void fundWallet_withValidAmount_shouldUpdatePersistedBalance() {
        RegisterCustomerRequest register =  new RegisterCustomerRequest();
        register.setFirstName("Azeez");
        register.setLastName("Walter");
        register.setEmail("azPapa@gmail.com");
        register.setPassword("password123");
        authService.registerCustomer(register);

        User savedUser = userRepository.findByEmail(register.getEmail()).orElseThrow();
        walletService.createWallet(savedUser.getEmail());

        FundWalletRequest request = new FundWalletRequest();
        request.setAmount(new BigDecimal("1000"));

        WalletResponse walletResponse = transactionService.fundWallet(savedUser.getEmail(), request);

        Wallet updatedWallet = walletRepository.findByUserId(savedUser.getId()).orElseThrow();

        assertEquals(updatedWallet.getBalance(), walletResponse.getBalance());
    }

    @Test
    public void fundWallet_multipleTimes_shouldAccumulateBalance() {
        RegisterCustomerRequest register =  new RegisterCustomerRequest();
        register.setFirstName("Azeez");
        register.setLastName("Walter");
        register.setEmail("azPapa@gmail.com");
        register.setPassword("password123");
        authService.registerCustomer(register);

        User savedUser = userRepository.findByEmail(register.getEmail()).orElseThrow();
        walletService.createWallet(savedUser.getEmail());

        FundWalletRequest request = new FundWalletRequest();
        request.setAmount(new BigDecimal("1000"));
        transactionService.fundWallet(savedUser.getEmail(), request);

        FundWalletRequest anotherRequest = new FundWalletRequest();
        anotherRequest.setAmount(new BigDecimal("1000"));

        WalletResponse walletResponse = transactionService.fundWallet(savedUser.getEmail(), anotherRequest);

        Wallet updatedWallet = walletRepository.findByUserId(savedUser.getId()).orElseThrow();

        assertEquals(updatedWallet.getBalance(), walletResponse.getBalance());
        assertEquals(0, new BigDecimal("2000").compareTo(walletResponse.getBalance()));

    }

    @Test
    public void fundWallet_withExistingUserButNoWallet_shouldThrowWalletException() {
        RegisterCustomerRequest register =  new RegisterCustomerRequest();
        register.setFirstName("Azeez");
        register.setLastName("Walter");
        register.setEmail("azPapa@gmail.com");
        register.setPassword("password123");
        authService.registerCustomer(register);

        User savedUser = userRepository.findByEmail(register.getEmail()).orElseThrow();

        FundWalletRequest request = new FundWalletRequest();
        request.setAmount(new BigDecimal("1000"));

        assertThrows(WalletException.class, () -> transactionService.fundWallet(savedUser.getEmail(), request));
    }

    @Test
    public void fundWallet_withNonExistingUser_shouldThrowUserException() {
        String email = "fake@gmail.com";

        FundWalletRequest request = new FundWalletRequest();
        request.setAmount(new BigDecimal("1000"));

        assertThrows(UserException.class, () -> transactionService.fundWallet(email, request));
    }

    @Test
    public void fundWallet_withZeroAmount_shouldThrowWalletException() {
        RegisterCustomerRequest register =  new RegisterCustomerRequest();
        register.setFirstName("Azeez");
        register.setLastName("Walter");
        register.setEmail("azPapa@gmail.com");
        register.setPassword("password123");
        authService.registerCustomer(register);

        User savedUser = userRepository.findByEmail(register.getEmail()).orElseThrow();
        walletService.createWallet(savedUser.getEmail());

        FundWalletRequest request = new FundWalletRequest();
        request.setAmount(new BigDecimal("0"));
        assertThrows(WalletException.class, () -> transactionService.fundWallet(savedUser.getEmail(), request));
    }

    @Test
    public void fundWallet_withNegativeAmount_shouldThrowWalletException() {
        RegisterCustomerRequest register =  new RegisterCustomerRequest();
        register.setFirstName("Azeez");
        register.setLastName("Walter");
        register.setEmail("azPapa@gmail.com");
        register.setPassword("password123");
        authService.registerCustomer(register);

        User savedUser = userRepository.findByEmail(register.getEmail()).orElseThrow();
        walletService.createWallet(savedUser.getEmail());

        FundWalletRequest request = new FundWalletRequest();
        request.setAmount(new BigDecimal("-100"));
        assertThrows(WalletException.class, () -> transactionService.fundWallet(savedUser.getEmail(), request));
    }

}
