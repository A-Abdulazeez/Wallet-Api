package az.project.walletapi.service;

import az.project.walletapi.dtos.request.FundWalletRequest;
import az.project.walletapi.dtos.response.WalletResponse;

public interface WalletService {

    WalletResponse createWallet (String email);

    WalletResponse getWallet(String email);

    WalletResponse fundWallet(String email, FundWalletRequest request);
}
