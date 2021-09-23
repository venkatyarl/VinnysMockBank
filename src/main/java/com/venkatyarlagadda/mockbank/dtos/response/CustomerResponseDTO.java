package com.venkatyarlagadda.mockbank.dtos.response;

import com.venkatyarlagadda.mockbank.entity.Customer;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * @author Venkat Yarlagadda
 * @version V1
 */

@Builder
@Getter
@AllArgsConstructor
public class CustomerResponseDTO implements Serializable {
    public UUID customerNumber;
    private String email;
    private String firstName;
    private String lastName;
    private List<AccountResponseDTO> listOfAccounts;

    public static CustomerResponseDTO buildCustomerResponseDTO(final Customer customer,
                                                               final List<AccountResponseDTO> listOfAccounts){
        return CustomerResponseDTO
                .builder()
                .customerNumber(customer.getCustomerNumber())
                .email(customer.getEmail())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .listOfAccounts(listOfAccounts)
                .build();
    }
}