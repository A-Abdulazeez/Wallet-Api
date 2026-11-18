package az.project.walletapi.integration;

import az.project.walletapi.data.model.Role;
import az.project.walletapi.data.model.Status;
import az.project.walletapi.data.model.User;
import az.project.walletapi.data.model.Wallet;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.data.repository.WalletRepository;
import az.project.walletapi.dtos.request.RegisterCustomerRequest;
import az.project.walletapi.dtos.response.AdminWalletResponse;
import az.project.walletapi.dtos.response.WalletResponse;
import az.project.walletapi.exception.WalletException;
import az.project.walletapi.service.AdminService;
import az.project.walletapi.service.AuthService;
import az.project.walletapi.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class AdminWalletIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private AdminService adminService;

    @Test
    public void getAllWallets_whenWalletsExist_shouldReturnWallets() {
        RegisterCustomerRequest request = new RegisterCustomerRequest();
        request.setFirstName("Azeez");
        request.setLastName("Walter");
        request.setEmail("walletowner@mail.com");
        request.setPassword("password123");
        authService.registerCustomer(request);
        walletService.createWallet(request.getEmail());

        List<AdminWalletResponse> wallets = adminService.getAllWallets();
        assertFalse(wallets.isEmpty());
    }

    @Test
    public void getWalletByAccountNumber_whenWalletExists_shouldReturnWallet() {
        RegisterCustomerRequest request = new RegisterCustomerRequest();
        request.setFirstName("Azeez");
        request.setLastName("Walter");
        request.setEmail("owner2@mail.com");
        request.setPassword("password123");
        authService.registerCustomer(request);
        WalletResponse created = walletService.createWallet(request.getEmail());

        AdminWalletResponse found = adminService.getWalletByAccountNumber(created.getAccountNumber());
        assertEquals(created.getAccountNumber(), found.getAccountNumber());
        assertEquals(Status.ACTIVE, found.getStatus());
        assertEquals("owner2@mail.com", found.getCustomerEmail());
    }

    @Test
    public void getWalletByAccountNumber_whenWalletDoesNotExist_shouldThrowException() {
        assertThrows(WalletException.class, () -> adminService.getWalletByAccountNumber("00000000000000000000"));
    }

    @Test
    public void freezeAndUnfreezeWallet_shouldUpdateStatusCorrectly() {
        RegisterCustomerRequest request = new RegisterCustomerRequest();
        request.setFirstName("Azeez");
        request.setLastName("Walter");
        request.setEmail("freezeowner@mail.com");
        request.setPassword("password123");
        authService.registerCustomer(request);
        WalletResponse created = walletService.createWallet(request.getEmail());

        AdminWalletResponse frozen = adminService.freezeWallet(created.getAccountNumber());
        assertEquals(Status.FROZEN, frozen.getStatus());

        assertThrows(WalletException.class, () -> adminService.freezeWallet(created.getAccountNumber()));

        AdminWalletResponse active = adminService.unfreezeWallet(created.getAccountNumber());
        assertEquals(Status.ACTIVE, active.getStatus());

        assertThrows(WalletException.class, () -> adminService.unfreezeWallet(created.getAccountNumber()));
    }
}
