package az.project.walletapi.controller;

import az.project.walletapi.dtos.response.CustomerProfileResponse;
import az.project.walletapi.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/profile")
    public ResponseEntity<CustomerProfileResponse> getProfile(Authentication authentication) {
        String email = authentication.getName();

        CustomerProfileResponse response = customerService.getProfile(email);

        return ResponseEntity.ok(response);

    }
}
