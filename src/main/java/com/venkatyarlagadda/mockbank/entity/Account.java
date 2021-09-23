package com.venkatyarlagadda.mockbank.entity;

import com.venkatyarlagadda.mockbank.dtos.request.AccountRequestDTO;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Venkat Yarlagadda
 * @version V1
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "account")
public class Account {

    @Id
    private UUID id;

    @Column(name = "account_number")
    private UUID accountNumber;

    private BigDecimal accountBalance;

    @ManyToOne
    @JoinColumn(name = "customer_number", referencedColumnName = "customer_number")
    private Customer customer;

    @Column(name = "created_on")
    protected LocalDateTime createdOn;

    @Column(name = "updated_on")
    protected LocalDateTime updatedOn;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    public static Account buildAccount(final AccountRequestDTO accountRequestDTO, final Customer customer) {
        return Account
                .builder()
                .id(UUID.randomUUID())
                .customer(customer)
                .accountNumber(UUID.randomUUID())
                .accountBalance(new BigDecimal(accountRequestDTO.getAmount()))
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }
}
