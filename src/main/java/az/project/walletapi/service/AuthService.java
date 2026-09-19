package az.project.walletapi.service;

import az.project.walletapi.dtos.request.RegisterCustomerRequest;
import az.project.walletapi.dtos.response.RegisterCustomerResponse;

public interface AuthService {

    RegisterCustomerResponse registerCustomer(RegisterCustomerRequest request);
}
