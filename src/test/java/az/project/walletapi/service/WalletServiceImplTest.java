package az.project.walletapi.service;

import az.project.walletapi.data.model.Status;
import az.project.walletapi.data.model.User;
import az.project.walletapi.data.model.Wallet;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.data.repository.WalletRepository;
import az.project.walletapi.dtos.request.FundWalletRequest;
import az.project.walletapi.dtos.response.WalletResponse;
import az.project.walletapi.exception.UserException;
import az.project.walletapi.exception.WalletException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WalletServiceImpl walletService;


    @Test
    public void createWallet_withValidUser_shouldCreateWallet() {
        User user =  new User();
        user.setEmail("Kesirat@gmail.com");
        user.setFirstName("Kesirat");
        user.setLastName("Amoke");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        when(walletRepository.existsByUserId(user.getId())).thenReturn(false);
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(walletRepository.existsByAccountNumber(anyString())).thenReturn(false);

        WalletResponse response = walletService.createWallet(user.getEmail());
        assertNotNull(response.getAccountNumber());
        assertNotNull(response);
        assertEquals(20, response.getAccountNumber().length());

        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    public void createWallet_withNonExistingUser_shouldThrowUserException() {
        String email = "testing@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThrows(UserException.class, () -> walletService.createWallet(email));

        verify(walletRepository, never()).save(any(Wallet.class));

    }

    @Test
    public void createWallet_whenUserAlreadyHasWallet_shouldThrowWalletException() {
        User user =  new User();
        user.setEmail("testing@gmail.com");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        when(walletRepository.existsByUserId(user.getId())).thenReturn(true);
        assertThrows(WalletException.class, () -> walletService.createWallet(user.getEmail()));

        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    public void getWallet_withExistingUserAndWallet_shouldReturnWallet() {
        User user =  new User();
        user.setEmail("Kesirat@gmail.com");
        user.setFirstName("Kesirat");
        user.setLastName("Amoke");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Wallet wallet = new Wallet();
        wallet.setAccountNumber("12345678901234567890");
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setStatus(Status.ACTIVE);
        wallet.setUser(user);

        when(walletRepository.findByUserId(user.getId())).thenReturn(Optional.of(wallet));

        WalletResponse response = walletService.getWallet(user.getEmail());
        assertEquals(20, response.getAccountNumber().length());
        assertEquals(0, BigDecimal.ZERO.compareTo(response.getBalance()));
        assertNotNull(response);
    }

    @Test
    public void getWallet_withNonExistingUser_shouldThrowUserException() {
        String email = "testing@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThrows(UserException.class, () -> walletService.getWallet(email));
    }

    @Test
    public void getWallet_withExistingUserButNoWallet_shouldThrowWalletException() {
        User user =  new User();
        user.setEmail("Tosin@gmail.com");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(walletRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        assertThrows(WalletException.class, () -> walletService.getWallet(user.getEmail()));
    }

    @Test
    public void fundWallet_withValidAmount_shouldUpdateBalance() {
        User user =  new User();
        user.setEmail("Kesirat@gmail.com");
        user.setFirstName("Kesirat");
        user.setLastName("Amoke");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Wallet wallet = new Wallet();
        wallet.setAccountNumber("12345678901234567890");
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setStatus(Status.ACTIVE);
        wallet.setUser(user);

        when(walletRepository.findByUserId(user.getId())).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FundWalletRequest request = new FundWalletRequest();
        request.setAmount(new  BigDecimal("10000"));

        WalletResponse walletResponse = walletService.fundWallet(user.getEmail(), request);
        assertNotNull(walletResponse);
        assertEquals(10000, walletResponse.getBalance().intValue());

        verify(walletRepository).save(wallet);
    }

    @Test
    public void fundWallet_withNonExistingUser_shouldThrowUserException() {
        String userEmail = "fakeemail@gmail.com";
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        FundWalletRequest request = new FundWalletRequest();
        request.setAmount(new  BigDecimal("10000"));

        assertThrows(UserException.class, () -> walletService.fundWallet(userEmail, request));

    }

    @Test
    public void fundWallet_withExistingUserButNoWallet_shouldThrowWalletException() {
        User user =  new User();
        user.setEmail("Kesirat@gmail.com");
        user.setFirstName("Kesirat");
        user.setLastName("Amoke");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        when(walletRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        FundWalletRequest request = new FundWalletRequest();
        request.setAmount(new  BigDecimal("10000"));
        assertThrows(WalletException.class, () -> walletService.fundWallet(user.getEmail(), request));
    }

    @Test
    public void fundWallet_withZeroAmount_shouldThrowWalletException() {
        User user =  new User();
        user.setEmail("Kesirat@gmail.com");
        user.setFirstName("Kesirat");
        user.setLastName("Amoke");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Wallet wallet = new Wallet();
        wallet.setAccountNumber("12345678901234567890");
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setStatus(Status.ACTIVE);
        wallet.setUser(user);

        when(walletRepository.findByUserId(user.getId())).thenReturn(Optional.of(wallet));

        FundWalletRequest request = new FundWalletRequest();
        request.setAmount(new  BigDecimal("0"));
        assertThrows(WalletException.class, () -> walletService.fundWallet(user.getEmail(), request));
    }

    @Test
    public void fundWallet_withNegativeAmount_shouldThrowWalletException() {
        User user =  new User();
        user.setEmail("Kesirat@gmail.com");
        user.setFirstName("Kesirat");
        user.setLastName("Amoke");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Wallet wallet = new Wallet();
        wallet.setAccountNumber("12345678901234567890");
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setStatus(Status.ACTIVE);
        wallet.setUser(user);

        when(walletRepository.findByUserId(user.getId())).thenReturn(Optional.of(wallet));

        FundWalletRequest request = new FundWalletRequest();
        request.setAmount(new BigDecimal("-1000"));
        assertThrows(WalletException.class, () -> walletService.fundWallet(user.getEmail(), request));
    }
}