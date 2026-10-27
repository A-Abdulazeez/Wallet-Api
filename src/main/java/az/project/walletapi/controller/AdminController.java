package az.project.walletapi.controller;

import az.project.walletapi.dtos.response.AdminCustomerResponse;
import az.project.walletapi.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/customers")
    public ResponseEntity<List<AdminCustomerResponse>> getAllCustomers() {
        return ResponseEntity.ok(adminService.getAllCustomers());
    }

}
