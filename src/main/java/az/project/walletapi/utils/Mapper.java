package az.project.walletapi.utils;

import az.project.walletapi.data.model.Role;
import az.project.walletapi.data.model.Status;
import az.project.walletapi.data.model.User;
import az.project.walletapi.data.model.Wallet;
import az.project.walletapi.data.model.Transaction;
import az.project.walletapi.dtos.request.RegisterCustomerRequest;
import az.project.walletapi.dtos.response.*;

import java.math.BigDecimal;

public class Mapper {

    public static User map(RegisterCustomerRequest request){
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setRole(Role.CUSTOMER);

        return user;
    }

    public static RegisterCustomerResponse map(User user) {
        RegisterCustomerResponse response = new RegisterCustomerResponse();
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());

        return response;
    }

    public static LoginCustomerResponse map(User user, String token) {
        LoginCustomerResponse response = new LoginCustomerResponse();
        response.setEmail(user.getEmail());
        response.setToken(token);
        response.setMessage(user.getFirstName() + " Logged in successfully");
        return response;
    }

    public static CustomerProfileResponse mapToCustomer(User user) {
        CustomerProfileResponse response = new CustomerProfileResponse();
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());

        return response;
    }

    public static WalletResponse map(Wallet wallet) {
        WalletResponse response = new WalletResponse();
        response.setAccountNumber(wallet.getAccountNumber());
        response.setBalance(wallet.getBalance());
        response.setStatus(Status.ACTIVE);
        return response;
    }

    public static TransferResponse map(Wallet senderWallet, Wallet receiverWallet, BigDecimal amount, String reference) {
        TransferResponse response = new TransferResponse();
        response.setSenderAccountNumber(senderWallet.getAccountNumber());
        response.setReceiverAccountNumber(receiverWallet.getAccountNumber());
        response.setAmount(amount);
        response.setSenderBalance(senderWallet.getBalance());
        response.setReference(reference);
        response.setMessage("Transfer successful");

        return response;
    }
    public static TransactionResponse map(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setReference(transaction.getReference());
        response.setType(transaction.getType());
        response.setStatus(transaction.getStatus());
        response.setAmount(transaction.getAmount());
        response.setCreatedAt(transaction.getCreatedAt());
        if (transaction.getSenderWallet() != null) {
            response.setSenderAccountNumber(transaction.getSenderWallet().getAccountNumber());
        }
        response.setReceiverAccountNumber(transaction.getReceiverWallet().getAccountNumber());
        return response;
    }

}
