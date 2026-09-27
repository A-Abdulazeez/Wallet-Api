package az.project.walletapi.data.repository;

import az.project.walletapi.data.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    Optional<Wallet> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
    Optional<Wallet> findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
}
