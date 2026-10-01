package az.project.walletapi.integration;

import az.project.walletapi.data.model.Status;
import az.project.walletapi.data.model.User;
import az.project.walletapi.data.model.Wallet;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.data.repository.WalletRepository;
import az.project.walletapi.dtos.request.RegisterCustomerRequest;
import az.project.walletapi.dtos.response.CreateWalletResponse;
import az.project.walletapi.exception.UserException;
import az.project.walletapi.exception.WalletException;
import az.project.walletapi.service.AuthService;
import az.project.walletapi.service.WalletService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
public class CreateWalletintegrationTest {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private WalletService walletService;

    @Test
    public void createWallet_withRegisteredUser_shouldPersistWallet() {
        RegisterCustomerRequest request = new RegisterCustomerRequest();
        request.setFirstName("Azeez");
        request.setLastName("Walter");
        request.setEmail("azPapa@gmail.com");
        request.setPassword("password123");
        authService.registerCustomer(request);

        User savedUser = userRepository.findByEmail(request.getEmail()).orElseThrow();

        CreateWalletResponse response = walletService.createWallet(savedUser.getEmail());
        Wallet savedWallet = walletRepository.findByUserId(savedUser.getId()).orElseThrow();

        assertNotNull(response.getAccountNumber());
        assertEquals(20, response.getAccountNumber().length());
        assertEquals(savedWallet.getAccountNumber(), response.getAccountNumber());
        assertEquals(Status.ACTIVE, savedWallet.getStatus());
        assertEquals(savedUser.getId(), savedWallet.getUser().getId());
        assertEquals(0, BigDecimal.ZERO.compareTo(savedWallet.getBalance()));

    }

    @Test
    public void createWallet_whenWalletAlreadyExists_shouldThrowWalletException() {
        RegisterCustomerRequest request = new RegisterCustomerRequest();
        request.setFirstName("Azeez");
        request.setLastName("Walter");
        request.setEmail("azPapa@gmail.com");
        request.setPassword("password123");
        authService.registerCustomer(request);

        User savedUser = userRepository.findByEmail(request.getEmail()).orElseThrow();

        walletService.createWallet(savedUser.getEmail());
        assertThrows(WalletException.class, () -> walletService.createWallet(savedUser.getEmail()));
    }

    @Test
    public void createWallet_withNonExistingUser_shouldThrowUserException() {
        String email = "NonExistingUser@gmail.com";
        assertThrows(UserException.class, () -> walletService.createWallet(email));
    }

}
