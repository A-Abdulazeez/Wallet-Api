package az.project.walletapi.utils;

import az.project.walletapi.data.model.Role;
import az.project.walletapi.data.model.User;
import az.project.walletapi.dtos.request.RegisterCustomerRequest;
import az.project.walletapi.dtos.response.RegisterCustomerResponse;

public class Mapper {

    public static User map(RegisterCustomerRequest request){
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setRole(Role.CUSTOMER);

        return user;
    }

    public static RegisterCustomerResponse map(User user) {
        RegisterCustomerResponse response = new RegisterCustomerResponse();
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());

        return response;
    }
}
