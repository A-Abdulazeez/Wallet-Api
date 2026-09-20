package az.project.walletapi.integration;

import az.project.walletapi.data.model.User;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.dtos.request.LoginCustomerRequest;
import az.project.walletapi.dtos.request.RegisterCustomerRequest;
import az.project.walletapi.dtos.response.LoginCustomerResponse;
import az.project.walletapi.exception.UserException;
import az.project.walletapi.service.AuthService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class LoginIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    @Test
    public void loginCustomer_withValidCredentials_shouldLoginSuccessfully() {
        RegisterCustomerRequest registerRequest = new RegisterCustomerRequest();
        registerRequest.setFirstName("Azeez");
        registerRequest.setLastName("Walter");
        registerRequest.setEmail("az@gmail.com");
        registerRequest.setPassword("password123");
        authService.registerCustomer(registerRequest);

        LoginCustomerRequest loginRequest = new LoginCustomerRequest();
        loginRequest.setEmail(registerRequest.getEmail());
        loginRequest.setPassword(registerRequest.getPassword());

        LoginCustomerResponse response = authService.loginCustomer(loginRequest);
        assertEquals(response.getEmail(), registerRequest.getEmail());

    }

    @Test
    public void loginCustomer_whenEmailDoesNotExist_shouldThrowUserException() {
        LoginCustomerRequest loginRequest = new LoginCustomerRequest();
        loginRequest.setEmail("wrongemail@email.com");
        loginRequest.setPassword("password123");
        assertThrows(UserException.class, () ->  authService.loginCustomer(loginRequest));
    }

    @Test
    public void loginCustomer_whenPasswordDoesNotMatch_shouldThrowUserException() {
        RegisterCustomerRequest registerRequest = new RegisterCustomerRequest();
        registerRequest.setFirstName("Azeez");
        registerRequest.setLastName("Walter");
        registerRequest.setEmail("az@gmail.com");
        registerRequest.setPassword("password123");
        authService.registerCustomer(registerRequest);

        LoginCustomerRequest loginRequest = new LoginCustomerRequest();
        loginRequest.setEmail(registerRequest.getEmail());
        loginRequest.setPassword("WrongPassword");

        assertThrows(UserException.class, () -> authService.loginCustomer(loginRequest));
    }
}
