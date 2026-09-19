package az.project.walletapi.service;

import az.project.walletapi.data.model.User;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.dtos.request.RegisterCustomerRequest;
import az.project.walletapi.dtos.response.RegisterCustomerResponse;
import az.project.walletapi.exception.UserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    public void registerCustomer_withValidRequest_shouldRegisterCustomer() {
        RegisterCustomerRequest request = new RegisterCustomerRequest();
        request.setFirstName("Azeez");
        request.setLastName("Walter");
        request.setEmail("az@gmail.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RegisterCustomerResponse response = authService.registerCustomer(request);
        assertEquals(request.getFirstName(), response.getFirstName());
        assertEquals(request.getLastName(), response.getLastName());
        assertEquals(request.getEmail(), response.getEmail());

        verify(userRepository).save(any(User.class));

    }

    @Test
    public void registerCustomer_whenEmailAlreadyExists_shouldThrowUserException() {
        RegisterCustomerRequest request = new RegisterCustomerRequest();
        request.setFirstName("Azeez");
        request.setLastName("Walter");
        request.setEmail("az@gmail.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);
        assertThrows(UserException.class, () -> authService.registerCustomer(request));

        verify(userRepository, never()).save(any(User.class));

    }

    @Test
    public void registerCustomer_shouldEncodePasswordBeforeSaving() {
        RegisterCustomerRequest request = new RegisterCustomerRequest();
        request.setFirstName("Azeez");
        request.setLastName("Walter");
        request.setEmail("az@gmail.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RegisterCustomerResponse response = authService.registerCustomer(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User user = captor.getValue();
        assertEquals("hashedPassword", user.getPassword());
        assertEquals(request.getEmail(), response.getEmail());
    }
}