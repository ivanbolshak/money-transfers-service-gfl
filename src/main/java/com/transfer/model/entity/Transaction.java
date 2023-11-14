package com.transfer.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transaction", schema = "transfer")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid", insertable = false, updatable = false)
    private UUID uuid;

    @Column(name = "src_account_serial", nullable = false)
    private String srcAccountSerial;

    @Column(name = "dest_account_serial", nullable = false)
    private String destAccountSerial;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "cur_dig_code", nullable = false, length = 3)
    private int currencyDigitalCode;

    @CreationTimestamp(source = SourceType.DB)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

}