package az.project.walletapi.service;

import az.project.walletapi.data.model.Status;
import az.project.walletapi.data.model.User;
import az.project.walletapi.data.model.Wallet;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.data.repository.WalletRepository;
import az.project.walletapi.dtos.response.CreateWalletResponse;
import az.project.walletapi.exception.UserException;
import az.project.walletapi.exception.WalletException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;

import static az.project.walletapi.utils.Mapper.map;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;


    @Override
    public CreateWalletResponse createWallet(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("User not found"));

        if (walletRepository.existsByUserId(user.getId())) throw new WalletException("Wallet already exists");

        Wallet wallet = new Wallet();
        wallet.setAccountNumber(generateAccountNumber());
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setStatus(Status.ACTIVE);
        wallet.setUser(user);

        Wallet savedWallet = walletRepository.save(wallet);

        return map(savedWallet);
    }

    @Override
    public CreateWalletResponse getWallet(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("User not found"));
        Wallet foundWallet = walletRepository.findByUserId(user.getId()).orElseThrow(() -> new WalletException("Wallet does not exist"));

        return map(foundWallet);
    }

    private String generateAccountNumber() {
        SecureRandom secureRandom = new SecureRandom();
        String accountnumber;
        do {
            StringBuilder builder = new StringBuilder();

            builder.append(secureRandom.nextInt(9) + 1);

            for (int count = 0; count < 19; count++) {
                builder.append(secureRandom.nextInt(10));
            }

            accountnumber = builder.toString();
        }
        while (walletRepository.existsByAccountNumber(accountnumber));

        return accountnumber;
    }
}
