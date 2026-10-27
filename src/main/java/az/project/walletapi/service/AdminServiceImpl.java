package az.project.walletapi.service;

import az.project.walletapi.data.model.Role;
import az.project.walletapi.data.model.User;
import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.dtos.response.AdminCustomerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<AdminCustomerResponse> getAllCustomers() {
       List<User> customers = userRepository.findAllByRole(Role.CUSTOMER);

       return customers.stream().map(az.project.walletapi.utils.Mapper::mapToAdmin).toList();
    }
}


