package com.venkatyarlagadda.mockbank.repositories;

import com.venkatyarlagadda.mockbank.entity.Account;
import com.venkatyarlagadda.mockbank.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Venkat Yarlagadda
 * @version V1
 */

@RestResource(exported = false)
public interface AccountRepository extends JpaRepository<Account, UUID> {
    List<Account> findByCustomer_CustomerNumber(final UUID customerNumber);
    Optional<Account> findAccountByAccountNumber(final UUID accountNumber);
    Optional<Account> findByAccountNumberAndCustomer_CustomerNumber(
            final UUID accountNumber,
            final UUID customer_customerNumber);
}
