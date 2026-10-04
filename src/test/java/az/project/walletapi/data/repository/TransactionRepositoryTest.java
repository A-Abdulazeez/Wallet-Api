package az.project.walletapi.data.repository;

import az.project.walletapi.data.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TransactionRepositoryTest {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    public void saveTransaction_shouldPersistTransaction() {
        Wallet wallet = persistWallet("transaction-test@gmail.com", "12345678901234567890");
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.FUNDING);
        transaction.setStatus(TransactionStatus.SUCCESSFUL);
        transaction.setAmount(new BigDecimal("500"));
        transaction.setReceiverWallet(wallet);

        Transaction saved = transactionRepository.saveAndFlush(transaction);

        assertNotNull(saved.getId());
        assertNotNull(saved.getReference());
        assertNotNull(saved.getCreatedAt());
        assertTrue(transactionRepository.findByReference(saved.getReference()).isPresent());
    }

    private Wallet persistWallet(String email, String accountNumber) {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail(email);
        user.setPassword("hashed-password");
        user.setRole(Role.CUSTOMER);
        user = userRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setAccountNumber(accountNumber);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setStatus(Status.ACTIVE);
        wallet.setUser(user);
        return walletRepository.save(wallet);
    }
}
