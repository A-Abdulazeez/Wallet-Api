package az.project.walletapi.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePasswordRequest {

    @NotBlank(message = "current Password is required")
    @Size(min = 8, message = "Password Must Be At Least 8 Characters")
    private String currentPassword;

    @NotBlank(message = "new Password is required")
    @Size(min = 8, message = "Password Must Be At Least 8 Characters")
    private String newPassword;
}
