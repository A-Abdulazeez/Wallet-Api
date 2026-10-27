package az.project.walletapi.service;

import az.project.walletapi.data.model.Role;
import az.project.walletapi.data.model.User;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.dtos.response.AdminCustomerResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    public void getAllCustomers_whenCustomersExist_shouldReturnCustomerList() {
        User customer1 = new User();
        customer1.setId(UUID.randomUUID());
        customer1.setFirstName("Azeez");
        customer1.setLastName("Walter");
        customer1.setEmail("az@gmail.com");
        customer1.setRole(Role.CUSTOMER);

        User customer2 = new User();
        customer2.setId(UUID.randomUUID());
        customer2.setFirstName("Banky");
        customer2.setLastName("Williams");
        customer2.setEmail("banky@gmail.com");
        customer2.setRole(Role.CUSTOMER);

        when(userRepository.findAllByRole(Role.CUSTOMER)).thenReturn(List.of(customer1, customer2));

        List<AdminCustomerResponse> response = adminService.getAllCustomers();
        assertEquals(2, response.size());

    }

    @Test
    public void getAllCustomers_whenNoCustomersExist_shouldReturnEmptyList() {
//        when(userRepository.findAll()).thenReturn(List.of());
        List<AdminCustomerResponse> response = adminService.getAllCustomers();
        assertEquals(0, response.size());
    }

}