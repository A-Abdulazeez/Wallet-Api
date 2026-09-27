package az.project.walletapi.data.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "wallet")
public class Wallet {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, scale = 2) // scale = 2 gives two decimal places for amounts eg 1500.75
    private BigDecimal balance;

    @OneToOne
    @JoinColumn(name = "userId", unique = true, nullable = false) // foreign key to users.id
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
