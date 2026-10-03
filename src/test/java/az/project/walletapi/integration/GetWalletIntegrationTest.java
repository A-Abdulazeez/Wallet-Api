package az.project.walletapi.integration;

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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class GetWalletIntegrationTest {

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Test
    public void getWallet_withExistingUserAndWallet_shouldReturnWallet() {
        RegisterCustomerRequest request = new RegisterCustomerRequest();
        request.setFirstName("Azeez");
        request.setLastName("Walter");
        request.setEmail("azPapa@gmail.com");
        request.setPassword("password123");
        authService.registerCustomer(request);

        User savedUser = userRepository.findByEmail(request.getEmail()).orElseThrow();

        walletService.createWallet(savedUser.getEmail());
        Wallet savedWallet = walletRepository.findByUserId(savedUser.getId()).orElseThrow();

        CreateWalletResponse getWallet = walletService.getWallet(savedUser.getEmail());
        assertNotNull(getWallet);
        assertEquals(savedWallet.getAccountNumber(), getWallet.getAccountNumber());
        assertEquals(savedWallet.getStatus(), getWallet.getStatus());
        assertEquals(0, savedWallet.getBalance().compareTo(getWallet.getBalance()));

    }

    @Test
    public void getWallet_withExistingUserButNoWallet_shouldThrowWalletException() {
        RegisterCustomerRequest request = new RegisterCustomerRequest();
        request.setFirstName("Azeez");
        request.setLastName("Walter");
        request.setEmail("azPapa@gmail.com");
        request.setPassword("password123");
        authService.registerCustomer(request);

        User savedUser = userRepository.findByEmail(request.getEmail()).orElseThrow();

        assertThrows(WalletException.class, () -> walletService.getWallet(savedUser.getEmail()));

    }

    @Test
    public void getWallet_withNonExistingUser_shouldThrowUserException() {
        String  email = "notexistingemail@gmail.com";
        assertThrows(UserException.class, () -> walletService.getWallet(email));
    }
}
