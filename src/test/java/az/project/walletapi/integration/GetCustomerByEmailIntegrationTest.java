package az.project.walletapi.integration;

import az.project.walletapi.data.model.Role;
import az.project.walletapi.data.model.User;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.dtos.request.RegisterCustomerRequest;
import az.project.walletapi.dtos.response.AdminCustomerResponse;
import az.project.walletapi.exception.UserException;
import az.project.walletapi.service.AdminService;
import az.project.walletapi.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class GetCustomerByEmailIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private AdminService adminService;

    @Test
    void getCustomerByEmail_whenCustomerExists_shouldReturnCustomer() {
        RegisterCustomerRequest registerRequest = new RegisterCustomerRequest();
        registerRequest.setFirstName("Azeez");
        registerRequest.setLastName("Walter");
        registerRequest.setEmail("az@gmail.com");
        registerRequest.setPassword("password123");
        authService.registerCustomer(registerRequest);
        User customer = userRepository.findByEmail(registerRequest.getEmail()).orElseThrow();

        AdminCustomerResponse response = adminService.getCustomerByEmail(customer.getEmail());
        assertEquals(customer.getEmail(), response.getEmail());
        assertEquals(customer.getId(), response.getId());

    }

    @Test
    public void getCustomerByEmail_whenCustomerDoesNotExist_shouldRThrowException() {
        String email = "testing@mail.com";
        assertThrows(UserException.class, ()-> adminService.getCustomerByEmail(email));
    }

    @Test
    public void getCustomerByEmail_whenUserIsAdmin_shouldReturnNotFound() {
        User admin = new User();
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setEmail("admin@test.com");
        admin.setPassword("encodedPassword");
        admin.setRole(Role.ADMIN);

        userRepository.save(admin);

        assertThrows(UserException.class, () -> adminService.getCustomerByEmail(admin.getEmail()));
    }
}
