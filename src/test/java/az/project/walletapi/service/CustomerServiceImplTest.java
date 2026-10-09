package az.project.walletapi.service;

import az.project.walletapi.data.model.User;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.dtos.request.UpdatePasswordRequest;
import az.project.walletapi.dtos.request.UpdateProfileRequest;
import az.project.walletapi.dtos.response.CustomerProfileResponse;
import az.project.walletapi.exception.UserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

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

    @Test
    public void updateProfile_withExistingEmail_shouldReturnUpdatedCustomerProfile() {
        User user = new User();
        user.setEmail("banky@gmail.com");
        user.setFirstName("banky");
        user.setLastName("w");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("Changed");
        request.setLastName("name");

        CustomerProfileResponse response = customerService.updateProfile(user.getEmail(), request);
        assertEquals(user.getFirstName(), response.getFirstName());
        assertEquals(user.getLastName(), response.getLastName());
        assertEquals(user.getEmail(), response.getEmail());

        verify(userRepository).save(user);
    }

    @Test
    public void updateProfile_withNonExistingEmail_shouldThrowUserException() {
        String email = "rubbishmail@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("Changed");
        request.setLastName("name");
        assertThrows(UserException.class, () -> customerService.updateProfile(email, request));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void changePassword_withValidCurrentPassword_shouldUpdatePassword() {
        User user = new User();
        user.setEmail("banky@gmail.com");
        user.setFirstName("banky");
        user.setLastName("w");
        user.setPassword("encoded-old-password");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password123", "encoded-old-password")).thenReturn(true);

        when(passwordEncoder.encode("newPassword123")).thenReturn("encoded-new-password");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setCurrentPassword("password123");
        request.setNewPassword("newPassword123");

        customerService.updatePassword(user.getEmail(), request);

        assertEquals("encoded-new-password", user.getPassword());

        verify(passwordEncoder).matches("password123", "encoded-old-password");
        verify(passwordEncoder).encode("newPassword123");
        verify(userRepository).save(user);
    }

    @Test
    public void changePassword_withNonExistingUser_shouldThrowUserException() {
        String email = "rubbishmail@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setCurrentPassword("password123");
        request.setNewPassword("newPassword123");
        assertThrows(UserException.class, () -> customerService.updatePassword(email, request));
    }

    @Test
    public void changePassword_withIncorrectCurrentPassword_shouldThrowUserException() {
        User user = new User();
        user.setEmail("banky@gmail.com");
        user.setFirstName("banky");
        user.setLastName("w");
        user.setPassword("encoded-old-password");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password123", "encoded-old-password")).thenReturn(false);

        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setCurrentPassword("password123");
        request.setNewPassword("newPassword123");

        assertThrows(UserException.class, () -> customerService.updatePassword(user.getEmail(), request));

    }

    @Test
    public void changePassword_withSameCurrentAndNewPassword_shouldThrowUserException() {
        User user = new User();
        user.setEmail("banky@gmail.com");
        user.setFirstName("banky");
        user.setLastName("w");
        user.setPassword("encoded-old-password");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password123", "encoded-old-password")).thenReturn(true);

        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setCurrentPassword("password123");
        request.setNewPassword("password123");

        assertThrows(UserException.class, () -> customerService.updatePassword(user.getEmail(), request));

    }

}