package az.project.walletapi.dtos.response;

import az.project.walletapi.data.model.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AdminCustomerResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
}
