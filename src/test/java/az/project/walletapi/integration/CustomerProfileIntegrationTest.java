package az.project.walletapi.integration;

import az.project.walletapi.data.model.User;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.dtos.request.RegisterCustomerRequest;
import az.project.walletapi.dtos.response.CustomerProfileResponse;
import az.project.walletapi.exception.UserException;
import az.project.walletapi.service.AuthService;
import az.project.walletapi.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class CustomerProfileIntegrationTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;


    @Test
    public void getProfile_withRegisteredCustomer_shouldReturnProfile() {
        RegisterCustomerRequest registerRequest = new RegisterCustomerRequest();
        registerRequest.setFirstName("Azeez");
        registerRequest.setLastName("Walter");
        registerRequest.setEmail("az@gmail.com");
        registerRequest.setPassword("password123");
        authService.registerCustomer(registerRequest);

        User user = userRepository.findByEmail(registerRequest.getEmail()).orElseThrow();
        CustomerProfileResponse response = customerService.getProfile(registerRequest.getEmail());
        assertEquals(user.getFirstName(), response.getFirstName());
        assertEquals(user.getLastName(), response.getLastName());
        assertEquals(user.getEmail(), response.getEmail());

    }


    @Test
    public void getProfile_withNonExistingCustomer_shouldThrowException() {
        String email = "nonexisting@gmail.com";

        assertThrows(UserException.class, () -> customerService.getProfile(email));
    }
}
