package az.project.walletapi.controller;

import az.project.walletapi.dtos.request.LoginCustomerRequest;
import az.project.walletapi.dtos.request.RegisterCustomerRequest;
import az.project.walletapi.dtos.response.LoginCustomerResponse;
import az.project.walletapi.dtos.response.RegisterCustomerResponse;
import az.project.walletapi.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/register")
    public ResponseEntity<RegisterCustomerResponse> registerCustomer(@Valid @RequestBody RegisterCustomerRequest request){
        RegisterCustomerResponse response = authService.registerCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginCustomerResponse> login(@Valid @RequestBody LoginCustomerRequest request){
        LoginCustomerResponse response = authService.loginCustomer(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
