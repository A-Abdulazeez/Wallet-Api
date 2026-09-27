package az.project.walletapi.service;

import az.project.walletapi.dtos.response.CustomerProfileResponse;

public interface CustomerService {

    CustomerProfileResponse getProfile(String email);
}
