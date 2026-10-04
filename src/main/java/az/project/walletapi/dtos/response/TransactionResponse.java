package az.project.walletapi.dtos.response;

import az.project.walletapi.data.model.TransactionStatus;
import az.project.walletapi.data.model.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private String reference;
    private TransactionType type;
    private TransactionStatus status;
    private BigDecimal amount;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private LocalDateTime createdAt;
}
