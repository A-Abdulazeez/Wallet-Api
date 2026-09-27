package az.project.walletapi.dtos.response;

import lombok.Data;

@Data
public class CustomerProfileResponse {

    private String email;
    private String firstName;
    private String lastName;
}
