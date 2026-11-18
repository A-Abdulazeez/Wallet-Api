package az.project.walletapi.service;

import az.project.walletapi.dtos.response.AdminCustomerResponse;
import az.project.walletapi.dtos.response.AdminWalletResponse;
import az.project.walletapi.dtos.response.TransactionResponse;

import java.util.List;

public interface AdminService {

    List<AdminCustomerResponse> getAllCustomers();
    AdminCustomerResponse getCustomerByEmail(String email);
    List<AdminWalletResponse> getAllWallets();
    AdminWalletResponse getWalletByAccountNumber(String accountNumber);
    List<TransactionResponse> getAllTransactions();
    TransactionResponse getTransactionByReference(String reference);
    List<TransactionResponse> getTransactionsByWallet(String accountNumber);
    AdminWalletResponse freezeWallet(String accountNumber);
    AdminWalletResponse unfreezeWallet(String accountNumber);
}
