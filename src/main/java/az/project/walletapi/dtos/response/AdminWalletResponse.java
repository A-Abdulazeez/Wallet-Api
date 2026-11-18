package az.project.walletapi.dtos.response;

import az.project.walletapi.data.model.Status;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AdminWalletResponse {
    private UUID id;
    private String accountNumber;
    private BigDecimal balance;
    private Status status;
    private String customerEmail;
    private LocalDateTime createdAt;
}
