package az.project.walletapi.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferFundsRequest {

    @NotBlank(message = "Account Number Cannot Be Blank")
    @Size(min = 20, max = 20, message = "Account Number Must be 20 digits")
    private String receiverAccountNumber;

    @NotNull(message= "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;
}
