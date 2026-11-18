package az.project.walletapi.service;

import az.project.walletapi.data.model.*;
import az.project.walletapi.data.repository.TransactionRepository;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.data.repository.WalletRepository;
import az.project.walletapi.dtos.response.AdminCustomerResponse;
import az.project.walletapi.dtos.response.AdminWalletResponse;
import az.project.walletapi.dtos.response.TransactionResponse;
import az.project.walletapi.exception.UserException;
import az.project.walletapi.exception.WalletException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Mock
    private TransactionRepository transactionRepository;

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

    @Test
    public void getAllTransactions_whenTransactionsExist_shouldReturnTransactionList() {
        Wallet sender = new Wallet();
        sender.setAccountNumber("11111111111111111111");
        Wallet receiver = new Wallet();
        receiver.setAccountNumber("22222222222222222222");

        Transaction tx1 = new Transaction();
        tx1.setReference("ref1");
        tx1.setType(TransactionType.TRANSFER);
        tx1.setStatus(TransactionStatus.SUCCESSFUL);
        tx1.setAmount(new BigDecimal("1000.00"));
        tx1.setSenderWallet(sender);
        tx1.setReceiverWallet(receiver);
        tx1.setCreatedAt(LocalDateTime.now());

        Transaction tx2 = new Transaction();
        tx2.setReference("ref2");
        tx2.setType(TransactionType.FUNDING);
        tx2.setStatus(TransactionStatus.SUCCESSFUL);
        tx2.setAmount(new BigDecimal("500.00"));
        tx2.setReceiverWallet(sender);
        tx2.setCreatedAt(LocalDateTime.now());

        when(transactionRepository.findAll()).thenReturn(List.of(tx1, tx2));

        List<TransactionResponse> response = adminService.getAllTransactions();
        assertEquals(2, response.size());
    }

    @Test
    public void getAllTransactions_whenNoTransactionsExist_shouldReturnEmptyList() {
        when(transactionRepository.findAll()).thenReturn(List.of());

        List<TransactionResponse> response = adminService.getAllTransactions();
        assertEquals(0, response.size());
    }

    @Test
    public void getTransactionByReference_whenTransactionExists_shouldReturnTransaction() {
        Wallet receiver = new Wallet();
        receiver.setAccountNumber("22222222222222222222");

        Transaction tx = new Transaction();
        tx.setReference("ref-123");
        tx.setType(TransactionType.FUNDING);
        tx.setStatus(TransactionStatus.SUCCESSFUL);
        tx.setAmount(new BigDecimal("2000.00"));
        tx.setReceiverWallet(receiver);
        tx.setCreatedAt(LocalDateTime.now());

        when(transactionRepository.findByReference("ref-123")).thenReturn(Optional.of(tx));

        TransactionResponse response = adminService.getTransactionByReference("ref-123");
        assertEquals("ref-123", response.getReference());
        assertEquals(new BigDecimal("2000.00"), response.getAmount());
        assertEquals(TransactionType.FUNDING, response.getType());
    }

    @Test
    public void getTransactionByReference_whenTransactionDoesNotExist_shouldThrowException() {
        String reference = "unknown-ref";
        when(transactionRepository.findByReference(reference)).thenReturn(Optional.empty());

        assertThrows(WalletException.class, () -> adminService.getTransactionByReference(reference));
    }

    @Test
    public void getTransactionsByWallet_whenWalletExists_shouldReturnTransactions() {
        Wallet wallet = new Wallet();
        wallet.setAccountNumber("11111111111111111111");

        Transaction tx = new Transaction();
        tx.setReference("ref-1");
        tx.setType(TransactionType.FUNDING);
        tx.setStatus(TransactionStatus.SUCCESSFUL);
        tx.setAmount(new BigDecimal("1000.00"));
        tx.setReceiverWallet(wallet);
        tx.setCreatedAt(LocalDateTime.now());

        when(walletRepository.findByAccountNumber(wallet.getAccountNumber())).thenReturn(Optional.of(wallet));
        when(transactionRepository.findBySenderWalletOrReceiverWalletOrderByCreatedAtDesc(wallet, wallet))
                .thenReturn(List.of(tx));

        List<TransactionResponse> response = adminService.getTransactionsByWallet(wallet.getAccountNumber());
        assertEquals(1, response.size());
        assertEquals("ref-1", response.get(0).getReference());
    }

    @Test
    public void getTransactionsByWallet_whenWalletDoesNotExist_shouldThrowException() {
        String accountNumber = "nonexistent";
        when(walletRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        assertThrows(WalletException.class, () -> adminService.getTransactionsByWallet(accountNumber));
    }

    @Test
    public void freezeWallet_whenWalletExistsAndActive_shouldFreezeWallet() {
        User user = new User();
        user.setEmail("az@gmail.com");

        Wallet wallet = new Wallet();
        wallet.setId(UUID.randomUUID());
        wallet.setAccountNumber("12345678901234567890");
        wallet.setBalance(new BigDecimal("5000.00"));
        wallet.setStatus(Status.ACTIVE);
        wallet.setUser(user);

        when(walletRepository.findByAccountNumber(wallet.getAccountNumber())).thenReturn(Optional.of(wallet));
        when(walletRepository.save(wallet)).thenReturn(wallet);

        AdminWalletResponse response = adminService.freezeWallet(wallet.getAccountNumber());
        assertEquals(Status.FROZEN, response.getStatus());
        assertEquals(Status.FROZEN, wallet.getStatus());
    }

    @Test
    public void freezeWallet_whenWalletIsAlreadyFrozen_shouldThrowException() {
        Wallet wallet = new Wallet();
        wallet.setAccountNumber("12345678901234567890");
        wallet.setStatus(Status.FROZEN);

        when(walletRepository.findByAccountNumber(wallet.getAccountNumber())).thenReturn(Optional.of(wallet));

        assertThrows(WalletException.class, () -> adminService.freezeWallet(wallet.getAccountNumber()));
    }

    @Test
    public void freezeWallet_whenWalletDoesNotExist_shouldThrowException() {
        String accountNumber = "nonexistent";
        when(walletRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        assertThrows(WalletException.class, () -> adminService.freezeWallet(accountNumber));
    }

    @Test
    public void unfreezeWallet_whenWalletExistsAndFrozen_shouldUnfreezeWallet() {
        User user = new User();
        user.setEmail("az@gmail.com");

        Wallet wallet = new Wallet();
        wallet.setId(UUID.randomUUID());
        wallet.setAccountNumber("12345678901234567890");
        wallet.setBalance(new BigDecimal("5000.00"));
        wallet.setStatus(Status.FROZEN);
        wallet.setUser(user);

        when(walletRepository.findByAccountNumber(wallet.getAccountNumber())).thenReturn(Optional.of(wallet));
        when(walletRepository.save(wallet)).thenReturn(wallet);

        AdminWalletResponse response = adminService.unfreezeWallet(wallet.getAccountNumber());
        assertEquals(Status.ACTIVE, response.getStatus());
        assertEquals(Status.ACTIVE, wallet.getStatus());
    }

    @Test
    public void unfreezeWallet_whenWalletIsAlreadyActive_shouldThrowException() {
        Wallet wallet = new Wallet();
        wallet.setAccountNumber("12345678901234567890");
        wallet.setStatus(Status.ACTIVE);

        when(walletRepository.findByAccountNumber(wallet.getAccountNumber())).thenReturn(Optional.of(wallet));

        assertThrows(WalletException.class, () -> adminService.unfreezeWallet(wallet.getAccountNumber()));
    }

    @Test
    public void unfreezeWallet_whenWalletDoesNotExist_shouldThrowException() {
        String accountNumber = "nonexistent";
        when(walletRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        assertThrows(WalletException.class, () -> adminService.unfreezeWallet(accountNumber));
    }

}