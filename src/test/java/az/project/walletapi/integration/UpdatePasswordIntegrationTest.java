package az.project.walletapi.integration;

import az.project.walletapi.data.model.User;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.dtos.request.LoginCustomerRequest;
import az.project.walletapi.dtos.request.RegisterCustomerRequest;
import az.project.walletapi.dtos.request.UpdatePasswordRequest;
import az.project.walletapi.dtos.response.CustomerProfileResponse;
import az.project.walletapi.dtos.response.LoginCustomerResponse;
import az.project.walletapi.exception.UserException;
import az.project.walletapi.service.AuthService;
import az.project.walletapi.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UpdatePasswordIntegrationTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private CustomerService customerService;

    @Test
    public void changePassword_withValidRequest_shouldPersistEncodedNewPassword() {
        RegisterCustomerRequest registerCustomer = new RegisterCustomerRequest();
        registerCustomer.setFirstName("Azeez");
        registerCustomer.setLastName("Walter");
        registerCustomer.setEmail("az@gmail.com");
        registerCustomer.setPassword("password123");
        authService.registerCustomer(registerCustomer);

        User savedUser = userRepository.findByEmail(registerCustomer.getEmail()).orElseThrow();

        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setCurrentPassword("password123");
        updatePasswordRequest.setNewPassword("changedPassword");

        customerService.updatePassword(savedUser.getEmail(), updatePasswordRequest);

        User changedUser = userRepository.findByEmail(registerCustomer.getEmail()).orElseThrow();

        assertTrue(passwordEncoder.matches("changedPassword", changedUser.getPassword()));
        assertFalse(passwordEncoder.matches("password123", changedUser.getPassword()));
    }

    @Test
    public void changePassword_withValidRequest_shouldAllowLoginWithNewPassword() {
        RegisterCustomerRequest registerCustomer = new RegisterCustomerRequest();
        registerCustomer.setFirstName("Azeez");
        registerCustomer.setLastName("Walter");
        registerCustomer.setEmail("az@gmail.com");
        registerCustomer.setPassword("password123");
        authService.registerCustomer(registerCustomer);

        User savedUser = userRepository.findByEmail(registerCustomer.getEmail()).orElseThrow();

        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setCurrentPassword("password123");
        updatePasswordRequest.setNewPassword("changedPassword");

        customerService.updatePassword(savedUser.getEmail(), updatePasswordRequest);

        LoginCustomerRequest loginCustomerRequest = new LoginCustomerRequest();
        loginCustomerRequest.setPassword("changedPassword");
        loginCustomerRequest.setEmail(savedUser.getEmail());

        LoginCustomerResponse loginResponse = authService.loginCustomer(loginCustomerRequest);
        assertNotNull(loginResponse.getToken());
        assertEquals(registerCustomer.getEmail(), loginResponse.getEmail());
    }

    @Test
    public void changePassword_withValidRequest_shouldRejectLoginWithOldPasswordAndThrowUserException() {
        RegisterCustomerRequest registerCustomer = new RegisterCustomerRequest();
        registerCustomer.setFirstName("Azeez");
        registerCustomer.setLastName("Walter");
        registerCustomer.setEmail("az@gmail.com");
        registerCustomer.setPassword("password123");
        authService.registerCustomer(registerCustomer);

        User savedUser = userRepository.findByEmail(registerCustomer.getEmail()).orElseThrow();

        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setCurrentPassword("password123");
        updatePasswordRequest.setNewPassword("changedPassword");

        customerService.updatePassword(savedUser.getEmail(), updatePasswordRequest);

        LoginCustomerRequest loginCustomerRequest = new LoginCustomerRequest();
        loginCustomerRequest.setPassword("password123");
        loginCustomerRequest.setEmail(savedUser.getEmail());

        assertThrows(UserException.class, () -> authService.loginCustomer(loginCustomerRequest));

    }

    @Test
    public void changePassword_withIncorrectCurrentPassword_shouldNotChangePersistedPasswordAndThrowUserException() {
        RegisterCustomerRequest registerCustomer = new RegisterCustomerRequest();
        registerCustomer.setFirstName("Azeez");
        registerCustomer.setLastName("Walter");
        registerCustomer.setEmail("az@gmail.com");
        registerCustomer.setPassword("password123");
        authService.registerCustomer(registerCustomer);

        User savedUser = userRepository.findByEmail(registerCustomer.getEmail()).orElseThrow();

        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setCurrentPassword("password1234");
        updatePasswordRequest.setNewPassword("changedPassword");

        assertThrows(UserException.class, () -> customerService.updatePassword(savedUser.getEmail(), updatePasswordRequest));

        User unchangedUser = userRepository.findByEmail(registerCustomer.getEmail()).orElseThrow();
        assertFalse(passwordEncoder.matches("changedPassword", unchangedUser.getPassword()));
    }
}
