package az.project.walletapi.integration;

import az.project.walletapi.data.model.User;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.dtos.request.RegisterCustomerRequest;
import az.project.walletapi.dtos.response.AdminCustomerResponse;
import az.project.walletapi.service.AdminService;
import az.project.walletapi.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class GetAllCustomersIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private AdminService adminService;

    @Test
    public void testFindAllCustomersReturnsAllCustomers() {
        RegisterCustomerRequest registerRequest = new RegisterCustomerRequest();
        registerRequest.setFirstName("Azeez");
        registerRequest.setLastName("Walter");
        registerRequest.setEmail("az@gmail.com");
        registerRequest.setPassword("password123");
        authService.registerCustomer(registerRequest);
        User firstUser = userRepository.findByEmail(registerRequest.getEmail()).orElseThrow();

        RegisterCustomerRequest SecondRegisterRequest = new RegisterCustomerRequest();
        SecondRegisterRequest.setFirstName("Azeez");
        SecondRegisterRequest.setLastName("Walter");
        SecondRegisterRequest.setEmail("banky@gmail.com");
        SecondRegisterRequest.setPassword("password123");
        authService.registerCustomer(SecondRegisterRequest);
        User secondUser = userRepository.findByEmail(SecondRegisterRequest.getEmail()).orElseThrow();

        List<AdminCustomerResponse> allCustomers = adminService.getAllCustomers();
        assertEquals(2,allCustomers.size());
    }

    @Test
    public void getAllCustomersWhenNoCustomersExistReturnEmptyList() {
        List<AdminCustomerResponse> allCustomers = adminService.getAllCustomers();
        assertEquals(0,allCustomers.size());
    }

}
