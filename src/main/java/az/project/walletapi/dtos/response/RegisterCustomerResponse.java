package az.project.walletapi.dtos.response;

import lombok.Data;

@Data
public class RegisterCustomerResponse {

    private String firstName;
    private String lastName;
    private String email;
}
