package az.project.walletapi.service;

import az.project.walletapi.dtos.response.AdminCustomerListResponse;

import java.util.List;

public interface AdminService {

    List<AdminCustomerListResponse> getAllCustomers();


}
