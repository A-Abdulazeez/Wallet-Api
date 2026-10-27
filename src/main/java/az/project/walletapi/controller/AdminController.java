package az.project.walletapi.controller;

import az.project.walletapi.data.repository.UserRepository;
import az.project.walletapi.dtos.response.AdminCustomerListResponse;
import az.project.walletapi.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/customers")
    public ResponseEntity<List<AdminCustomerListResponse>> getAllCustomers() {
        return ResponseEntity.ok(adminService.getAllCustomers());
    }

}
