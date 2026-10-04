package az.project.walletapi.service;

import az.project.walletapi.dtos.response.WalletResponse;

public interface WalletService {
    WalletResponse createWallet(String email);
    WalletResponse getWallet(String email);
}
