package az.project.walletapi.dtos.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferResponse {
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private BigDecimal amount;
    private BigDecimal senderBalance;
    private String reference;
    private String message;
}
