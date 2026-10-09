package az.project.walletapi.service;

import az.project.walletapi.data.model.User;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.dtos.request.UpdatePasswordRequest;
import az.project.walletapi.dtos.request.UpdateProfileRequest;
import az.project.walletapi.dtos.response.CustomerProfileResponse;
import az.project.walletapi.exception.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static az.project.walletapi.utils.Mapper.mapToCustomer;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public CustomerProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("User not found"));

        return mapToCustomer(user);
    }

    @Override
    public CustomerProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("User not found"));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        User savedUser = userRepository.save(user);
        return mapToCustomer(savedUser);
    }

    @Override
    public String updatePassword(String email, UpdatePasswordRequest request) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("User not found"));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) throw new UserException("Incorrect Password");
        if (request.getNewPassword().equals(request.getCurrentPassword())) throw new UserException("Current and new Password Cannot b the same");
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        User savedUser = userRepository.save(user);

        return savedUser.getEmail() + " Password Changed Successfully";
    }
}
