package com.venkatyarlagadda.mockbank.repositories;

import com.venkatyarlagadda.mockbank.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Venkat Yarlagadda
 * @version V1
 */

@RestResource(exported = false)
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findCustomerByEmail(final String email);
    Optional<Customer> findCustomerByCustomerNumber(final UUID customerNumber);
}
