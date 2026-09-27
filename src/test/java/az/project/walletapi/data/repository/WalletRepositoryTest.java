package az.project.walletapi.data.repository;

import az.project.walletapi.data.model.Role;
import az.project.walletapi.data.model.Status;
import az.project.walletapi.data.model.User;
import az.project.walletapi.data.model.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class WalletRepositoryTest {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void saveWallet_shouldPersistSavedWallet() {
        User user = new User();
        user.setFirstName("Azeez");
        user.setLastName("Walter");
        user.setEmail("az@gmail.com");
        user.setPassword("hashedPassword");
        user.setRole(Role.CUSTOMER);
        User savedUser = userRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setAccountNumber("1234567890");
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setStatus(Status.ACTIVE);
        wallet.setUser(savedUser);
        Wallet savedWallet = walletRepository.save(wallet);

        assertEquals("1234567890", savedWallet.getAccountNumber());
        assertEquals(savedUser.getId(), savedWallet.getUser().getId());
    }

    @Test
    public void findByUserId_whenWalletExists_shouldReturnWallet() {
        User user = new User();
        user.setFirstName("Azeez");
        user.setLastName("Walter");
        user.setEmail("az@gmail.com");
        user.setPassword("hashedPassword");
        user.setRole(Role.CUSTOMER);
        User savedUser = userRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setAccountNumber("1234567890");
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setStatus(Status.ACTIVE);
        wallet.setUser(savedUser);
        walletRepository.save(wallet);

        Wallet foundWallet = walletRepository.findByUserId(user.getId()).orElseThrow();

        assertEquals(wallet.getId(), foundWallet.getId());
        assertEquals("1234567890", foundWallet.getAccountNumber());
        assertEquals(savedUser.getId(), foundWallet.getUser().getId());
    }

    @Test
    public void existsByUserId_whenWalletExists_shouldReturnTrue() {
        User user = new User();
        user.setFirstName("Azeez");
        user.setLastName("Walter");
        user.setEmail("az@gmail.com");
        user.setPassword("hashedPassword");
        user.setRole(Role.CUSTOMER);
        User savedUser = userRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setAccountNumber("1234567890");
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setStatus(Status.ACTIVE);
        wallet.setUser(savedUser);
        walletRepository.save(wallet);

        assertTrue(walletRepository.existsByUserId(user.getId()));

    }

    @Test
    public void findByAccountNumber_whenWalletExists_shouldReturnWallet() {
        User user = new User();
        user.setFirstName("Azeez");
        user.setLastName("Walter");
        user.setEmail("az@gmail.com");
        user.setPassword("hashedPassword");
        user.setRole(Role.CUSTOMER);
        User savedUser = userRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setAccountNumber("1234567890");
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setStatus(Status.ACTIVE);
        wallet.setUser(savedUser);
        walletRepository.save(wallet);

        Wallet foundWallet = walletRepository.findByAccountNumber(wallet.getAccountNumber()).orElseThrow();

        assertEquals(wallet.getId(), foundWallet.getId());
    }

    @Test
    public void existsByAccountNumber_whenWalletExists_shouldReturnTrue() {
        User user = new User();
        user.setFirstName("Azeez");
        user.setLastName("Walter");
        user.setEmail("az@gmail.com");
        user.setPassword("hashedPassword");
        user.setRole(Role.CUSTOMER);
        User savedUser = userRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setAccountNumber("1234567890");
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setStatus(Status.ACTIVE);
        wallet.setUser(savedUser);
        walletRepository.save(wallet);

        assertTrue(walletRepository.existsByAccountNumber(wallet.getAccountNumber()));

    }

}