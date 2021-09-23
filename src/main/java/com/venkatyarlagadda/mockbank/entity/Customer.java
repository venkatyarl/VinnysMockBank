package com.venkatyarlagadda.mockbank.entity;

import com.venkatyarlagadda.mockbank.dtos.request.CustomerRequestDTO;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
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
@Table(name = "customer")
public class Customer implements Serializable {

    @Column(nullable = false)
    @Id
    private UUID id;

    @NotNull
    @Column(name = "customer_number")
    private UUID customerNumber;

    @Email
    private String email;

    private String password;
    private boolean locked;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "num_of_failed_attempts")
    private int numOfFailedAttempts;

    @Column(name = "created_on")
    protected LocalDateTime createdOn;

    @Column(name = "updated_on")
    protected LocalDateTime updatedOn;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    public static Customer buildCustomer(final CustomerRequestDTO customerRequestDTO) {
        return Customer
                .builder()
                .id(UUID.randomUUID())
                .customerNumber(UUID.randomUUID())
                .email(customerRequestDTO.getEmail())
                .password(customerRequestDTO.getPassword())
                .firstName(customerRequestDTO.getFirstName())
                .lastName(customerRequestDTO.getLastName())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }
}
