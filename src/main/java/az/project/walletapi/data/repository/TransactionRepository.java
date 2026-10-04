package az.project.walletapi.data.repository;

import az.project.walletapi.data.model.Transaction;
import az.project.walletapi.data.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Optional<Transaction> findByReference(String reference);
    List<Transaction> findBySenderWalletOrReceiverWalletOrderByCreatedAtDesc(Wallet senderWallet, Wallet receiverWallet);
}
