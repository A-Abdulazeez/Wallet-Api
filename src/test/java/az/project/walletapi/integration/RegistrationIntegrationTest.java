package az.project.walletapi.integration;

import az.project.walletapi.data.model.Role;
import az.project.walletapi.data.model.User;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.dtos.request.RegisterCustomerRequest;
import az.project.walletapi.dtos.response.RegisterCustomerResponse;
import az.project.walletapi.exception.UserException;
import az.project.walletapi.service.AuthService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class RegistrationIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthService authService;

    @Test
    public void registerCustomer_withValidRequest_shouldPersistCustomerWithHashedPassword() {
        RegisterCustomerRequest request = new RegisterCustomerRequest();
        request.setFirstName("Azeez");
        request.setLastName("Walter");
        request.setEmail("az@gmail.com");
        request.setPassword("password123");

        RegisterCustomerResponse response = authService.registerCustomer(request);
        User savedUser = userRepository.findByEmail(request.getEmail()).orElseThrow();

        assertNotEquals(request.getPassword(), savedUser.getPassword());
        assertTrue(passwordEncoder.matches(request.getPassword(), savedUser.getPassword()));

    }

    @Test
    public void registerCustomer_whenEmailAlreadyExists_shouldThrowUserException() {
        RegisterCustomerRequest request = new RegisterCustomerRequest();
        request.setFirstName("Azeez");
        request.setLastName("Walter");
        request.setEmail("az@gmail.com");
        request.setPassword("password123");
        authService.registerCustomer(request);

        RegisterCustomerRequest request2 = new RegisterCustomerRequest();
        request2.setFirstName("Azeez");
        request2.setLastName("Blater");
        request2.setEmail("az@gmail.com");
        request2.setPassword("password123");
        assertThrows(UserException.class, () -> authService.registerCustomer(request2));
        assertEquals(1, userRepository.count());
    }

    @Test
    public void registerCustomer_withValidRequest_shouldGenerateIdAndCreatedAt() {
        RegisterCustomerRequest request = new RegisterCustomerRequest();
        request.setFirstName("Azeez");
        request.setLastName("Walter");
        request.setEmail("az@gmail.com");
        request.setPassword("password123");
        authService.registerCustomer(request);

        User savedUser = userRepository.findByEmail(request.getEmail()).orElseThrow();
        assertNotNull(savedUser.getId());
        assertNotNull(savedUser.getCreatedAt());
        assertEquals(Role.CUSTOMER, savedUser.getRole());

    }
}
