package az.project.walletapi.service;

import az.project.walletapi.dtos.response.CreateWalletResponse;

public interface WalletService {

    CreateWalletResponse createWallet (String email);
}
