package com.transfer.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account", schema = "transfer")
@SQLDelete(sql = "UPDATE \"transfer\".\"account\" SET \"deleted\" = true WHERE \"id\"=? AND \"opt_lock_version\"=? ") //soft delete
@Where(clause = "\"deleted\"=false")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "serial", nullable = false, unique = true)
    private String serial;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;                 //TODO balance could be implemented in a separate table with soft deletion for history

    @Version
    @Column(name = "opt_lock_version")  //optimistic lock, retry on exception
    private Long optLockVersion;

    private boolean deleted = Boolean.FALSE;

    @Column(name = "cur_dig_code", nullable = false, length = 3)
    private int currencyDigitalCode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp(source = SourceType.DB)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
    private List<Transaction> transactions;

    @Override
    public String toString() {
        return "Account{" +
                "serial='" + serial + '\'' +
                ", balance=" + balance +
                ", optLockVersion=" + optLockVersion +
                ", deleted=" + deleted +
                ", currencyDigitalCode=" + currencyDigitalCode +
                ", createdAt=" + createdAt +
                '}';
    }
}