package az.project.walletapi.service;

import az.project.walletapi.dtos.response.AdminCustomerResponse;
import az.project.walletapi.dtos.response.AdminWalletResponse;

import java.util.List;

public interface AdminService {

    List<AdminCustomerResponse> getAllCustomers();
    AdminCustomerResponse getCustomerByEmail(String email);
    List<AdminWalletResponse> getAllWallets();
    AdminWalletResponse getWalletByAccountNumber(String accountNumber);
}
