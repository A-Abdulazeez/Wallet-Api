package az.project.walletapi.service;

import az.project.walletapi.data.model.User;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.dtos.response.CustomerProfileResponse;
import az.project.walletapi.exception.UserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    public void getProfile_withExistingEmail_shouldReturnCustomerProfile() {
        User user = new User();
        user.setEmail("banky@gmail.com");
        user.setFirstName("banky");
        user.setLastName("w");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        CustomerProfileResponse response = customerService.getProfile(user.getEmail());
        assertEquals(user.getFirstName(), response.getFirstName());
        assertEquals(user.getLastName(), response.getLastName());
        assertEquals(user.getEmail(), response.getEmail());

    }

    @Test
    public void getProfile_withNonExistingEmail_shouldThrowUserException() {
        String email = "rubbishmail@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserException.class, () -> customerService.getProfile(email));
    }

}