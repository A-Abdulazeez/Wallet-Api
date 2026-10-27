package az.project.walletapi.service;

import az.project.walletapi.dtos.response.AdminCustomerResponse;

import java.util.List;

public interface AdminService {

    List<AdminCustomerResponse> getAllCustomers();


}
