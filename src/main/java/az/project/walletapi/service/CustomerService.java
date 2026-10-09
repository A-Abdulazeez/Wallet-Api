package az.project.walletapi.service;

import az.project.walletapi.dtos.request.UpdatePasswordRequest;
import az.project.walletapi.dtos.request.UpdateProfileRequest;
import az.project.walletapi.dtos.response.CustomerProfileResponse;

public interface CustomerService {

    CustomerProfileResponse getProfile(String email);
    CustomerProfileResponse updateProfile(String email, UpdateProfileRequest request);
    String updatePassword(String email, UpdatePasswordRequest request);
}
