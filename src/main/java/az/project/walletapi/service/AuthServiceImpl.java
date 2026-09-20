package az.project.walletapi.service;

import az.project.walletapi.data.model.User;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.dtos.request.LoginCustomerRequest;
import az.project.walletapi.dtos.request.RegisterCustomerRequest;
import az.project.walletapi.dtos.response.LoginCustomerResponse;
import az.project.walletapi.dtos.response.RegisterCustomerResponse;
import az.project.walletapi.exception.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static az.project.walletapi.utils.Mapper.map;
import static az.project.walletapi.utils.Mapper.mapToLogin;

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

    @Override
    public LoginCustomerResponse loginCustomer(LoginCustomerRequest request) {
       User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UserException("Email not Exist"));
       if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) throw new UserException("Wrong password");

       return mapToLogin(user);
    }
}
