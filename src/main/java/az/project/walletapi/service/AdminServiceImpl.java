package az.project.walletapi.service;

import az.project.walletapi.data.model.Role;
import az.project.walletapi.data.model.User;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.dtos.response.AdminCustomerResponse;
import az.project.walletapi.exception.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static az.project.walletapi.utils.Mapper.map;
import static az.project.walletapi.utils.Mapper.mapToAdmin;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<AdminCustomerResponse> getAllCustomers() {
       List<User> customers = userRepository.findAllByRole(Role.CUSTOMER);

       return customers.stream().map(az.project.walletapi.utils.Mapper::mapToAdmin).toList();
    }

    @Override
    public AdminCustomerResponse getCustomerByEmail(String email) {
        User user = userRepository.findByEmailAndRole(email, Role.CUSTOMER).orElseThrow(() -> new UserException("User not found"));
        return mapToAdmin(user);
    }

}


