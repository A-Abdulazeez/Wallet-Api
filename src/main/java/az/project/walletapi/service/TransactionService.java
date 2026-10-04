package az.project.walletapi.service;

import az.project.walletapi.dtos.request.FundWalletRequest;
import az.project.walletapi.dtos.request.TransferFundsRequest;
import az.project.walletapi.dtos.response.TransactionResponse;
import az.project.walletapi.dtos.response.TransferResponse;
import az.project.walletapi.dtos.response.WalletResponse;

import java.util.List;

public interface TransactionService {
    WalletResponse fundWallet(String email, FundWalletRequest request);
    TransferResponse transferFunds(String senderEmail, TransferFundsRequest request);
    List<TransactionResponse> getTransactions(String email);
}
