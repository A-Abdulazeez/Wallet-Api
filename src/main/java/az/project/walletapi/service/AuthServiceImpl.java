package az.project.walletapi.service;

import az.project.walletapi.data.model.User;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.dtos.request.RegisterCustomerRequest;
import az.project.walletapi.dtos.response.RegisterCustomerResponse;
import az.project.walletapi.exception.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static az.project.walletapi.utils.Mapper.map;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public RegisterCustomerResponse registerCustomer(RegisterCustomerRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) throw new UserException("Email already exists");

        User user = map(request);

        String hashPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(hashPassword);

        User savedUser = userRepository.save(user);

        return map(savedUser);
    }
}
