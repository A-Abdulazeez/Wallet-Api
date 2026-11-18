package az.project.walletapi.service;

import az.project.walletapi.data.model.Role;
import az.project.walletapi.data.model.Status;
import az.project.walletapi.data.model.User;
import az.project.walletapi.data.model.Wallet;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.data.repository.WalletRepository;
import az.project.walletapi.dtos.response.AdminCustomerResponse;
import az.project.walletapi.dtos.response.AdminWalletResponse;
import az.project.walletapi.exception.UserException;
import az.project.walletapi.exception.WalletException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    public void getAllCustomers_whenCustomersExist_shouldReturnCustomerList() {
        User customer1 = new User();
        customer1.setId(UUID.randomUUID());
        customer1.setFirstName("Azeez");
        customer1.setLastName("Walter");
        customer1.setEmail("az@gmail.com");
        customer1.setRole(Role.CUSTOMER);

        User customer2 = new User();
        customer2.setId(UUID.randomUUID());
        customer2.setFirstName("Banky");
        customer2.setLastName("Williams");
        customer2.setEmail("banky@gmail.com");
        customer2.setRole(Role.CUSTOMER);

        when(userRepository.findAllByRole(Role.CUSTOMER)).thenReturn(List.of(customer1, customer2));

        List<AdminCustomerResponse> response = adminService.getAllCustomers();
        assertEquals(2, response.size());

    }

    @Test
    public void getAllCustomers_whenNoCustomersExist_shouldReturnEmptyList() {
//        when(userRepository.findAll()).thenReturn(List.of());
        List<AdminCustomerResponse> response = adminService.getAllCustomers();
        assertEquals(0, response.size());
    }

    @Test
    public void getCustomerByEmail_whenCustomerExists_shouldReturnCustomer() {
        User customer1 = new User();
        customer1.setId(UUID.randomUUID());
        customer1.setFirstName("Azeez");
        customer1.setLastName("Walter");
        customer1.setEmail("az@gmail.com");
        customer1.setRole(Role.CUSTOMER);

        when(userRepository.findByEmailAndRole(customer1.getEmail(), Role.CUSTOMER)).thenReturn(Optional.of(customer1));

        AdminCustomerResponse response = adminService.getCustomerByEmail(customer1.getEmail());
        assertEquals("Azeez", response.getFirstName());
        assertEquals("Walter", response.getLastName());
        assertEquals("az@gmail.com", response.getEmail());
    }

    @Test
    public void getCustomerByEmail_whenCustomerDoesNotExist_shouldThrowException() {
        String email =  "mail@email.com";
        when(userRepository.findByEmailAndRole(email, Role.CUSTOMER)).thenReturn(Optional.empty());

        assertThrows(UserException.class, ()-> adminService.getCustomerByEmail(email));
    }

    @Test
    public void getCustomerByEmail_whenUserIsAdmin_shouldThrowException() {
        User admin = new User();
        admin.setId(UUID.randomUUID());
        admin.setFirstName("Azeez");
        admin.setLastName("Walter");
        admin.setEmail("az@gmail.com");
        admin.setRole(Role.ADMIN);

        when(userRepository.findByEmailAndRole(admin.getEmail(), Role.CUSTOMER)).thenReturn(Optional.empty());
        assertThrows(UserException.class, ()-> adminService.getCustomerByEmail(admin.getEmail()));

    }

    @Test
    public void getAllWallets_whenWalletsExist_shouldReturnWalletList() {
        User user = new User();
        user.setEmail("az@gmail.com");

        Wallet wallet1 = new Wallet();
        wallet1.setId(UUID.randomUUID());
        wallet1.setAccountNumber("12345678901234567890");
        wallet1.setBalance(new BigDecimal("5000.00"));
        wallet1.setStatus(Status.ACTIVE);
        wallet1.setUser(user);

        Wallet wallet2 = new Wallet();
        wallet2.setId(UUID.randomUUID());
        wallet2.setAccountNumber("09876543210987654321");
        wallet2.setBalance(new BigDecimal("10000.00"));
        wallet2.setStatus(Status.ACTIVE);
        wallet2.setUser(user);

        when(walletRepository.findAll()).thenReturn(List.of(wallet1, wallet2));

        List<AdminWalletResponse> response = adminService.getAllWallets();
        assertEquals(2, response.size());
    }

    @Test
    public void getAllWallets_whenNoWalletsExist_shouldReturnEmptyList() {
        when(walletRepository.findAll()).thenReturn(List.of());

        List<AdminWalletResponse> response = adminService.getAllWallets();
        assertEquals(0, response.size());
    }

    @Test
    public void getWalletByAccountNumber_whenWalletExists_shouldReturnWallet() {
        User user = new User();
        user.setEmail("az@gmail.com");

        Wallet wallet = new Wallet();
        wallet.setId(UUID.randomUUID());
        wallet.setAccountNumber("12345678901234567890");
        wallet.setBalance(new BigDecimal("5000.00"));
        wallet.setStatus(Status.ACTIVE);
        wallet.setUser(user);

        when(walletRepository.findByAccountNumber(wallet.getAccountNumber())).thenReturn(Optional.of(wallet));

        AdminWalletResponse response = adminService.getWalletByAccountNumber(wallet.getAccountNumber());
        assertEquals(wallet.getAccountNumber(), response.getAccountNumber());
        assertEquals(wallet.getBalance(), response.getBalance());
        assertEquals(Status.ACTIVE, response.getStatus());
        assertEquals("az@gmail.com", response.getCustomerEmail());
    }

    @Test
    public void getWalletByAccountNumber_whenWalletDoesNotExist_shouldThrowException() {
        String accountNumber = "nonexistent";
        when(walletRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        assertThrows(WalletException.class, () -> adminService.getWalletByAccountNumber(accountNumber));
    }

}