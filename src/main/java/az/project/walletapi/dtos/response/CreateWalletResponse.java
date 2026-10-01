package az.project.walletapi.dtos.response;

import az.project.walletapi.data.model.Status;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateWalletResponse {

    private String accountNumber;
    private BigDecimal balance;
    private Status status;
}
