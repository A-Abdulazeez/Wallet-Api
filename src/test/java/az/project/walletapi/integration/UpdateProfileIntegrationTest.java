package az.project.walletapi.integration;

import az.project.walletapi.data.model.User;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.dtos.request.RegisterCustomerRequest;
import az.project.walletapi.dtos.request.UpdateProfileRequest;
import az.project.walletapi.dtos.response.CustomerProfileResponse;
import az.project.walletapi.exception.UserException;
import az.project.walletapi.service.AuthService;
import az.project.walletapi.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class UpdateProfileIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AuthService authService;

    @Test
    public void updateProfile_withValidRequest_shouldPersistUpdatedProfile() {
        RegisterCustomerRequest registerCustomer = new RegisterCustomerRequest();
        registerCustomer.setFirstName("Azeez");
        registerCustomer.setLastName("Walter");
        registerCustomer.setEmail("az@gmail.com");
        registerCustomer.setPassword("password123");
        authService.registerCustomer(registerCustomer);

        User savedUser = userRepository.findByEmail(registerCustomer.getEmail()).orElseThrow();
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("Changed");
        request.setLastName("name");

        CustomerProfileResponse response = customerService.updateProfile(savedUser.getEmail(), request);

        User updatedUser = userRepository.findByEmail(savedUser.getEmail()).orElseThrow();

        assertEquals("Changed", response.getFirstName());
        assertEquals("name", response.getLastName());

        assertEquals("Changed", updatedUser.getFirstName());
        assertEquals("name", updatedUser.getLastName());
        assertEquals("az@gmail.com", updatedUser.getEmail());
    }

    @Test
    public void updateProfile_withNonExistingUser_shouldThrowUserException() {
        String email = "non-existing-email@gmail.com";
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("Changed");
        request.setLastName("name");
        assertThrows(UserException.class,  () -> customerService.updateProfile(email, request));

    }
}
