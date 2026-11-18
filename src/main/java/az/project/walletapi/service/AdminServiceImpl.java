package az.project.walletapi.service;

import az.project.walletapi.data.model.Role;
import az.project.walletapi.data.model.Status;
import az.project.walletapi.data.model.Transaction;
import az.project.walletapi.data.model.User;
import az.project.walletapi.data.model.Wallet;
import az.project.walletapi.data.repository.TransactionRepository;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.data.repository.WalletRepository;
import az.project.walletapi.dtos.response.AdminCustomerResponse;
import az.project.walletapi.dtos.response.AdminWalletResponse;
import az.project.walletapi.dtos.response.TransactionResponse;
import az.project.walletapi.exception.UserException;
import az.project.walletapi.exception.WalletException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static az.project.walletapi.utils.Mapper.map;
import static az.project.walletapi.utils.Mapper.mapToAdmin;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public List<AdminCustomerResponse> getAllCustomers() {
       List<User> customers = userRepository.findAllByRole(Role.CUSTOMER);

       return customers.stream().map(az.project.walletapi.utils.Mapper::mapToAdmin).toList();
    }

    @Override
    public AdminCustomerResponse getCustomerByEmail(String email) {
        User user = userRepository.findByEmailAndRole(email, Role.CUSTOMER).orElseThrow(() -> new UserException("User not found"));
        return mapToAdmin(user);
    }

    @Override
    public List<AdminWalletResponse> getAllWallets() {
        return walletRepository.findAll().stream().map(az.project.walletapi.utils.Mapper::mapToAdmin).toList();
    }

    @Override
    public AdminWalletResponse getWalletByAccountNumber(String accountNumber) {
        Wallet wallet = walletRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new WalletException("Wallet does not exist"));
        return mapToAdmin(wallet);
    }

    @Override
    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll().stream().map(az.project.walletapi.utils.Mapper::map).toList();
    }

    @Override
    public TransactionResponse getTransactionByReference(String reference) {
        Transaction transaction = transactionRepository.findByReference(reference)
                .orElseThrow(() -> new WalletException("Transaction not found"));
        return map(transaction);
    }

    @Override
    public List<TransactionResponse> getTransactionsByWallet(String accountNumber) {
        Wallet wallet = walletRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new WalletException("Wallet does not exist"));

        return transactionRepository.findBySenderWalletOrReceiverWalletOrderByCreatedAtDesc(wallet, wallet)
                .stream()
                .map(az.project.walletapi.utils.Mapper::map)
                .toList();
    }

    @Override
    public AdminWalletResponse freezeWallet(String accountNumber) {
        Wallet wallet = walletRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new WalletException("Wallet does not exist"));

        if (wallet.getStatus() == Status.FROZEN) {
            throw new WalletException("Wallet is already frozen");
        }

        wallet.setStatus(Status.FROZEN);
        Wallet savedWallet = walletRepository.save(wallet);
        return mapToAdmin(savedWallet);
    }

    @Override
    public AdminWalletResponse unfreezeWallet(String accountNumber) {
        Wallet wallet = walletRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new WalletException("Wallet does not exist"));

        if (wallet.getStatus() == Status.ACTIVE) {
            throw new WalletException("Wallet is already active");
        }

        wallet.setStatus(Status.ACTIVE);
        Wallet savedWallet = walletRepository.save(wallet);
        return mapToAdmin(savedWallet);
    }

}


