package az.project.walletapi.service;

import az.project.walletapi.data.model.*;
import az.project.walletapi.data.repository.TransactionRepository;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.data.repository.WalletRepository;
import az.project.walletapi.dtos.request.FundWalletRequest;
import az.project.walletapi.dtos.request.TransferFundsRequest;
import az.project.walletapi.dtos.response.TransactionResponse;
import az.project.walletapi.dtos.response.TransferResponse;
import az.project.walletapi.dtos.response.WalletResponse;
import az.project.walletapi.exception.UserException;
import az.project.walletapi.exception.WalletException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static az.project.walletapi.utils.Mapper.*;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;


    @Transactional
    @Override
    public WalletResponse fundWallet(String email, FundWalletRequest request) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("User not found"));
        Wallet wallet = walletRepository.findByUserId(user.getId()).orElseThrow(() -> new WalletException("Wallet does not exist"));

        validateAmount(request.getAmount());
        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        Wallet updatedWallet = walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.FUNDING);
        transaction.setStatus(TransactionStatus.SUCCESSFUL);
        transaction.setAmount(request.getAmount());
        transaction.setReceiverWallet(updatedWallet);
        transactionRepository.save(transaction);

        return map(updatedWallet);
    }

    @Transactional
    @Override
    public TransferResponse transferFunds(String senderEmail, TransferFundsRequest request) {
        User user = userRepository.findByEmail(senderEmail).orElseThrow(() -> new UserException("User not found"));
        Wallet senderWallet = walletRepository.findByUserId(user.getId()).orElseThrow(() -> new WalletException("Wallet does not exist"));
        Wallet receiverWallet = walletRepository.findByAccountNumber(request.getReceiverAccountNumber()).orElseThrow(() -> new WalletException("Receiver Wallet does not exist"));

        if (senderWallet.getAccountNumber().equals(receiverWallet.getAccountNumber())) throw new WalletException("Cannot transfer to the same wallet");
        validateAmount(request.getAmount());
        if (senderWallet.getBalance().compareTo(request.getAmount()) < 0) throw new WalletException("Insufficient balance");

        senderWallet.setBalance(senderWallet.getBalance().subtract(request.getAmount()));
        receiverWallet.setBalance(receiverWallet.getBalance().add(request.getAmount()));

        Wallet updatedSenderWallet = walletRepository.save(senderWallet);
        Wallet updatedReceiverWallet = walletRepository.save(receiverWallet);

        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.SUCCESSFUL);
        transaction.setAmount(request.getAmount());
        transaction.setSenderWallet(updatedSenderWallet);
        transaction.setReceiverWallet(updatedReceiverWallet);
        Transaction savedTransaction = transactionRepository.save(transaction);

        return map(updatedSenderWallet, updatedReceiverWallet, request.getAmount(), savedTransaction.getReference());
    }

    @Transactional(readOnly = true)
    @Override
    public List<TransactionResponse> getTransactions(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("User not found"));
        Wallet wallet = walletRepository.findByUserId(user.getId()).orElseThrow(() -> new WalletException("Wallet does not exist"));

        return transactionRepository
                .findBySenderWalletOrReceiverWalletOrderByCreatedAtDesc(wallet, wallet)
                .stream()
                .map(az.project.walletapi.utils.Mapper::map)
                .toList();
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new WalletException("Amount must be greater than zero");
    }
}
